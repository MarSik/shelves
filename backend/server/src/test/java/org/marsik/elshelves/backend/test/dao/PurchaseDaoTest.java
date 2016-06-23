package org.marsik.elshelves.backend.test.dao;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.UUID;

import org.joda.time.DateTime;
import org.junit.Test;
import org.marsik.elshelves.api.entities.fields.LotAction;
import org.marsik.elshelves.backend.entities.LotHistory;
import org.marsik.elshelves.backend.entities.Purchase;
import org.marsik.elshelves.backend.entities.PurchasedLot;
import org.marsik.elshelves.backend.entities.Source;
import org.marsik.elshelves.backend.entities.Transaction;
import org.marsik.elshelves.backend.entities.Type;
import org.marsik.elshelves.backend.entities.User;
import org.marsik.elshelves.backend.repositories.LotHistoryRepository;
import org.marsik.elshelves.backend.repositories.LotRepository;
import org.marsik.elshelves.backend.repositories.PurchaseRepository;
import org.marsik.elshelves.backend.repositories.SourceRepository;
import org.marsik.elshelves.backend.repositories.TransactionRepository;
import org.marsik.elshelves.backend.repositories.TypeRepository;
import org.marsik.elshelves.backend.repositories.UserRepository;
import org.marsik.elshelves.backend.services.UuidGenerator;
import org.marsik.elshelves.backend.services.UuidGeneratorImpl;
import org.mockito.Spy;
import org.modelmapper.internal.util.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

public class PurchaseDaoTest extends BaseDaoTest {
    @Autowired
    PurchaseRepository purchaseRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    TypeRepository typeRepository;

    @Autowired
    TransactionRepository transactionRepository;

    @Autowired
    LotRepository lotRepository;

    @Autowired
    SourceRepository sourceRepository;

    @Autowired
    LotHistoryRepository lotHistoryRepository;

    @Spy
    UuidGenerator uuidGenerator = new UuidGeneratorImpl();

    @Test
    @Transactional
    @Rollback
    public void testExpectedDelivery() {
        UUID uuid = uuidGenerator.generate();
        User user = new User();
        user.setName("Test user");
        user.setEmail("user@test.com");
        user.setId(uuid);
        user.setCreated(new DateTime());
        user.setLastModified(new DateTime());

        userRepository.save(user);

        Type type = new Type();
        type.setName("Type");
        type.setId(uuidGenerator.generate());
        type.setOwner(user);

        typeRepository.save(type);

        Source source = new Source();
        source.setName("source");
        source.setId(uuidGenerator.generate());
        source.setOwner(user);

        sourceRepository.save(source);

        Transaction transaction = new Transaction();
        transaction.setId(uuidGenerator.generate());
        transaction.setName("transaction");
        transaction.setOwner(user);
        transaction.setSource(source);

        transactionRepository.save(transaction);

        Purchase purchase = new Purchase();
        purchase.setId(uuidGenerator.generate());
        purchase.setType(type);
        purchase.setCount(20L);
        purchase.setOwner(user);
        purchase.setTransaction(transaction);

        purchaseRepository.save(purchase);

        LotHistory lotHistory = new LotHistory();
        lotHistory.setId(uuidGenerator.generate());
        lotHistory.setValidSince(DateTime.now());
        lotHistory.setAction(LotAction.ASSIGNED);

        lotHistoryRepository.save(lotHistory);

        PurchasedLot lot = new PurchasedLot();
        lot.setId(uuidGenerator.generate());
        lot.setCount(10L);
        lot.setValid(true);
        lot.setType(type);
        lot.setPurchase(purchase);
        lot.setOwner(user);
        lot.setHistory(lotHistory);

        lotRepository.save(lot);

        Purchase purchase2 = new Purchase();
        purchase2.setId(uuidGenerator.generate());
        purchase2.setType(type);
        purchase2.setCount(20L);
        purchase2.setOwner(user);
        purchase2.setTransaction(transaction);

        purchaseRepository.save(purchase2);

        PurchasedLot lot2 = new PurchasedLot();
        lot2.setId(uuidGenerator.generate());
        lot2.setCount(20L);
        lot2.setValid(true);
        lot2.setType(type);
        lot2.setPurchase(purchase2);
        lot2.setOwner(user);
        lot2.setHistory(lotHistory);

        lotRepository.save(lot2);

        Purchase purchase3 = new Purchase();
        purchase3.setId(uuidGenerator.generate());
        purchase3.setType(type);
        purchase3.setCount(20L);
        purchase3.setOwner(user);
        purchase3.setTransaction(transaction);

        purchaseRepository.save(purchase3);

        List<UUID> ids = Lists.from(purchaseRepository.findUndelivered(user, type).iterator());
        assertThat(ids)
                .hasSize(2)
                .contains(purchase.getId(), purchase3.getId());
    }
}
