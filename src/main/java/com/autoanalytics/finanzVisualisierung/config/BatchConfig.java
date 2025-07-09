package com.autoanalytics.finanzVisualisierung.config;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

import javax.sql.DataSource;


// Use @EnableBatchProcessing annotation on a @Configuration class to enable the batch
// processing and execute its autoconfiguration with Spring Boot. This will bootstrap
// spring Batch with some sensible defaults. The default configuration will configure
// a JobRepository, JobRegistry, and JobLauncher beans.
@Configuration
@EnableBatchProcessing
public class BatchConfig {

    DataSource dataSource;

    public BatchConfig(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Bean
    public DataSourceTransactionManager transactionManager() {
        return new DataSourceTransactionManager(dataSource);
    }


    // Spring batch distribution comes with schema files for all popular databases.
    // We only need to refer it when initializing the DataSource using the DataSourceInitializer bean.
    @Bean
    public DataSourceInitializer databasePopulator() {
        ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
      //  populator.addScript(new ClassPathResource("org/springframework/batch/core/schema-mysql.sql"));     // org/springframework/batch/core/schema-h2.sql
        populator.addScript(new ClassPathResource("sql/batch-schema.sql"));
        populator.setContinueOnError(false);
        populator.setIgnoreFailedDrops(false);
        DataSourceInitializer dataSourceInitializer = new DataSourceInitializer();
        dataSourceInitializer.setDataSource(dataSource);
        dataSourceInitializer.setDatabasePopulator(populator);
        return dataSourceInitializer;
    }



}

