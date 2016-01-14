package org.marsik.elshelves.backend.services;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.marsik.elshelves.backend.entities.Box;
import org.marsik.elshelves.backend.entities.Lot;
import org.marsik.elshelves.backend.entities.LotHistory;
import org.marsik.elshelves.backend.entities.User;
import org.marsik.elshelves.backend.interfaces.Relinker;
import org.marsik.elshelves.backend.repositories.IdentifiedEntityRepository;
import org.marsik.elshelves.backend.repositories.OwnedEntityRepository;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.doReturn;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class RelinkServiceTest {

    @InjectMocks
    RelinkServiceImpl relinkService;

    @Mock
    OwnedEntityRepository ownedEntityRepository;

    @Mock
    IdentifiedEntityRepository identifiedEntityRepository;

    @Spy
    UuidGenerator uuidGenerator = new UuidGeneratorImpl();

    @Configuration
    static class TestConfig {
    }

    @Before
    public void setUp() {
        // Init mocks
        MockitoAnnotations.initMocks(this);

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

        Relinker relinkContext = relinkService.newRelinker();
        relinkContext.addToCache(newUser);
        relinkContext.addToCache(oldUser.getId(), newUser);
        relinkContext.addToCache(lot);
        relinkContext.addToCache(lot.getHistory());

        lot.relink(relinkContext);
        lot.getHistory().relink(relinkContext);

        assertThat(lot.getHistory().getPerformedBy())
                .isNotNull()
                .isEqualTo(newUser);
        assertThat(lot.getHistory().getPerformedBy().getName())
                .isEqualTo(newUser.getName());
    }

    @Test
    public void testLotOwned() {
        User newUser = new User();
        newUser.setName("new user");
        newUser.setId(uuidGenerator.generate());
        newUser.setDbId(5L);

        Lot lot = new Lot();
        lot.setId(uuidGenerator.generate());
        lot.setHistory(new LotHistory());
        lot.getHistory().setPerformedBy(newUser);

        Box box = new Box();
        lot.setLocation(box);
        lot.getHistory().setLocation(box);

        Relinker relinkContext = relinkService.newRelinker();
        relinkContext.addToCache(newUser);

        relinkContext
                .addToCache(lot)
                .ensureOwner(lot, newUser)
                .addToCache(lot.getHistory())
                .addToCache(box)
                .ensureOwner(box, newUser);

        lot.relink(relinkContext);
        lot.getHistory().relink(relinkContext);
        box.relink(relinkContext);

        assertThat(lot.getOwner())
                .isNotNull()
                .isEqualTo(newUser);
        assertThat(lot.getHistory().getPerformedBy().getName())
                .isEqualTo(newUser.getName());

        assertThat(box.getId())
                .isNotNull();
        assertThat(lot.getHistory().getLocation())
                .isNotNull()
                .isEqualTo(box);

        assertThat(box.getOwner())
                .isNotNull()
                .isEqualTo(newUser);
    }
}
