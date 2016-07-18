package org.marsik.elshelves.backend.services;

import com.google.common.annotations.VisibleForTesting;
import gnu.trove.map.hash.THashMap;
import gnu.trove.set.hash.THashSet;
import org.javamoney.moneta.Money;
import org.javamoney.moneta.function.MonetaryFunctions;
import org.joda.time.DateTime;
import org.marsik.elshelves.api.entities.fields.LotAction;
import org.marsik.elshelves.backend.controllers.exceptions.EntityNotFound;
import org.marsik.elshelves.backend.controllers.exceptions.OperationNotPermitted;
import org.marsik.elshelves.backend.controllers.exceptions.PermissionDenied;
import org.marsik.elshelves.backend.dtos.LotSplitResult;
import org.marsik.elshelves.backend.entities.Box;
import org.marsik.elshelves.backend.entities.IdentifiedEntityInterface;
import org.marsik.elshelves.backend.entities.Lot;
import org.marsik.elshelves.backend.entities.LotHistory;
import org.marsik.elshelves.backend.entities.MixedLot;
import org.marsik.elshelves.backend.entities.Purchase;
import org.marsik.elshelves.backend.entities.PurchasedLot;
import org.marsik.elshelves.backend.entities.Source;
import org.marsik.elshelves.backend.entities.Transaction;
import org.marsik.elshelves.backend.entities.Type;
import org.marsik.elshelves.backend.entities.User;
import org.marsik.elshelves.backend.interfaces.Relinker;
import org.marsik.elshelves.backend.repositories.BoxRepository;
import org.marsik.elshelves.backend.repositories.LotRepository;
import org.marsik.elshelves.backend.repositories.PurchaseRepository;
import org.marsik.elshelves.backend.repositories.SourceRepository;
import org.marsik.elshelves.backend.repositories.TransactionRepository;
import org.marsik.elshelves.backend.repositories.TypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.validation.constraints.NotNull;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Comparator;
import java.util.Deque;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class LotServiceImpl implements LotService {
	LotRepository lotRepository;
	PurchaseRepository purchaseRepository;
	BoxRepository boxRepository;
	UuidGenerator uuidGenerator;
    private TransactionRepository transactionRepository;
    private TypeRepository typeRepository;
    private SourceRepository sourceRepository;

    @PersistenceContext
    EntityManager entityManager;

    @Autowired
    RelinkService relinkService;

    @Autowired
    public LotServiceImpl(LotRepository lotRepository,
                          PurchaseRepository purchaseRepository,
                          BoxRepository boxRepository,
                          TransactionRepository transactionRepository,
                          TypeRepository typeRepository,
                          SourceRepository sourceRepository,
                          UuidGenerator uuidGenerator) {
        this.lotRepository = lotRepository;
        this.purchaseRepository = purchaseRepository;
        this.boxRepository = boxRepository;
        this.uuidGenerator = uuidGenerator;
        this.transactionRepository = transactionRepository;
        this.typeRepository = typeRepository;
        this.sourceRepository = sourceRepository;
    }

    protected Collection<Lot> getAllEntities(User currentUser) {
        return lotRepository.findByOwner(currentUser);
    }

	@Override
    public Collection<Lot> getAll(User currentUser) {
		return getAllEntities(currentUser);
	}

	@Override
    public Lot get(UUID id, User currentUser) throws PermissionDenied, EntityNotFound {
		Lot lot = lotRepository.findById(id);

		if (lot == null) {
			throw new EntityNotFound();
		}

		if (!lot.getOwner().equals(currentUser)) {
			throw new PermissionDenied();
		}

		return lot;
	}

	@Override
    public Lot deliverPurchasedLot(PurchasedLot newLot0, DateTime expiration, User currentUser) throws EntityNotFound, PermissionDenied, OperationNotPermitted {
		Purchase purchase = purchaseRepository.findById(newLot0.getPurchase().getId());
		Box location = boxRepository.findById(newLot0.getLocation().getId());

		if (purchase == null || location == null) {
			throw new EntityNotFound();
		}

		if (!purchase.getOwner().equals(currentUser)) {
			throw new PermissionDenied();
		}

		if (!location.getOwner().equals(currentUser)) {
			throw new PermissionDenied();
		}

		PurchasedLot lot = delivery(purchase,
                uuidGenerator.generate(),
                newLot0.getCount(),
                location,
                expiration,
                currentUser,
                uuidGenerator);
		save(lot);

		purchase.addLot(lot);
        saveOrUpdate(purchase);

		return lotMixer(lot, new THashMap<>());
	}

    @Override
    public Lot deliverTypeInTransaction(Transaction transaction0, Type type0, Box location0, Long count,
                                        DateTime expiration, User currentUser) throws EntityNotFound, PermissionDenied, OperationNotPermitted {
        Transaction transaction = transactionRepository.findById(transaction0.getId());
        Box location = boxRepository.findById(location0.getId());
        Type type = typeRepository.findById(type0.getId());

        if (transaction == null || location == null || type == null) {
            throw new EntityNotFound();
        }

        if (!transaction.getOwner().equals(currentUser)) {
            throw new PermissionDenied();
        }

        if (!location.getOwner().equals(currentUser)) {
            throw new PermissionDenied();
        }

        if (!type.getOwner().equals(currentUser)) {
            throw new PermissionDenied();
        }

        Comparator<Purchase> priceComparator = new Comparator<Purchase>() {
            @Override
            public int compare(Purchase one, Purchase two) {
                return MonetaryFunctions.sortNumberDesc().compare(one.getSinglePricePaid(), two.getSinglePricePaid());
            }
        };

        Comparator<Purchase> sizeComparator = new Comparator<Purchase>() {
            @Override
            public int compare(Purchase one, Purchase two) {
                return Long.compare(one.getCount(), two.getCount());
            }
        };

        java.util.List<Purchase> purchases = transaction.getItems().stream()
                .filter(p -> p.getType().equals(type))
                .sorted(priceComparator.thenComparing(sizeComparator))
                .collect(Collectors.toList());

        Purchase purchase = null;

        if (purchases.size() == 1) {
            // single purchase, use it (normal situation)
            purchase = purchases.get(0);
        } else if (!purchases.isEmpty()) {
            // multiple possible purchases, use the most expensive one that is not filled
            // while we do allow this it is kind of unusual to have multiple prices for the same item

            // look for possible unfilled purchases with enough free space
            for (Purchase p: purchases) {
                if (p.getLots().stream()
                        .filter(Lot::isValid)
                        .mapToLong(Lot::getCount)
                        .sum() <= p.getCount() - count) {
                    purchase = p;
                    break;
                }
            }
        }

        if (purchase == null) {
            // No such type is part of that transaction, create a Purchase
            purchase = new Purchase();
            purchase.setId(uuidGenerator.generate());
            purchase.setType(type);
            purchase.setTransaction(transaction);
            purchase.setCount(count);
            purchase.setType(type);
            purchase.setOwner(currentUser);
            saveOrUpdate(purchase);
        }

        PurchasedLot lot = delivery(purchase,
                uuidGenerator.generate(),
                count,
                location,
                expiration,
                currentUser,
                uuidGenerator);

        save(lot);
        purchase.addLot(lot);
        saveOrUpdate(purchase);

        return lotMixer(lot, new THashMap<>());
    }

    @Override
    public Lot deliverAdHoc(Source source0, Type type0, Box location0, Long count,
                            DateTime expiration, User currentUser) throws EntityNotFound, PermissionDenied, OperationNotPermitted {
        Source source = sourceRepository.findById(source0.getId());
        Box location = boxRepository.findById(location0.getId());
        Type type = typeRepository.findById(type0.getId());

        if (source == null || location == null || type == null) {
            throw new EntityNotFound();
        }

        if (!source.getOwner().equals(currentUser)) {
            throw new PermissionDenied();
        }

        if (!location.getOwner().equals(currentUser)) {
            throw new PermissionDenied();
        }

        if (!type.getOwner().equals(currentUser)) {
            throw new PermissionDenied();
        }

        Transaction transaction = new Transaction();
        transaction.setId(uuidGenerator.generate());
        transaction.setDate(DateTime.now());
        transaction.setSource(source);
        transaction.setFlagged(true);
        transaction.setOwner(currentUser);
        transaction.setCreated(DateTime.now());
        transaction.setName("AdHoc transaction "
                + source.getName()
                + " "
                + transaction.getCreated().toString("yyyy-MM-dd HH:mm"));

        saveOrUpdate(transaction);

        Purchase purchase = new Purchase();
        purchase.setId(uuidGenerator.generate());
        purchase.setType(type);
        purchase.setTransaction(transaction);
        purchase.setCount(count);
        purchase.setType(type);
        purchase.setOwner(currentUser);
        saveOrUpdate(purchase);

        PurchasedLot lot = delivery(purchase,
                uuidGenerator.generate(),
                count,
                location,
                expiration,
                currentUser,
                uuidGenerator);

        save(lot);
        purchase.addLot(lot);
        saveOrUpdate(purchase);
        saveOrUpdate(transaction);

        return lotMixer(lot, new THashMap<>());
    }

    private static PurchasedLot delivery(Purchase purchase, UUID uuid, Long count,
                                        Box location, DateTime expiration, User performedBy, UuidGenerator uuidGenerator) {
        PurchasedLot l = new PurchasedLot();
        l.setOwner(purchase.getOwner());
        l.setId(uuid);
        l.setLocation(location);
        l.setCount(count);
        l.setPurchase(purchase);
        l.setExpiration(expiration);
        l.setUsed(false);
        l.setUsedInPast(false);
        l.setValid(true);
        l.setType(purchase.getType());

        LotHistory h = l.createRevision(null, uuidGenerator, performedBy);
        h.setLocation(location);
        l.setHistory(h);

        return l;
    }

	@Override
    public LotSplitResult update(Lot lot, Lot update, User currentUser) throws PermissionDenied, EntityNotFound, OperationNotPermitted {
        if (lot == null) {
            throw new EntityNotFound();
        }

        if (!lot.getOwner().equals(currentUser)) {
            throw new PermissionDenied();
        }

        long count = lot.getCount();

        Relinker context = relinkService.newRelinker();
        context
                .currentUser(currentUser)
                .relink(update);

        Lot taken = null;

        // Prepare update without count change
        Lot dummyUpdate = (Lot) update.shallowClone();
        dummyUpdate.setCount(count);

        if (!lot.isRevisionNeeded(dummyUpdate)) {
            // split and move to the same location, parts can't be distinguished
            // so keep them together

            // Perform count unrelated updates
            lot.updateFrom(update);
            save(lot);

            return new LotSplitResult(lot, (Collection<Lot>) null);

        } else if (count > update.getCount()) {
            taken = (Lot)lot.shallowClone();
            taken.setDbId(null);
            taken.setVersion(null);
            taken.setId(uuidGenerator.generate());
            taken.setCount(update.getCount());

            // Taken entity does not inherit barcodes by default
            taken.setCodes(new THashSet<>());

            taken.relink(context);
            save(taken);

            // Prepare history object
            if (taken.isRevisionNeeded(update)) {
                LotHistory history = taken.createRevision(update, uuidGenerator, currentUser);
                taken.setPreviousRevision(history);
            }

            saveHistory(taken);
            taken.updateFrom(update);

            save(taken);

            lot.setCount(count - update.getCount());
        } else {
            // Prepare history object
            if (lot.isRevisionNeeded(update)) {
                LotHistory history = lot.createRevision(update, uuidGenerator, currentUser);
                lot.setPreviousRevision(history);
            }

            saveHistory(lot);
            lot.updateFrom(update);
        }

        save(lot);

        Map<Lot, Lot> cache = new THashMap<>();

        /*
           Perform Lot mixing
         */
        Lot original = lot;
        lot = lotMixer(lot, cache);
        taken = lotMixer(taken, cache);

        // Make sure the possible modified lot is inserted to the cache for sideloading,
        // but make sure we do not rewrite the result of mixing
        cache.putIfAbsent(original, original);

        /* The old id belongs to the rest and the assigned / moved lot has a new uuid.
           This should help with concurrent access (the second request might still have chance
           to get the necessary count without reloading its cache.
         */

        if (taken == null) {
            return new LotSplitResult(lot, cache);
        } else {
            return new LotSplitResult(taken, cache);
        }
	}

    /**
     * Check the location of lot to see whether there are some undistinguishable parts.
     * Lots with different history, the same type, no serial number and no bar-code cant be distinguished
     * in the future and have to be joined together to a MixedLot.
     *
     * All assignments have to be updated when a lot is changed to MixedLot (the UUID will change!) and
     * the location and assignments for the originals have to be reset.
     *
     * @param lot one changed / moved lot to be used as search template
     * @return Mixed version of the lot if the lot was changed
     */
    @VisibleForTesting
    protected Lot lotMixer(Lot lot, @NotNull Map<Lot, Lot> lotMap) {
        if (lot == null) {
            return null;
        }

        if (!lot.isValid()) {
            return lotMap.getOrDefault(lot, lot);
        }

        if (lot.getLocation() == null) {
            return lot;
        }

        if (lot.getSerials() != null && !lot.getSerials().isEmpty()) {
            return lot;
        }

        if (lot.getCodes() != null && !lot.getCodes().isEmpty()) {
            return lot;
        }

        lot = lotMap.getOrDefault(lot, lot);
        final Type lotType = lot.getType();

        // Get all lots with the same type and no serials or barcodes
        Set<Lot> candidates = lot.getLocation().getLots().stream()
                .filter(c -> Objects.equals(c.getType(), lotType))
                .filter(c -> c.getSerials() == null || c.getSerials().isEmpty())
                .filter(c -> c.getCodes() == null || c.getCodes().isEmpty())
                .collect(Collectors.toSet());

        // Add the "template" lot
        candidates.add(lot);

        // If there was no other lot, no mixing is needed
        if (candidates.size() == 1) {
            return lot;
        }

        // Collect freely laying unassigned lots
        Set<Lot> unassigned = candidates.stream()
                .filter(c -> c.getUsedBy() == null)
                .collect(Collectors.toSet());

        // Create a single unassigned lot
        if (!unassigned.isEmpty()) {
            // Check for existing mixed lot of the right type
            MixedLot mixedLot = (MixedLot)unassigned.stream()
                    .filter(this::isMixedWithNoSignificantHistory)
                    .findFirst()
                    .orElseGet(() -> MixedLot.from(uuidGenerator, unassigned));

            // Record the target to the cache so we can sideload it
            // when it was not the original lot
            lotMap.put(mixedLot, mixedLot);

            // Just to make sure we are not creating cyclic dependency
            // or deleting the mixed lot
            unassigned.remove(mixedLot);

            // Make sure all parents are added
            for (Lot source: unassigned) {
                mixedLot.addPartsToMix(source);
            }

            for (Lot possibleSource: candidates) {
                // Avoid cyclic dependencies
                if (possibleSource == mixedLot) continue;

                // Avoid dependencies between assigned lots that were kept
                // because they do not have significant history
                // When such mixed lot is detected, its parents are added instead
                if (isMixedAssignedWithNoSignificantHistory(possibleSource)) {
                    ((MixedLot) possibleSource).getParents().stream().forEach(mixedLot::addPossibleSource);
                } else {
                    mixedLot.addPossibleSource(possibleSource);
                }
            }

            unassigned.forEach(Lot::invalidate);
            unassigned.forEach(this::save);
            unassigned.forEach(c -> lotMap.put(c, mixedLot));
            save(mixedLot);
        }

        lot = lotMap.getOrDefault(lot, lot);

        // Find all already assigned Lots of the expected type
        Set<Lot> assigned = candidates.stream()
                .filter(c -> c.getUsedBy() != null)
                .collect(Collectors.toSet());

        // Convert all assigned parts to MixedLots
        // keep the proper count, but add all possible sources
        // update the assignments!
        for (Lot l: assigned) {
            MixedLot mixedLot;
            if (isMixedAssignedWithNoSignificantHistory(l)) {
                mixedLot = (MixedLot) l;
            } else {
                mixedLot = MixedLot.from(uuidGenerator, l);
            }

            for (Lot possibleSource: candidates) {
                // Avoid self-cyclic dependencies
                if (possibleSource == mixedLot) continue;

                // Avoid dependencies between assigned lots that were kept
                // because they do not have significant history
                // When such mixed lot is detected, its parents are added instead
                if (isMixedAssignedWithNoSignificantHistory(possibleSource)) {
                    ((MixedLot) possibleSource).getParents().stream().forEach(mixedLot::addPossibleSource);
                } else {
                    mixedLot.addPossibleSource(possibleSource);
                }

            }

            // Unlink the old lot if necessary (this will unassign it)
            if (mixedLot != l) {
                l.invalidate();
                save(l);
            }

            lotMap.put(l, mixedLot);
            save(mixedLot);
        }

        return lotMap.getOrDefault(lot, lot);
    }

    private boolean isMixedAssignedWithNoSignificantHistory(Lot l) {
        // Avoid dependencies between assigned lots that were kept
        // because they do not have significant history
        return isMixedWithNoSignificantHistory(l)
                && l.getUsedBy() != null;
    }

    private boolean isMixedWithNoSignificantHistory(Lot l) {
        return (l instanceof MixedLot
                && l.getHistory().getPrevious() == null
                && l.getHistory().getAction() == LotAction.MIX);
    }

    private <T extends Lot> void saveHistory(T lot) {
        Deque<LotHistory> unsaved = new ArrayDeque<>();
        LotHistory curr = lot.getHistory();

        while (curr != null && curr.isNew()) {
            unsaved.add(curr);
            curr = curr.getPrevious();
        }

        // Save in order from the oldest to the newest
        for (LotHistory h: unsaved) {
            saveOrUpdate(h);
        }
    }

    protected Lot save(Lot entity) {
        if (entity == null) {
            return null;
        }

        saveHistory(entity);
        return lotRepository.save(entity);
    }

    protected <E extends IdentifiedEntityInterface> E saveOrUpdate(E entity) {
        if (entity == null) {
            return null;
        }

        if (entity.isNew()) {
            entityManager.persist(entity);
        } else {
            entityManager.merge(entity);
        }

        return entity;
    }
}
