package org.marsik.elshelves.backend.test.unit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;

import javax.persistence.EntityManager;
import java.util.UUID;

import org.javamoney.moneta.Money;
import org.junit.Before;
import org.junit.Test;
import org.marsik.elshelves.backend.entities.Box;
import org.marsik.elshelves.backend.entities.IdentifiedEntity;
import org.marsik.elshelves.backend.entities.Lot;
import org.marsik.elshelves.backend.entities.OwnedEntity;
import org.marsik.elshelves.backend.entities.Purchase;
import org.marsik.elshelves.backend.entities.PurchasedLot;
import org.marsik.elshelves.backend.entities.Source;
import org.marsik.elshelves.backend.entities.Transaction;
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

    @Test
    public void testTransactionNoPreviousPurchaseDelivery() throws Exception {
        Type type = randomIdObj(Type.class);
        Box box = randomIdObj(Box.class);
        Source source = randomIdObj(Source.class);
        source.setName("SOURCE");

        Transaction transaction = randomIdObj(Transaction.class);

        alwaysReturn(typeRepository, type);
        alwaysReturn(boxRepository, box);
        alwaysReturn(sourceRepository, source);
        alwaysReturn(transactionRepository, transaction);

        Lot lot = service.deliverTypeInTransaction(transaction, type, box, 10L, null, user);
        assertThat(lot)
                .isNotNull()
                .isInstanceOf(PurchasedLot.class);

        assertThat(lot.getCount())
                .isNotNull()
                .isEqualTo(10L);

        assertThat(lot.isValid())
                .isTrue();

        assertThat(((PurchasedLot)lot).getPurchase())
                .isNotNull();

        assertThat(((PurchasedLot)lot).getPurchase().getTransaction())
                .isEqualTo(transaction);
    }

    @Test
    public void testTransactionOnePreviousPurchaseDelivery() throws Exception {
        Type type = randomIdObj(Type.class);
        Box box = randomIdObj(Box.class);
        Source source = randomIdObj(Source.class);
        source.setName("SOURCE");

        Transaction transaction = randomIdObj(Transaction.class);
        Purchase purchase = randomIdObj(Purchase.class);
        purchase.setTransaction(transaction);
        purchase.setType(type);

        alwaysReturn(typeRepository, type);
        alwaysReturn(boxRepository, box);
        alwaysReturn(sourceRepository, source);
        alwaysReturn(transactionRepository, transaction);
        alwaysReturn(purchaseRepository, purchase);

        Lot lot = service.deliverTypeInTransaction(transaction, type, box, 10L, null, user);
        assertThat(lot)
                .isNotNull()
                .isInstanceOf(PurchasedLot.class);

        assertThat(lot.getCount())
                .isNotNull()
                .isEqualTo(10L);

        assertThat(lot.isValid())
                .isTrue();

        assertThat(((PurchasedLot)lot).getPurchase())
                .isNotNull()
                .isEqualTo(purchase);

        assertThat(((PurchasedLot)lot).getPurchase().getTransaction())
                .isEqualTo(transaction);

        assertThat(((PurchasedLot)lot).getPurchase().getTransaction().getItems())
                .hasSize(1)
                .containsExactly(purchase);
    }

    @Test
    public void testTransactionTwoPreviousPurchasesDelivery() throws Exception {
        Type type = randomIdObj(Type.class);
        Box box = randomIdObj(Box.class);
        Source source = randomIdObj(Source.class);
        source.setName("SOURCE");

        Transaction transaction = randomIdObj(Transaction.class);
        Purchase purchase = randomIdObj(Purchase.class);
        purchase.setTransaction(transaction);
        purchase.setType(type);
        purchase.setCount(10L);
        purchase.setSinglePricePaid(Money.of(1, "CZK"));

        Purchase purchase2 = randomIdObj(Purchase.class);
        purchase2.setTransaction(transaction);
        purchase2.setType(type);
        purchase2.setCount(5L);
        purchase2.setSinglePricePaid(Money.of(2, "CZK"));

        alwaysReturn(typeRepository, type);
        alwaysReturn(boxRepository, box);
        alwaysReturn(sourceRepository, source);
        alwaysReturn(transactionRepository, transaction);

        doReturn(purchase).when(purchaseRepository).findById(purchase.getId());
        doReturn(purchase2).when(purchaseRepository).findById(purchase2.getId());

        Lot lot = service.deliverTypeInTransaction(transaction, type, box, 10L, null, user);
        assertThat(lot)
                .isNotNull()
                .isInstanceOf(PurchasedLot.class);

        assertThat(lot.getCount())
                .isNotNull()
                .isEqualTo(10L);

        assertThat(lot.isValid())
                .isTrue();

        assertThat(((PurchasedLot)lot).getPurchase())
                .isNotNull()
                .isEqualTo(purchase);

        assertThat(((PurchasedLot)lot).getPurchase().getTransaction())
                .isEqualTo(transaction);

        assertThat(((PurchasedLot)lot).getPurchase().getTransaction().getItems())
                .hasSize(2)
                .contains(purchase, purchase2);
    }

    @Test
    public void testTransactionTwoPreviousPurchasesDeliveryTooSmall() throws Exception {
        Type type = randomIdObj(Type.class);
        Box box = randomIdObj(Box.class);
        Source source = randomIdObj(Source.class);
        source.setName("SOURCE");

        Transaction transaction = randomIdObj(Transaction.class);
        Purchase purchase = randomIdObj(Purchase.class);
        purchase.setTransaction(transaction);
        purchase.setType(type);
        purchase.setCount(10L);
        purchase.setSinglePricePaid(Money.of(1, "CZK"));

        PurchasedLot lot0 = randomIdObj(PurchasedLot.class);
        lot0.setCount(5L);
        lot0.setPurchase(purchase);
        lot0.setType(type);
        lot0.setValid(true);

        Purchase purchase2 = randomIdObj(Purchase.class);
        purchase2.setTransaction(transaction);
        purchase2.setType(type);
        purchase2.setCount(5L);
        purchase2.setSinglePricePaid(Money.of(2, "CZK"));

        alwaysReturn(typeRepository, type);
        alwaysReturn(boxRepository, box);
        alwaysReturn(sourceRepository, source);
        alwaysReturn(transactionRepository, transaction);

        doReturn(purchase).when(purchaseRepository).findById(purchase.getId());
        doReturn(purchase2).when(purchaseRepository).findById(purchase2.getId());

        Lot lot = service.deliverTypeInTransaction(transaction, type, box, 10L, null, user);
        assertThat(lot)
                .isNotNull()
                .isInstanceOf(PurchasedLot.class);

        assertThat(lot.getCount())
                .isNotNull()
                .isEqualTo(10L);

        assertThat(lot.isValid())
                .isTrue();

        assertThat(((PurchasedLot)lot).getPurchase())
                .isNotNull()
                .isNotEqualTo(purchase2)
                .isNotEqualTo(purchase);

        assertThat(((PurchasedLot)lot).getPurchase().getTransaction())
                .isEqualTo(transaction);

        assertThat(((PurchasedLot)lot).getPurchase().getTransaction().getItems())
                .hasSize(3)
                .contains(purchase, purchase2, ((PurchasedLot) lot).getPurchase());
    }

    private <T extends IdentifiedEntity> T randomIdObj(Class<T> cls) throws IllegalAccessException, InstantiationException {
        T o = cls.newInstance();
        o.setId(UUID.randomUUID());
        return o;
    }

    private <T extends IdentifiedEntity> void alwaysReturn(BaseIdentifiedEntityRepository<T> repository, T value) {
        doReturn(value).when(repository).findById(any());
        value.setOwner(user);
    }
}
