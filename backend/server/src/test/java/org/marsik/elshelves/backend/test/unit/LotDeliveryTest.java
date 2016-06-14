package org.marsik.elshelves.backend.test.unit;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;

import javax.persistence.EntityManager;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;
import org.marsik.elshelves.backend.entities.Box;
import org.marsik.elshelves.backend.entities.IdentifiedEntity;
import org.marsik.elshelves.backend.entities.OwnedEntity;
import org.marsik.elshelves.backend.entities.Source;
import org.marsik.elshelves.backend.entities.Type;
import org.marsik.elshelves.backend.entities.User;
import org.marsik.elshelves.backend.repositories.BaseIdentifiedEntityRepository;
import org.marsik.elshelves.backend.repositories.BaseOwnedEntityRepository;
import org.marsik.elshelves.backend.repositories.BoxRepository;
import org.marsik.elshelves.backend.repositories.LotRepository;
import org.marsik.elshelves.backend.repositories.PurchaseRepository;
import org.marsik.elshelves.backend.repositories.SourceRepository;
import org.marsik.elshelves.backend.repositories.TransactionRepository;
import org.marsik.elshelves.backend.repositories.TypeRepository;
import org.marsik.elshelves.backend.services.LotServiceImpl;
import org.marsik.elshelves.backend.services.UuidGenerator;
import org.marsik.elshelves.backend.services.UuidGeneratorImpl;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.springframework.data.repository.Repository;

public class LotDeliveryTest extends BaseUnitTest {
    @InjectMocks
    LotServiceImpl service;

    @Mock
    EntityManager entityManager;

    @Mock
    TypeRepository typeRepository;

    @Mock
    TransactionRepository transactionRepository;

    @Mock
    PurchaseRepository purchaseRepository;

    @Mock
    SourceRepository sourceRepository;

    @Mock
    LotRepository lotRepository;

    @Mock
    BoxRepository boxRepository;

    @Spy
    UuidGenerator uuidGenerator = new UuidGeneratorImpl();

    private User user;

    @Before
    public void setUp() throws Exception {
        user = randomIdObj(User.class);
    }

    @Test
    public void testTypeBasedDelivery() throws Exception {
        Type type = randomIdObj(Type.class);
        Box box = randomIdObj(Box.class);
        Source source = randomIdObj(Source.class);
        source.setName("SOURCE");

        alwaysReturn(typeRepository, type);
        alwaysReturn(boxRepository, box);
        alwaysReturn(sourceRepository, source);

        service.deliverAdHoc(source, type, box, 10L, null, user);
    }

    private <T extends IdentifiedEntity> T randomIdObj(Class<T> cls) throws IllegalAccessException, InstantiationException {
        T o = cls.newInstance();
        o.setId(UUID.randomUUID());
        return o;
    }

    private <T extends OwnedEntity> void alwaysReturn(BaseOwnedEntityRepository<T> repository, T value) {
        doReturn(value).when(repository).findById(any());
        value.setOwner(user);
    }
}
