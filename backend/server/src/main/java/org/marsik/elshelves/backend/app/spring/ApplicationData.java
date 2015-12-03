package org.marsik.elshelves.backend.app.spring;

import net.spy.memcached.MemcachedClient;
import org.marsik.elshelves.backend.app.MemcacheCacheManager;
import org.marsik.elshelves.backend.entities.User;
import org.marsik.elshelves.backend.repositories.UserRepository;
import org.marsik.elshelves.backend.services.FileStorageManager;
import org.marsik.elshelves.backend.services.StorageManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.orm.jpa.EntityScan;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

@Configuration
@EnableJpaRepositories(basePackageClasses = UserRepository.class)
@EntityScan(basePackageClasses = User.class)
@EnableTransactionManagement
@EnableJpaAuditing
@EnableCaching
public class ApplicationData {
    private static final Logger log = LoggerFactory.getLogger(ApplicationData.class);

    @Value("${storage.path:/tmp/shelves/documents}")
    String documentPath;

    @Bean
    public StorageManager getStorageManager() {
        return new FileStorageManager(documentPath);
    }

    @Bean
    Validator beanValidation() {
        return new LocalValidatorFactoryBean();
    }

    @Bean
    CacheManager cacheManager(MemcachedClient memcachedClient) {
        return new MemcacheCacheManager(memcachedClient);
    }
}
