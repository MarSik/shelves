package org.marsik.elshelves.backend.services;

import gnu.trove.map.hash.THashMap;
import gnu.trove.set.hash.THashSet;
import org.junit.Before;
import org.junit.Test;
import org.marsik.elshelves.api.entities.fields.LotAction;
import org.marsik.elshelves.backend.dtos.LotSplitResult;
import org.marsik.elshelves.backend.entities.Box;
import org.marsik.elshelves.backend.entities.IdentifiedEntity;
import org.marsik.elshelves.backend.entities.IdentifiedEntityInterface;
import org.marsik.elshelves.backend.entities.Lot;
import org.marsik.elshelves.backend.entities.LotHistory;
import org.marsik.elshelves.backend.entities.MixedLot;
import org.marsik.elshelves.backend.entities.OwnedEntityInterface;
import org.marsik.elshelves.backend.entities.PurchasedLot;
import org.marsik.elshelves.backend.entities.Requirement;
import org.marsik.elshelves.backend.entities.Type;
import org.marsik.elshelves.backend.entities.User;
import org.marsik.elshelves.backend.interfaces.Relinker;
import org.marsik.elshelves.backend.repositories.BoxRepository;
import org.marsik.elshelves.backend.repositories.LotRepository;
import org.marsik.elshelves.backend.repositories.PurchaseRepository;
import org.marsik.elshelves.backend.test.unit.BaseUnitTest;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import javax.persistence.EntityManager;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

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

    @Mock
    RelinkService relinkService;

    @Mock
    Relinker relinker;

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

        // Mock relinker
        doReturn(relinker).when(relinkService).newRelinker();
        doReturn(relinker).when(relinker).currentUser(any(User.class));
        when(relinker.findExisting(any())).thenAnswer(new Answer<IdentifiedEntityInterface>() {
            @Override
            public IdentifiedEntityInterface answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                return (IdentifiedEntityInterface) args[0];
            }
        });
    }

    @Test
    public void testLotMixerBase() throws Exception {
        Lot l1 = getLot(Lot.class, type, 5);
        box.addLot(l1);

        Lot l2 = getLot(Lot.class, type, 7);
        box.addLot(l2);
        final THashMap<Lot, Lot> lotMap = new THashMap<>();
        Lot result = service.lotMixer(l2, lotMap);

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

        assertThat(l1.isValid()).isFalse();
        assertThat(l2.isValid()).isFalse();

        assertThat(box.getLots()).containsExactly(result);

        assertThat(lotMap)
                .containsKeys(l1, l2);
    }

    @Test
    public void testLotMixerBaseUsed() throws Exception {
        Lot l1 = getLot(Lot.class, type, 5);
        box.addLot(l1);

        Lot l2 = getLot(Lot.class, type, 7);
        l2.setUsedInPast(true);

        box.addLot(l2);
        final THashMap<Lot, Lot> lotMap = new THashMap<>();
        Lot result = service.lotMixer(l2, lotMap);

        assertThat(result)
                .isNotNull()
                .isInstanceOf(MixedLot.class)
                .isNotEqualTo(l2)
                .isNotEqualTo(l1);

        assertThat(result.getUsedInPast())
                .isNotNull()
                .isTrue();

        assertThat(((MixedLot) result).getParents())
                .isNotNull()
                .hasSize(2)
                .contains(l1, l2);

        assertThat(result.getCount())
                .isEqualTo(12);

        assertThat(l1.isValid()).isFalse();
        assertThat(l2.isValid()).isFalse();

        assertThat(box.getLots()).containsExactly(result);

        assertThat(lotMap)
                .containsKeys(l1, l2);
    }

    @Test
    public void testLotWithSerial() throws Exception {
        Lot l1 = getLot(Lot.class, type, 5);
        box.addLot(l1);

        Lot l2 = getLot(Lot.class, type, 7);
        l2.setSerials(new THashSet<>());
        l2.getSerials().add("SERIAL");
        box.addLot(l2);

        final THashMap<Lot, Lot> lotMap = new THashMap<>();
        Lot result = service.lotMixer(l2, lotMap);
        Lot newL1 = service.lotMixer(l1, lotMap);


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

        assertThat(lotMap)
                .isEmpty();
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

        Map<Lot, Lot> cache = new THashMap<>();
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

        assertThat(assigned.isValid());

        assertThat(l1.isValid()).isTrue();

        assertThat(l2.isValid()).isFalse();

        assertThat(l3.getUsedBy()).isNull();
        assertThat(l3.isValid()).isFalse();

        assertThat(box.getLots())
                .hasSize(2)
                .contains(result, assigned);

        assertThat(cache)
                .containsKeys(l3, l2);
    }

    @Test
    public void testLotMixerAlreadyMixedExists() throws Exception {
        Lot l1 = getLot(MixedLot.class, type, 5);
        box.addLot(l1);

        Lot l2 = getLot(Lot.class, type, 7);
        box.addLot(l2);
        final THashMap<Lot, Lot> lotMap = new THashMap<>();
        Lot result = service.lotMixer(l2, lotMap);

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

        assertThat(l1.isValid()).isTrue();

        assertThat(l2.isValid()).isFalse();

        assertThat(box.getLots()).containsExactly(result);

        assertThat(lotMap)
                .containsKeys(l1, l2);
    }

    @Test
    public void testTwoMixedExist() throws Exception {
        Lot l1 = getLot(MixedLot.class, type, 5);
        box.addLot(l1);

        Lot l2 = getLot(MixedLot.class, type, 7);
        box.addLot(l2);
        final THashMap<Lot, Lot> lotMap = new THashMap<>();
        Lot result = service.lotMixer(l2, lotMap);
        Lot other = service.lotMixer(l1, lotMap);

        assertThat(other).isEqualTo(result);

        assertThat(result)
                .isNotNull()
                .isInstanceOf(MixedLot.class);

        assertThat(((MixedLot) result).getParents())
                .isNotNull()
                .hasSize(1);

        assertThat(result.getCount())
                .isEqualTo(12);

        assertThat(l2.isValid()).isNotEqualTo(l1.isValid());
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
        Map<Lot, Lot> cache = new THashMap<>();
        Lot result = service.lotMixer(l2, cache);
        Lot aux = service.lotMixer(l1, cache);

        assertThat(result)
                .isNotNull()
                .isInstanceOf(MixedLot.class)
                .isEqualTo(aux);

        for (Lot parent: ((MixedLot) result).getParents()) {
            if (parent instanceof MixedLot) {
                assertThat(((MixedLot) parent).getParents())
                        .isNotNull()
                        .hasSize(2)
                        .doesNotContain(result);
            }
        }

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
        l.setValid(true);
        l.setUsed(false);
        l.setUsedInPast(false);
        l.setHistory(new LotHistory());
        l.getHistory().setAction(cls.equals(MixedLot.class) ? LotAction.MIX : LotAction.DELIVERY);
        return l;
    }

    @Test
    public void testSolderAction() throws Exception {
        Requirement requirement = new Requirement();
        User user = new User();

        Lot lot = getLot(PurchasedLot.class, type, 10);
        lot.setOwner(user);
        lot.setUsedBy(requirement);
        lot.setLocation(box);

        Lot update = new Lot();
        update.setId(lot.getId());
        update.setUsed(true);
        update.setUsedBy(requirement);
        update.setCount(7L);

        LotSplitResult result = service.update(lot, update, user);

        assertThat(result.getRequested())
                .isNotNull()
                .isInstanceOf(PurchasedLot.class);

        assertThat(result.getRequested().getCount())
                .isNotNull()
                .isEqualTo(7L);

        assertThat(result.getRequested().getUsed())
                .isNotNull()
                .isTrue();

        assertThat(result.getRequested().getUsedInPast())
                .isNotNull()
                .isTrue();

        assertThat(result.getRequested().getLocation())
                .isNull();

        assertThat(result.getRequested().getUsedBy())
                .isNotNull()
                .isEqualTo(requirement);

        assertThat(result.getOthers())
                .isNotNull()
                .hasSize(1);

        assertThat(result.getOthers().iterator().next())
                .isInstanceOf(PurchasedLot.class);

        assertThat(result.getOthers().iterator().next().getCount())
                .isEqualTo(3L);

        assertThat(result.getOthers().iterator().next().getLocation())
                .isNotNull()
                .isEqualTo(box);

        assertThat(result.getOthers().iterator().next().getUsedBy())
                .isNotNull()
                .isEqualTo(requirement);
    }

    @Test
    public void testSolderActionMixedLot() throws Exception {
        Requirement requirement = new Requirement();
        User user = new User();

        Lot lot1a = getLot(PurchasedLot.class, type, 10);
        lot1a.setOwner(user);
        lot1a.setLocation(box);

        Lot lot1b = getLot(PurchasedLot.class, type, 10);
        lot1b.setOwner(user);
        lot1b.setLocation(box);

        Lot lot = service.lotMixer(lot1a, new THashMap<>());
        lot.setUsedBy(requirement);

        assertThat(box.getLots())
                .hasSize(1)
                .contains(lot);

        assertThat(lot1a.getValid())
                .isFalse();

        assertThat(lot1b.getValid())
                .isFalse();

        Lot update = new Lot();
        update.setId(lot.getId());
        update.setUsed(true);
        update.setUsedBy(requirement);
        update.setCount(7L);

        LotSplitResult result = service.update(lot, update, user);

        assertThat(result.getRequested())
                .isNotNull()
                .isInstanceOf(MixedLot.class);

        assertThat(((MixedLot) result.getRequested()).getParents())
                .hasSize(2)
                .contains(lot1a, lot1b);

        assertThat(result.getRequested().getCount())
                .isNotNull()
                .isEqualTo(7L);

        assertThat(result.getRequested().getUsed())
                .isNotNull()
                .isTrue();

        assertThat(result.getRequested().getUsedInPast())
                .isNotNull()
                .isTrue();

        assertThat(result.getRequested().getLocation())
                .isNull();

        assertThat(result.getRequested().getUsedBy())
                .isNotNull()
                .isEqualTo(requirement);

        assertThat(result.getOthers())
                .isNotNull()
                .hasSize(1);

        assertThat(result.getOthers().iterator().next())
                .isInstanceOf(MixedLot.class);

        assertThat(result.getOthers().iterator().next().getCount())
                .isEqualTo(13L);

        assertThat(result.getOthers().iterator().next().getLocation())
                .isNotNull()
                .isEqualTo(box);

        assertThat(result.getOthers().iterator().next().getUsedBy())
                .isNotNull()
                .isEqualTo(requirement);

        assertThat(((MixedLot) result.getOthers().iterator().next()).getParents())
                .hasSize(2)
                .contains(lot1a, lot1b);
    }

    @Test
    public void testOnlyTwoAssignedMixedLots() throws Exception {
        User user = new User();

        Lot lot1a = getLot(PurchasedLot.class, type, 10);
        lot1a.setOwner(user);
        lot1a.setLocation(box);

        Lot lot1b = getLot(PurchasedLot.class, type, 10);
        lot1b.setOwner(user);
        lot1b.setLocation(box);

        Lot lot1 = service.lotMixer(lot1a, new THashMap<>());
        Requirement requirement1 = new Requirement();
        lot1.setUsedBy(requirement1);

        assertThat(lot1)
                .isInstanceOf(MixedLot.class);

        Box box2 = new Box();
        Lot lot2a = getLot(PurchasedLot.class, type, 10);
        lot2a.setOwner(user);
        lot2a.setLocation(box2);

        Lot lot2b = getLot(PurchasedLot.class, type, 10);
        lot2b.setOwner(user);
        lot2b.setLocation(box2);

        Lot lot2 = service.lotMixer(lot2a, new THashMap<>());
        Requirement requirement2 = new Requirement();
        lot2.setUsedBy(requirement2);
        lot2.setLocation(box);

        assertThat(lot2)
                .isInstanceOf(MixedLot.class);

        assertThat(box.getLots())
                .hasSize(2)
                .contains(lot1, lot2);

        Map<Lot, Lot> cache = new THashMap<>();
        Lot result1 = service.lotMixer(lot1, cache);

        assertThat(result1)
                .isEqualTo(lot1)
                .isInstanceOf(MixedLot.class);

        assertThat(lot1.isValid())
                .isTrue();

        assertThat(((MixedLot) lot1).getParents())
                .hasSize(4)
                .contains(lot1a, lot1b, lot2a, lot2b);
    }

    @Test
    public void testOnlyTwoAssignedMixedLotsWithHistory() throws Exception {
        User user = new User();

        Lot lot1a = getLot(PurchasedLot.class, type, 10);
        lot1a.setOwner(user);
        lot1a.setLocation(box);

        Lot lot1b = getLot(PurchasedLot.class, type, 10);
        lot1b.setOwner(user);
        lot1b.setLocation(box);

        Lot lot1 = service.lotMixer(lot1a, new THashMap<>());
        Requirement requirement1 = new Requirement();

        Lot update = (Lot) lot1.shallowClone();
        update.setUsedBy(requirement1);
        lot1.updateFrom(update);
        lot1.setHistory(lot1.createRevision(update, uuidGenerator, user));

        assertThat(lot1)
                .isInstanceOf(MixedLot.class);
        assertThat(lot1.getHistory().getAction())
                .isNotEqualTo(LotAction.MIX);

        Box box2 = new Box();
        Lot lot2a = getLot(PurchasedLot.class, type, 10);
        lot2a.setOwner(user);
        lot2a.setLocation(box2);

        Lot lot2b = getLot(PurchasedLot.class, type, 10);
        lot2b.setOwner(user);
        lot2b.setLocation(box2);

        Lot lot2 = service.lotMixer(lot2a, new THashMap<>());
        Requirement requirement2 = new Requirement();
        lot2.setUsedBy(requirement2);
        lot2.setLocation(box);

        assertThat(lot2)
                .isInstanceOf(MixedLot.class);

        assertThat(box.getLots())
                .hasSize(2)
                .contains(lot1, lot2);

        Map<Lot, Lot> cache = new THashMap<>();
        Lot result1 = service.lotMixer(lot1, cache);

        assertThat(result1)
                .isNotEqualTo(lot1)
                .isInstanceOf(MixedLot.class);

        assertThat(lot1.isValid())
                .isFalse();

        assertThat(result1.isValid())
                .isTrue();

        assertThat(((MixedLot) lot1).getParents())
                .hasSize(2)
                .contains(lot1a, lot1b);

        assertThat(((MixedLot) result1).getParents())
                .hasSize(3)
                .contains(lot1, lot2a, lot2b);

        assertThat(box.getLots())
                .hasSize(2)
                .contains(result1, lot2);

        assertThat(lot2.isValid())
                .isTrue();

        assertThat(((MixedLot) lot2).getParents())
                .hasSize(3)
                .contains(lot1, lot2a, lot2b);
    }
}
