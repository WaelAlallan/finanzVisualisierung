package com.autoanalytics.finanzVisualisierung.job;

import com.autoanalytics.finanzVisualisierung.model.Unternehmen;
import com.autoanalytics.finanzVisualisierung.processor.UnternehmenItemProcessor;
import com.autoanalytics.finanzVisualisierung.tasklets.DeleteInputCsvTasklet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.MultiResourceItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.dao.DeadlockLoserDataAccessException;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Configuration
@EnableBatchProcessing
public class CsvToDBUnternehmen {

    public static final Logger logger = LoggerFactory.getLogger(CsvToDBUnternehmen.class);
    private final JobRepository jobRepository;

    @Value("${local.input.file.path}")    // see application.properties
    private String inputPath;

    private static final String INSERT_QUERY = """
      insert into unternehmen (unternehmen, gründung, sitz, mitarbeiterzahl, fabrikanzahl, produktionskapazität, logistikpartner, frauenanteil_in_Führung, co2_Bilanz)
      values (:unternehmen, :gründung, :sitz, :mitarbeiterzahl, :fabrikanzahl, :produktionskapazität, :logistikpartner, :frauenanteil_in_Führung, :co2Bilanz)
      ON DUPLICATE KEY UPDATE
            unternehmen = VALUES(unternehmen),
            gründung = VALUES(gründung),
            sitz = VALUES(sitz),
            mitarbeiterzahl = VALUES(mitarbeiterzahl),
            fabrikanzahl = VALUES(fabrikanzahl),
            produktionskapazität = VALUES(produktionskapazität),
            logistikpartner = VALUES(logistikpartner),
            frauenanteil_in_Führung = VALUES(frauenanteil_in_Führung),
            co2_Bilanz = VALUES(co2_Bilanz)
      """;

    public CsvToDBUnternehmen(JobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }



    @Bean("readCSVFilesJob")
    public Job readCSVFilesJob(Step step1, Step step2) {
        var name = "read CSV Files Job";
        var builder = new JobBuilder(name, jobRepository);

        return builder
                .incrementer(new RunIdIncrementer())
                .start(step1)
                .next(step2)
                .build();
    }

    @Bean
    public Step step1(MultiResourceItemReader<Unternehmen> multiResourceItemReader,
                      @Qualifier("DBWriterBean") ItemWriter<Unternehmen> writer,
                      //ItemProcessor<Unternehmen, Unternehmen> unternehmenItemProcessor,
                      PlatformTransactionManager txManager) {
        var name = "INSERT CSV RECORDS To DB Step";
        var builder = new StepBuilder(name, jobRepository);
        return builder
            .<Unternehmen, Unternehmen>chunk(500, txManager)
            .faultTolerant()
            .retryLimit(3).retry(DeadlockLoserDataAccessException.class)
            .reader(multiResourceItemReader)
            //.processor(unternehmenItemProcessor)
            .writer(writer)
            .build();
    }


    public Resource[] getInputFiles(String fileName) {
        ResourcePatternResolver patternResolver = new PathMatchingResourcePatternResolver();
        List<Resource> matchingResources = new ArrayList<>();

        try {
            Resource[] allResources = patternResolver.getResources(inputPath ); //  *.csv

            for (Resource resource : allResources) {
               // System.out.println("resources:  " + resource.getFilename() + "   -   ");
                if (resource.getFilename() != null && resource.getFilename().contains(fileName)) {
                    System.out.println("Matching file: " + resource.getFilename());
                    matchingResources.add(resource);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return matchingResources.toArray(new Resource[0]);
    }



    @Bean
    @StepScope
    public MultiResourceItemReader<Unternehmen> multiResourceItemReader(FlatFileItemReader<Unternehmen> reader) {
        MultiResourceItemReader<Unternehmen> resourceItemReader = new MultiResourceItemReader<>();
        resourceItemReader.setResources(getInputFiles("unternehmensdaten"));      // inputResources
        resourceItemReader.setDelegate(reader);
        return resourceItemReader;
    }

    @Bean
    public FlatFileItemReader<Unternehmen> reader() {
        //Create reader instance
        FlatFileItemReader<Unternehmen> reader = new FlatFileItemReader<>();

        //Set the number of lines to skip. Use it if a file has header rows.
        reader.setLinesToSkip(1);

        //Configure how each line will be parsed and mapped to different values
        DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer();
        tokenizer.setDelimiter(",");
        tokenizer.setNames("Unternehmen", "Gründung", "Sitz", "Mitarbeiterzahl", "Fabrikanzahl",
                "Produktionskapazität", "Logistikpartner", "Frauenanteil_in_Führung", "CO2-Bilanz");

        BeanWrapperFieldSetMapper<Unternehmen> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
        fieldSetMapper.setTargetType(Unternehmen.class);

        DefaultLineMapper<Unternehmen> lineMapper = new DefaultLineMapper<>();
        lineMapper.setLineTokenizer(tokenizer);
        lineMapper.setFieldSetMapper(fieldSetMapper);

        reader.setLineMapper(lineMapper);
        return reader;
    }


    @Bean
    public ItemWriter<Unternehmen> loggingItemWriter() {
        return items -> {
            for (Unternehmen item : items) {
                // Log each item
                logger.info("Writing item: {}", item.toString());
            }
        };
    }


    @Bean
    public Step step2(PlatformTransactionManager txManager, DeleteInputCsvTasklet deleteCsvTasklet) {
        var name = "DELETE CSV FILE";
        var builder = new StepBuilder(name, jobRepository);
        return builder
                .tasklet(deleteCsvTasklet, txManager)
                .build();
    }


    @Bean
    @StepScope
    public DeleteInputCsvTasklet deleteCsvTasklet() {
        DeleteInputCsvTasklet tasklet = new DeleteInputCsvTasklet();
        Resource[] resources = getInputFiles("unternehmensdaten");
        // System.out.println("aus deleteInputCsvTasklet: " + Arrays.toString(resources));
        tasklet.setResources(resources);
        return tasklet;
    }


    @Bean("DBWriterBean")
    @StepScope
    public JdbcBatchItemWriter<Unternehmen> jdbcItemWriter(DataSource dataSource) {
        var provider = new BeanPropertyItemSqlParameterSourceProvider<Unternehmen>();
        var itemWriter = new JdbcBatchItemWriter<Unternehmen>();
        itemWriter.setDataSource(dataSource);
        itemWriter.setSql(INSERT_QUERY);
        itemWriter.setItemSqlParameterSourceProvider(provider);
        return itemWriter;
    }



    @Bean
    public UnternehmenItemProcessor unternehmenItemProcessor() {
        return new UnternehmenItemProcessor();
    }
}