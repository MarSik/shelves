package org.marsik.elshelves.backend.services;

import gnu.trove.set.hash.THashSet;
import org.junit.Before;
import org.junit.Test;
import org.marsik.elshelves.api.entities.fields.LotAction;
import org.marsik.elshelves.backend.entities.Box;
import org.marsik.elshelves.backend.entities.Lot;
import org.marsik.elshelves.backend.entities.MixedLot;
import org.marsik.elshelves.backend.entities.Purchase;
import org.marsik.elshelves.backend.entities.Type;
import org.marsik.elshelves.backend.repositories.BoxRepository;
import org.marsik.elshelves.backend.repositories.LotRepository;
import org.marsik.elshelves.backend.repositories.PurchaseRepository;
import org.marsik.elshelves.backend.test.unit.BaseUnitTest;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;

import javax.persistence.EntityManager;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

public class LotServiceImplTest extends BaseUnitTest {
    @InjectMocks
    LotServiceImpl service;

    @Mock
    LotRepository lotRepository;

    @Mock
    PurchaseRepository purchaseRepository;

    @Mock
    BoxRepository boxRepository;

    @Mock
    EntityManager entityManager;

    @Spy
    UuidGenerator uuidGenerator = new UuidGeneratorImpl();

    Box box;
    Type type;
    AtomicLong dbId = new AtomicLong();

    @Before
    public void setUp() {
        box = new Box();
        box.setId(uuidGenerator.generate());
        box.setLots(new THashSet<>());
        box.setDbId(dbId.incrementAndGet());

        type = new Type();
        type.setId(uuidGenerator.generate());
        type.setLots(new THashSet<>());
        type.setDbId(dbId.incrementAndGet());
    }

    @Test
    public void testLotMixerBase() throws Exception {
        Lot l1 = getLot(Lot.class, type, 5);
        box.addLot(l1);

        Lot l2 = getLot(Lot.class, type, 7);
        box.addLot(l2);
        Lot result = service.lotMixer(l2);

        assertThat(result)
                .isNotNull()
                .isInstanceOf(MixedLot.class)
                .isNotEqualTo(l2)
                .isNotEqualTo(l1);

        assertThat(((MixedLot) result).getParents())
                .isNotNull()
                .hasSize(2)
                .contains(l1, l2);

        assertThat(result.getCount())
                .isEqualTo(12);

        assertThat(l1.getStatus()).isEqualTo(LotAction.MIXED);
        assertThat(l1.isValid()).isFalse();

        assertThat(l2.getStatus()).isEqualTo(LotAction.MIXED);
        assertThat(l2.isValid()).isFalse();

        assertThat(box.getLots()).containsExactly(result);
    }

    @Test
    public void testLotMixerAlreadyMixedExists() throws Exception {
        Lot l1 = getLot(MixedLot.class, type, 5);
        box.addLot(l1);

        Lot l2 = getLot(Lot.class, type, 7);
        box.addLot(l2);
        Lot result = service.lotMixer(l2);

        assertThat(result)
                .isNotNull()
                .isInstanceOf(MixedLot.class)
                .isNotEqualTo(l2)
                .isEqualTo(l1);

        assertThat(((MixedLot) result).getParents())
                .isNotNull()
                .containsExactly(l2);

        assertThat(result.getCount())
                .isEqualTo(12);

        assertThat(l1.getStatus()).isEqualTo(LotAction.DELIVERY);
        assertThat(l1.isValid()).isTrue();

        assertThat(l2.getStatus()).isEqualTo(LotAction.MIXED);
        assertThat(l2.isValid()).isFalse();

        assertThat(box.getLots()).containsExactly(result);
    }

    private <T extends Lot> T getLot(Class<T> cls, Type t, long count) throws IllegalAccessException, InstantiationException {
        T l = cls.newInstance();
        l.setId(uuidGenerator.generate());
        l.setType(type);
        l.setCount(count);
        l.setDbId(dbId.incrementAndGet());
        l.setStatus(LotAction.DELIVERY);
        return l;
    }
}
