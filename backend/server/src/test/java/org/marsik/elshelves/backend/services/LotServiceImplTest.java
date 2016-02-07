package org.marsik.elshelves.backend.services;

import gnu.trove.map.hash.THashMap;
import gnu.trove.set.hash.THashSet;
import org.junit.Before;
import org.junit.Test;
import org.marsik.elshelves.api.entities.fields.LotAction;
import org.marsik.elshelves.backend.entities.Box;
import org.marsik.elshelves.backend.entities.Lot;
import org.marsik.elshelves.backend.entities.MixedLot;
import org.marsik.elshelves.backend.entities.Requirement;
import org.marsik.elshelves.backend.entities.Type;
import org.marsik.elshelves.backend.repositories.BoxRepository;
import org.marsik.elshelves.backend.repositories.LotRepository;
import org.marsik.elshelves.backend.repositories.PurchaseRepository;
import org.marsik.elshelves.backend.test.unit.BaseUnitTest;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;

import javax.persistence.EntityManager;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

import static org.assertj.core.api.Assertions.assertThat;

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
        Lot result = service.lotMixer(l2, new THashMap<>());

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
    public void testLotWithSerial() throws Exception {
        Lot l1 = getLot(Lot.class, type, 5);
        box.addLot(l1);

        Lot l2 = getLot(Lot.class, type, 7);
        l2.setSerials(new THashSet<>());
        l2.getSerials().add("SERIAL");
        box.addLot(l2);

        Lot result = service.lotMixer(l2, new THashMap<>());
        Lot newL1 = service.lotMixer(l1, new THashMap<>());


        assertThat(result)
                .isNotNull()
                .isInstanceOf(Lot.class)
                .isEqualTo(l2);

        assertThat(newL1)
                .isNotNull()
                .isInstanceOf(Lot.class)
                .isEqualTo(l1);

        assertThat(result.getCount())
                .isEqualTo(l2.getCount());

        assertThat(l1.isValid()).isTrue();
        assertThat(l2.isValid()).isTrue();

        assertThat(box.getLots())
                .hasSize(2)
                .contains(l1, l2);
    }

    @Test
    public void testLotMixerAlreadyMixedAndAssignment() throws Exception {
        Lot l1 = getLot(MixedLot.class, type, 5);
        box.addLot(l1);

        Lot l2 = getLot(Lot.class, type, 7);
        box.addLot(l2);

        Requirement req = new Requirement();
        req.setId(uuidGenerator.generate());
        req.setDbId(dbId.incrementAndGet());

        Lot l3 = getLot(Lot.class, type, 9);
        l3.setUsedBy(req);
        box.addLot(l3);

        Map<UUID, Lot> cache = new THashMap<>();
        Lot result = service.lotMixer(l2, cache);
        Lot assigned = service.lotMixer(l3, cache);

        assertThat(result)
                .isNotNull()
                .isInstanceOf(MixedLot.class)
                .isNotEqualTo(l2)
                .isNotEqualTo(l3)
                .isEqualTo(l1);

        assertThat(((MixedLot) result).getParents())
                .isNotNull()
                .hasSize(2)
                .contains(l2, l3);

        assertThat(result.getCount())
                .isEqualTo(12);

        assertThat(assigned)
                .isNotNull()
                .isInstanceOf(MixedLot.class)
                .isNotEqualTo(l1)
                .isNotEqualTo(l3)
                .isNotEqualTo(l2);

        assertThat(assigned.getCount())
                .isEqualTo(9);

        assertThat(assigned.getUsedBy())
                .isEqualTo(req);

        assertThat(assigned.getStatus())
                .isEqualTo(LotAction.DELIVERY);

        assertThat(l1.getStatus()).isEqualTo(LotAction.DELIVERY);
        assertThat(l1.isValid()).isTrue();

        assertThat(l2.getStatus()).isEqualTo(LotAction.MIXED);
        assertThat(l2.isValid()).isFalse();

        assertThat(l3.getUsedBy()).isNull();
        assertThat(l3.getStatus()).isEqualTo(LotAction.MIXED);
        assertThat(l3.isValid()).isFalse();

        assertThat(box.getLots())
                .hasSize(2)
                .contains(result, assigned);
    }

    @Test
    public void testLotMixerAlreadyMixedExists() throws Exception {
        Lot l1 = getLot(MixedLot.class, type, 5);
        box.addLot(l1);

        Lot l2 = getLot(Lot.class, type, 7);
        box.addLot(l2);
        Lot result = service.lotMixer(l2, new THashMap<>());

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

    @Test
    public void testSiblingUnassigned() throws Exception {
        Lot l1a = getLot(Lot.class, type, 5);
        Lot l1b = getLot(Lot.class, type, 5);
        box.addLot(l1a);
        box.addLot(l1b);
        Lot l1 = service.lotMixer(l1a, new THashMap<>());
        assertThat(box.getLots()).containsExactly(l1);

        Lot l2 = (Lot)l1.shallowClone();
        l2.setId(uuidGenerator.generate());
        l2.setDbId(dbId.incrementAndGet());
        box.addLot(l2);
        Lot result = service.lotMixer(l2, new THashMap<>());

        assertThat(result)
                .isNotNull()
                .isInstanceOf(MixedLot.class);

        assertThat(((MixedLot) result).getParents())
                .isNotNull()
                .hasSize(3)
                .contains(l1a, l1b);

        assertThat(result.getCount())
                .isEqualTo(20);

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
