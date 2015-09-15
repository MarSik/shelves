package org.marsik.elshelves.backend.app;

import org.marsik.elshelves.backend.configuration.DatabaseConfiguration;
import org.marsik.elshelves.backend.configuration.SearchIndexConfiguration;
import org.marsik.elshelves.backend.configuration.StorageConfiguration;
import org.marsik.elshelves.backend.services.FileStorageManager;
import org.marsik.elshelves.backend.services.StorageManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

@Configuration
@EnableJpaRepositories("org.marsik.elshelves.backend.repositories")
@EnableTransactionManagement
public class ApplicationData {
    private static final Logger logger = LoggerFactory.getLogger(ApplicationData.class);

    /*public ApplicationData() {
        setBasePackage("org.marsik.elshelves.backend.entities");
    }*/

    @Bean
    DatabaseConfiguration getDbConfiguration() {
        return new DatabaseConfiguration();
    }

    @Bean
    public SearchIndexConfiguration getStreetDbConfig() {
        return new SearchIndexConfiguration();
    }

    @Bean
    public StorageConfiguration getStorageConfiguration() {
        return new StorageConfiguration();
    }

    @Bean
    public StorageManager getStorageManager() {
        return new FileStorageManager(getStorageConfiguration());
    }

    @Bean
    Validator beanValidation() {
        return new LocalValidatorFactoryBean();
    }
}
