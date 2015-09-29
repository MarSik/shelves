package org.marsik.elshelves.backend.services;

import gnu.trove.map.hash.THashMap;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.marsik.elshelves.backend.entities.IdentifiedEntityInterface;
import org.marsik.elshelves.backend.entities.Lot;
import org.marsik.elshelves.backend.entities.LotHistory;
import org.marsik.elshelves.backend.entities.OwnedEntity;
import org.marsik.elshelves.backend.entities.User;
import org.marsik.elshelves.backend.repositories.IdentifiedEntityRepository;
import org.marsik.elshelves.backend.repositories.OwnedEntityRepository;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

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

    @Mock
    IdentifiedEntityRepository identifiedEntityRepository;

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

        relinkService.uuidGenerator = uuidGenerator;

        // Simulate empty database
        doReturn(null).when(ownedEntityRepository).findById(any(UUID.class));
        doReturn(null).when(identifiedEntityRepository).findById(any(UUID.class));
    }

    @Test
    public void testLotPerformedBy() {
        User oldUser = new User();
        oldUser.setName("old user");
        oldUser.setId(uuidGenerator.generate());

        User newUser = new User();
        newUser.setName("new user");
        newUser.setId(uuidGenerator.generate());
        newUser.setDbId(5L);

        Lot lot = new Lot();
        lot.setId(uuidGenerator.generate());
        lot.setHistory(new LotHistory());
        lot.getHistory().setPerformedBy(oldUser);
        lot.setOwner(newUser);

        Map<UUID, IdentifiedEntityInterface> relinkCache = new THashMap<>();
        relinkCache.put(newUser.getId(), newUser);
        relinkCache.put(oldUser.getId(), newUser);

        relinkService.relink(lot, newUser, relinkCache, true);

        assertThat(lot.getHistory().getPerformedBy())
                .isNotNull()
                .isEqualTo(newUser);
        assertThat(lot.getHistory().getPerformedBy().getName())
                .isEqualTo(newUser.getName());
    }
}
