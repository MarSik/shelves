package org.marsik.elshelves.backend.services;

import gnu.trove.map.hash.THashMap;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.marsik.elshelves.backend.app.ApplicationLauncher;
import org.marsik.elshelves.backend.entities.Lot;
import org.marsik.elshelves.backend.entities.LotHistory;
import org.marsik.elshelves.backend.entities.OwnedEntity;
import org.marsik.elshelves.backend.entities.User;
import org.marsik.elshelves.backend.repositories.OwnedEntityRepository;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.HashSet;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class RelinkServiceTest {

    @InjectMocks
    RelinkService relinkService;

    @Mock
    OwnedEntityRepository ownedEntityRepository;

    @Configuration
    static class TestConfig {
        @Bean
        public UuidGenerator uuidGenerator() {
            return new UuidGeneratorImpl();
        }
    }

    @Autowired
    UuidGenerator uuidGenerator;

    @Before
    public void setUp() {
        // Init mocks
        MockitoAnnotations.initMocks(this);

        // Simulate empty database
        doReturn(null).when(ownedEntityRepository).findByUuid(any(UUID.class));
    }

    @Test
    public void testLotPerformedBy() {
        User oldUser = new User();
        oldUser.setName("old user");
        oldUser.setUuid(uuidGenerator.generate());

        User newUser = new User();
        newUser.setName("new user");
        newUser.setUuid(uuidGenerator.generate());

        Lot lot = new Lot();
        lot.setUuid(uuidGenerator.generate());
        lot.setHistory(new LotHistory());
        lot.getHistory().setPerformedBy(oldUser);
        lot.setOwner(newUser);

        Map<UUID, OwnedEntity> relinkCache = new THashMap<>();
        relinkCache.put(newUser.getUuid(), newUser);
        relinkCache.put(oldUser.getUuid(), newUser);

        relinkService.relink(lot, newUser, relinkCache, false);

        assertThat(lot.getHistory().getPerformedBy())
                .isNotNull()
                .isEqualTo(newUser);
        assertThat(lot.getHistory().getPerformedBy().getName())
                .isEqualTo(newUser.getName());
    }
}
