package org.marsik.elshelves.backend.test.config;

import org.marsik.elshelves.backend.app.spring.ApplicationRest;
import org.marsik.elshelves.backend.app.spring.NoAutoscan;
import org.marsik.elshelves.backend.controllers.UserController;
import org.marsik.elshelves.backend.entities.converters.AbstractEmberToEntity;
import org.marsik.elshelves.backend.repositories.ItemRepository;
import org.marsik.elshelves.backend.repositories.LotHistoryRepository;
import org.marsik.elshelves.backend.repositories.LotRepository;
import org.marsik.elshelves.backend.repositories.NamedEntityRepository;
import org.marsik.elshelves.backend.repositories.OwnedEntityRepository;
import org.marsik.elshelves.backend.repositories.TestRepository;
import org.marsik.elshelves.backend.services.AuthorizationService;
import org.marsik.elshelves.backend.services.BackupService;
import org.marsik.elshelves.backend.services.BoxService;
import org.marsik.elshelves.backend.services.CodeService;
import org.marsik.elshelves.backend.services.DocumentService;
import org.marsik.elshelves.backend.services.ElshelvesUserDetailsService;
import org.marsik.elshelves.backend.services.FileAnalysisDoneHandler;
import org.marsik.elshelves.backend.services.FootprintService;
import org.marsik.elshelves.backend.services.GoogleOauthService;
import org.marsik.elshelves.backend.services.GroupService;
import org.marsik.elshelves.backend.services.ItemService;
import org.marsik.elshelves.backend.services.ListService;
import org.marsik.elshelves.backend.services.LotService;
import org.marsik.elshelves.backend.services.MailService;
import org.marsik.elshelves.backend.services.NumericPropertyService;
import org.marsik.elshelves.backend.services.PurchaseService;
import org.marsik.elshelves.backend.services.RequirementService;
import org.marsik.elshelves.backend.services.SearchService;
import org.marsik.elshelves.backend.services.SourceService;
import org.marsik.elshelves.backend.services.StickerService;
import org.marsik.elshelves.backend.services.StorageManager;
import org.marsik.elshelves.backend.services.TransactionService;
import org.marsik.elshelves.backend.services.TypeService;
import org.marsik.elshelves.backend.services.UnitService;
import org.marsik.elshelves.backend.services.UserService;
import org.marsik.elshelves.backend.services.UuidGenerator;
import org.marsik.elshelves.backend.services.UuidGeneratorImpl;
import org.marsik.elshelves.backend.test.util.MockitoFactoryBean;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

@SpringBootApplication
@ComponentScan(basePackageClasses = { UserController.class, AbstractEmberToEntity.class },
        excludeFilters = { @ComponentScan.Filter(classes = NoAutoscan.class) })
@Configuration
@NoAutoscan
public class ControllerOnlyApplication {
    @Bean
    public MockitoFactoryBean<AuthorizationService> authorizationServiceMockitoFactoryBean() {
        return new MockitoFactoryBean<>(AuthorizationService.class);
    }

    @Bean
    public MockitoFactoryBean<BackupService> backupServiceMockitoFactoryBean() {
        return new MockitoFactoryBean<>(BackupService.class);
    }

    @Bean
    public MockitoFactoryBean<BoxService> boxServiceMockitoFactoryBean() {
        return new MockitoFactoryBean<>(BoxService.class);
    }

    @Bean
    public MockitoFactoryBean<CodeService> codeServiceMockitoFactoryBean() {
        return new MockitoFactoryBean<>(CodeService.class);
    }

    @Bean
    public MockitoFactoryBean<DocumentService> documentServiceMockitoFactoryBean() {
        return new MockitoFactoryBean<>(DocumentService.class);
    }

    @Bean
    public MockitoFactoryBean<ElshelvesUserDetailsService> elshelvesUserDetailsServiceMockitoFactoryBean() {
        return new MockitoFactoryBean<>(ElshelvesUserDetailsService.class);
    }

    @Bean
    public MockitoFactoryBean<FileAnalysisDoneHandler> fileAnalysisDoneHandlerMockitoFactoryBean() {
        return new MockitoFactoryBean<>(FileAnalysisDoneHandler.class);
    }

    @Bean
    public MockitoFactoryBean<FootprintService> footprintServiceMockitoFactoryBean() {
        return new MockitoFactoryBean<>(FootprintService.class);
    }

    @Bean
    public MockitoFactoryBean<GroupService> groupServiceMockitoFactoryBean() {
        return new MockitoFactoryBean<>(GroupService.class);
    }

    @Bean
    public MockitoFactoryBean<ItemRepository> itemRepositoryMockitoFactoryBean() {
        return new MockitoFactoryBean<>(ItemRepository.class);
    }

    @Bean
    public MockitoFactoryBean<ItemService> itemServiceMockitoFactoryBean() {
        return new MockitoFactoryBean<>(ItemService.class);
    }

    @Bean
    public MockitoFactoryBean<ListService> listServiceMockitoFactoryBean() {
        return new MockitoFactoryBean<>(ListService.class);
    }

    @Bean
    public MockitoFactoryBean<LotHistoryRepository> lotHistoryRepositoryMockitoFactoryBean() {
        return new MockitoFactoryBean<>(LotHistoryRepository.class);
    }

    @Bean
    public MockitoFactoryBean<LotRepository> lotRepositoryMockitoFactoryBean() {
        return new MockitoFactoryBean<>(LotRepository.class);
    }

    @Bean
    public MockitoFactoryBean<LotService> lotServiceMockitoFactoryBean() {
        return new MockitoFactoryBean<>(LotService.class);
    }

    @Bean
    public MockitoFactoryBean<MailService> mailgunServiceMockitoFactoryBean() {
        return new MockitoFactoryBean<>(MailService.class);
    }

    @Bean
    public MockitoFactoryBean<NamedEntityRepository> namedEntityRepositoryMockitoFactoryBean() {
        return new MockitoFactoryBean<>(NamedEntityRepository.class);
    }

    @Bean
    public MockitoFactoryBean<NumericPropertyService> numericPropertyServiceMockitoFactoryBean() {
        return new MockitoFactoryBean<>(NumericPropertyService.class);
    }

    @Bean
    public MockitoFactoryBean<OwnedEntityRepository> ownedEntityRepositoryMockitoFactoryBean() {
        return new MockitoFactoryBean<>(OwnedEntityRepository.class);
    }

    @Bean
    public MockitoFactoryBean<PurchaseService> purchaseServiceMockitoFactoryBean() {
        return new MockitoFactoryBean<>(PurchaseService.class);
    }

    @Bean
    public MockitoFactoryBean<RequirementService> requirementServiceMockitoFactoryBean() {
        return new MockitoFactoryBean<>(RequirementService.class);
    }

    @Bean
    public MockitoFactoryBean<SearchService> searchServiceMockitoFactoryBean() {
        return new MockitoFactoryBean<>(SearchService.class);
    }

    @Bean
    public MockitoFactoryBean<SourceService> sourceServiceMockitoFactoryBean() {
        return new MockitoFactoryBean<>(SourceService.class);
    }

    @Bean
    public MockitoFactoryBean<StickerService> stickerServiceMockitoFactoryBean() {
        return new MockitoFactoryBean<>(StickerService.class);
    }

    @Bean
    public MockitoFactoryBean<StorageManager> storageManagerMockitoFactoryBean() {
        return new MockitoFactoryBean<>(StorageManager.class);
    }

    @Bean
    public MockitoFactoryBean<TestRepository> testRepositoryMockitoFactoryBean() {
        return new MockitoFactoryBean<>(TestRepository.class);
    }

    @Bean
    public MockitoFactoryBean<TransactionService> transactionServiceMockitoFactoryBean() {
        return new MockitoFactoryBean<>(TransactionService.class);
    }

    @Bean
    public MockitoFactoryBean<TypeService> typeServiceMockitoFactoryBean() {
        return new MockitoFactoryBean<>(TypeService.class);
    }

    @Bean
    public MockitoFactoryBean<UnitService> unitServiceMockitoFactoryBean() {
        return new MockitoFactoryBean<>(UnitService.class);
    }

    @Bean
    public MockitoFactoryBean<UserService> userServiceMockitoFactoryBean() {
        return new MockitoFactoryBean<>(UserService.class);
    }

    @Bean
    public UuidGenerator uuidGenerator() {
        return new UuidGeneratorImpl();
    }

    @Bean
    MappingJackson2HttpMessageConverter emberJackson2HttpMessageConverter() {
        return ApplicationRest.getJacksonConverter();
    }

    @Bean
    public MockitoFactoryBean<GoogleOauthService> googleOauthService() {
        return new MockitoFactoryBean<>(GoogleOauthService.class);
    }
}
