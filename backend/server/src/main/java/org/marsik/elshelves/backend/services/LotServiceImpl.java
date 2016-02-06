package org.marsik.elshelves.backend.services;

import com.google.common.annotations.VisibleForTesting;
import gnu.trove.map.hash.THashMap;
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
import org.marsik.elshelves.backend.entities.Type;
import org.marsik.elshelves.backend.entities.User;
import org.marsik.elshelves.backend.interfaces.Relinker;
import org.marsik.elshelves.backend.repositories.BoxRepository;
import org.marsik.elshelves.backend.repositories.LotRepository;
import org.marsik.elshelves.backend.repositories.PurchaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.validation.constraints.NotNull;
import java.util.ArrayDeque;
import java.util.Collection;
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

    @PersistenceContext
    EntityManager entityManager;

    @Autowired
    RelinkService relinkService;

    @Autowired
    public LotServiceImpl(LotRepository lotRepository,
            PurchaseRepository purchaseRepository,
            BoxRepository boxRepository,
            UuidGenerator uuidGenerator) {
        this.lotRepository = lotRepository;
        this.purchaseRepository = purchaseRepository;
        this.boxRepository = boxRepository;
        this.uuidGenerator = uuidGenerator;
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
    public Lot delivery(PurchasedLot newLot0, DateTime expiration, User currentUser) throws EntityNotFound, PermissionDenied, OperationNotPermitted {
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

		PurchasedLot lot = PurchasedLot.delivery(purchase,
                uuidGenerator.generate(),
                newLot0.getCount(),
                location,
                expiration,
                currentUser,
                uuidGenerator);
		save(lot);

		purchase.addLot(lot);

		return lotMixer(lot, new THashMap<>());
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

        if (count > update.getCount()) {
            taken = (Lot)lot.shallowClone();
            taken.setDbId(null);
            taken.setVersion(null);
            taken.setId(uuidGenerator.generate());
            taken.setCount(update.getCount());

            taken.relink(context);
            save(taken);

            // Prepare history object
            if (taken.isRevisionNeeded(update)) {
                LotHistory history = taken.createRevision(uuidGenerator, currentUser);
                taken.setPreviousRevision(history);
            }

            saveHistory(taken);
            taken.updateFrom(update);

            save(taken);

            lot.setCount(count - update.getCount());
        } else {
            // Prepare history object
            if (lot.isRevisionNeeded(update)) {
                LotHistory history = lot.createRevision(uuidGenerator, currentUser);
                lot.setPreviousRevision(history);
            }

            saveHistory(lot);
            lot.updateFrom(update);
        }

        save(lot);

        /*
           Perform Lot mixing
         */
        Map<UUID, Lot> cache = new THashMap<>();
        lot = lotMixer(lot, cache);
        taken = lotMixer(taken, cache);

        /* The old id belongs to the rest and the assigned / moved lot has a new uuid.
           This should help with concurrent access (the second request might still have chance
           to get the necessary count without reloading its cache.
         */

        if (taken == null) {
            return new LotSplitResult(lot, null);
        } else {
            return new LotSplitResult(taken, lot);
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
    protected Lot lotMixer(Lot lot, @NotNull Map<UUID, Lot> lotMap) {
        if (lot == null) {
            return null;
        }

        if (!lot.isValid()) {
            return lotMap.getOrDefault(lot.getId(), lot);
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

        lot = lotMap.getOrDefault(lot.getId(), lot);
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
                    .filter(c -> c instanceof MixedLot)
                    .findFirst()
                    .orElseGet(() -> MixedLot.from(uuidGenerator, unassigned));

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

                mixedLot.addPossibleSource(possibleSource);
            }
            unassigned.forEach(c -> c.unlinkWithStatus(LotAction.MIXED));
            unassigned.forEach(this::save);
            unassigned.forEach(c -> lotMap.put(c.getId(), mixedLot));
            save(mixedLot);
        }

        // Find all already assigned Lots of the expected type
        Set<Lot> assigned = candidates.stream()
                .filter(c -> c.getUsedBy() != null)
                .collect(Collectors.toSet());

        // Convert all assigned parts to MixedLots
        // keep the proper count, but add all possible sources
        // update the assignments!
        for (Lot l: assigned) {
            MixedLot mixedLot;
            if (l instanceof MixedLot) {
                mixedLot = (MixedLot) l;
            } else {
                mixedLot = MixedLot.from(uuidGenerator, l);
            }

            for (Lot possibleSource: candidates) {
                // Avoid cyclic dependencies
                if (possibleSource == mixedLot) continue;

                mixedLot.addPossibleSource(possibleSource);
            }

            // Unlink the old lot if necessary (this will unassign it)
            if (mixedLot != l) {
                l.unlinkWithStatus(LotAction.MIXED);
                save(l);
            }

            lotMap.put(l.getId(), mixedLot);
            save(mixedLot);
        }

        return lotMap.getOrDefault(lot.getId(), lot);
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
