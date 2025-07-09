package com.autoanalytics.finanzVisualisierung.job;

import com.autoanalytics.finanzVisualisierung.model.Finanzen;
import com.autoanalytics.finanzVisualisierung.model.Unternehmen;
import com.autoanalytics.finanzVisualisierung.processor.FinanzenItemProcessor;
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
public class CsvToDBFinanzen {

    public static final Logger logger = LoggerFactory.getLogger(CsvToDBFinanzen.class);
    private final JobRepository jobRepository;

    @Value("${local.input.file.path}") // see application.properties
    private String inputPath;

    /** tabelle unternehmen muss erstmal vorhanden sein. */
    private static final String INSERT_QUERY = """
      insert into finanzen (unternehmen_id, monat, produktion_Budget, produktion_Kosten, personal_Budget, personal_Kosten, \s
      IT_Budget, IT_Kosten, marketing_Budget, marketing_Kosten, logistik_Budget, logistik_Kosten, sonstiges_Budget,
      sonstiges_Kosten, budget_Gesamt, ausgaben_Gesamt, umsatz, gewinn)
      values ((SELECT unternehmen_id FROM unternehmen WHERE unternehmen = :unternehmen), :monat_, :produktion_Budget,\s
      :produktion_Kosten, :personal_Budget, :personal_Kosten, :IT_Budget, :IT_Kosten, :marketing_Budget, :marketing_Kosten,
      :logistik_Budget, :logistik_Kosten, :sonstiges_Budget, :sonstiges_Kosten, :budget_Gesamt, :ausgaben_Gesamt, :umsatz_, :gewinn)
            ON DUPLICATE KEY UPDATE
                produktion_Budget = VALUES(produktion_Budget),
                produktion_Kosten = VALUES(produktion_Kosten),
                personal_Budget = VALUES(personal_Budget),
                personal_Kosten = VALUES(personal_Kosten),
                IT_Budget = VALUES(IT_Budget),
                IT_Kosten = VALUES(IT_Kosten),
                marketing_Budget = VALUES(marketing_Budget),
                marketing_Kosten = VALUES(marketing_Kosten),
                logistik_Budget = VALUES(logistik_Budget),
                logistik_Kosten = VALUES(logistik_Kosten),
                sonstiges_Budget = VALUES(sonstiges_Budget),
                sonstiges_Kosten = VALUES(sonstiges_Kosten),
                budget_Gesamt = VALUES(budget_Gesamt),
                ausgaben_Gesamt = VALUES(ausgaben_Gesamt),
                umsatz = VALUES(umsatz),
                gewinn = VALUES(gewinn);
      """;

    public CsvToDBFinanzen(JobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }


    @Bean("readCSVFilesJob_Finanzen")
    public Job readCSVFilesJob_Finanzen(Step step1_Finanzen, Step step2_Finanzen) {
        var name = "readCSVFilesJob_Finanzen";
        var builder = new JobBuilder(name, jobRepository);

        return builder
                .incrementer(new RunIdIncrementer())
                .start(step1_Finanzen)
                 .next(step2_Finanzen)
                .build();
    }

    @Bean
    public Step step1_Finanzen(MultiResourceItemReader<Finanzen> multiResourceItemReader,
                      @Qualifier("DBWriterBean_finanzen") ItemWriter<Finanzen> writer,
                        ItemProcessor<Finanzen, Finanzen> finanzenItemProcessor,
                      PlatformTransactionManager txManager) {
        var name = "INSERT CSV RECORDS To DB Step";
        var builder = new StepBuilder(name, jobRepository);
        return builder
                .<Finanzen, Finanzen>chunk(500, txManager)
                .faultTolerant()
                .retryLimit(3).retry(DeadlockLoserDataAccessException.class)
                .reader(multiResourceItemReader)
                // .processor(finanzenItemProcessor)
                .writer(writer)
                .build();
    }


    public Resource[] getInputFiles(String fileName) {
        ResourcePatternResolver patternResolver = new PathMatchingResourcePatternResolver();
        List<Resource> matchingResources = new ArrayList<>();

        try {
            Resource[] allResources = patternResolver.getResources(inputPath);
            for (Resource resource : allResources) {
               // System.out.println("resources:  " + resource.getFilename() + "   -   ");
                if (resource.getFilename() != null && resource.getFilename().contains(fileName)) {
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
    public MultiResourceItemReader<Finanzen> multiResourceItemReader_Finanzen(FlatFileItemReader<Finanzen> reader) {
        MultiResourceItemReader<Finanzen> resourceItemReader = new MultiResourceItemReader<>();
        resourceItemReader.setResources(getInputFiles("finanzdaten"));
        resourceItemReader.setDelegate(reader);
        return resourceItemReader;
    }

    @Bean
    public FlatFileItemReader<Finanzen> reader_Finanzen() {
        //Create reader instance
        FlatFileItemReader<Finanzen> reader = new FlatFileItemReader<>();

        //Set the number of lines to skip. Use it if a file has header rows.
        reader.setLinesToSkip(1);

        //Configure how each line will be parsed and mapped to different values
        DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer();
        tokenizer.setDelimiter(",");
        tokenizer.setNames("Unternehmen", "Monat", "Produktion_Budget", "Produktion_Kosten", "Personal_Budget", "Personal_Kosten",
                "IT_Budget", "IT_Kosten", "Marketing_Budget", "Marketing_Kosten", "Logistik_Budget", "Logistik_Kosten",
                "Sonstiges_Budget","Sonstiges_Kosten", "Budget_Gesamt", "Ausgaben_Gesamt", "Umsatz", "Gewinn");

        BeanWrapperFieldSetMapper<Finanzen> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
        fieldSetMapper.setTargetType(Finanzen.class);

        DefaultLineMapper<Finanzen> lineMapper = new DefaultLineMapper<>();
        lineMapper.setLineTokenizer(tokenizer);
        lineMapper.setFieldSetMapper(fieldSetMapper);

        reader.setLineMapper(lineMapper);
        return reader;
    }


    @Bean
    public ItemWriter<Finanzen> loggingItemWriter_fin() {
        return items -> {
            for (Finanzen item : items) {
                // Log each item
                logger.info("Writing item: {}", item.toString());
            }
        };
    }


    @Bean
    public Step step2_Finanzen(PlatformTransactionManager txManager, DeleteInputCsvTasklet deleteCsvTasklet_Finanzen) {
        var name = "DELETE CSV FILE";
        var builder = new StepBuilder(name, jobRepository);
        return builder
                .tasklet(deleteCsvTasklet_Finanzen, txManager)
                .build();
    }


    @Bean
    @StepScope
    public DeleteInputCsvTasklet deleteCsvTasklet_Finanzen() {
        DeleteInputCsvTasklet tasklet = new DeleteInputCsvTasklet();
        Resource[] resources = getInputFiles("finanzdaten");
        System.out.println("aus deleteInputCsvTasklet_F: " + Arrays.toString(resources));
        tasklet.setResources(resources);
        return tasklet;
    }



    @Bean("DBWriterBean_finanzen")
    public JdbcBatchItemWriter<Finanzen> jdbcItemWriter(DataSource dataSource) {
        var provider = new BeanPropertyItemSqlParameterSourceProvider<Finanzen>();
        var itemWriter = new JdbcBatchItemWriter<Finanzen>();
        itemWriter.setDataSource(dataSource);
        itemWriter.setSql(INSERT_QUERY);
        itemWriter.setItemSqlParameterSourceProvider(provider);
        return itemWriter;
    }



    @Bean
    public FinanzenItemProcessor finanzenItemProcessor() {
        return new FinanzenItemProcessor();
    }

}