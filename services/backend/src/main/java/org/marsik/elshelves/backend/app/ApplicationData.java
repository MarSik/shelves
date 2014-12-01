package org.marsik.elshelves.backend.app;

import org.marsik.elshelves.backend.app.spring.MyConversionServiceConverters;
import org.marsik.elshelves.backend.configuration.DatabaseConfiguration;
import org.marsik.elshelves.backend.configuration.SearchIndexConfiguration;
import org.marsik.elshelves.backend.configuration.StorageConfiguration;
import org.marsik.elshelves.backend.services.FileStorageManager;
import org.marsik.elshelves.backend.services.StorageManager;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.factory.GraphDatabaseBuilder;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.factory.GraphDatabaseSettings;
import org.neo4j.kernel.GraphDatabaseAPI;
import org.neo4j.server.WrappingNeoServerBootstrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AdviceMode;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.converter.ConverterRegistry;
import org.springframework.data.neo4j.aspects.config.Neo4jAspectConfiguration;
import org.springframework.data.neo4j.config.EnableNeo4jRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableNeo4jRepositories("org.marsik.elshelves.services.backend.repositories")
@EnableTransactionManagement(mode = AdviceMode.PROXY)
public class ApplicationData extends Neo4jAspectConfiguration {
    private static final Logger logger = LoggerFactory.getLogger(ApplicationData.class);

    public ApplicationData() {
        setBasePackage("org.marsik.elshelves.services.backend.entities");
    }

    @Bean
    DatabaseConfiguration getDbConfiguration() {
        return new DatabaseConfiguration();
    }

    @Autowired
    @Bean(destroyMethod = "shutdown")
    public GraphDatabaseService graphDatabaseService(DatabaseConfiguration dbConf) {
        GraphDatabaseBuilder graphDatabaseBuilder = new GraphDatabaseFactory().newEmbeddedDatabaseBuilder(dbConf.getDbPath());
        graphDatabaseBuilder.setConfig("remote_shell_enabled", "true");
        graphDatabaseBuilder.setConfig(GraphDatabaseSettings.allow_store_upgrade, "true");
        return graphDatabaseBuilder.newGraphDatabase();
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

    @Autowired
    @Bean(destroyMethod = "stop")
    public WrappingNeoServerBootstrapper serverWrapper(GraphDatabaseService db) {
        logger.info("Starting Neo4j server");
        WrappingNeoServerBootstrapper wrapper = new WrappingNeoServerBootstrapper((GraphDatabaseAPI)db);
        wrapper.start();
        return wrapper;
    }

    @Bean
    @Override
    public ConversionService neo4jConversionService() throws Exception {
        ConverterRegistry converterRegistry = (ConverterRegistry) super.neo4jConversionService();
        MyConversionServiceConverters.registerConverters(converterRegistry);
        return (ConversionService) converterRegistry;
    }
}
