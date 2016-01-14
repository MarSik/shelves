package org.marsik.elshelves.backend.services;

import org.joda.time.DateTime;
import org.marsik.elshelves.backend.controllers.exceptions.EntityNotFound;
import org.marsik.elshelves.backend.controllers.exceptions.OperationNotPermitted;
import org.marsik.elshelves.backend.controllers.exceptions.PermissionDenied;
import org.marsik.elshelves.backend.dtos.LotSplitResult;
import org.marsik.elshelves.backend.entities.Box;
import org.marsik.elshelves.backend.entities.IdentifiedEntityInterface;
import org.marsik.elshelves.backend.entities.Lot;
import org.marsik.elshelves.backend.entities.LotHistory;
import org.marsik.elshelves.backend.entities.Purchase;
import org.marsik.elshelves.backend.entities.User;
import org.marsik.elshelves.backend.interfaces.Relinker;
import org.marsik.elshelves.backend.repositories.BoxRepository;
import org.marsik.elshelves.backend.repositories.LotRepository;
import org.marsik.elshelves.backend.repositories.PurchaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;
import java.util.UUID;

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

	@Override public Collection<Lot> getAll(User currentUser) {
		return getAllEntities(currentUser);
	}

	@Override public Lot get(UUID id, User currentUser) throws PermissionDenied, EntityNotFound {
		Lot lot = lotRepository.findById(id);

		if (lot == null) {
			throw new EntityNotFound();
		}

		if (!lot.getOwner().equals(currentUser)) {
			throw new PermissionDenied();
		}

		return lot;
	}

	@Override public Lot delivery(Lot newLot0, DateTime expiration, User currentUser) throws EntityNotFound, PermissionDenied, OperationNotPermitted {
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

		Lot lot = Lot.delivery(purchase,
                uuidGenerator.generate(),
                newLot0.getCount(),
                location,
                expiration,
                currentUser,
                uuidGenerator);
		save(lot);

		purchase.addLot(lot);

		return lot;
	}



	@Override public <T extends Lot> LotSplitResult<T> update(T lot, T update, User currentUser) throws PermissionDenied, EntityNotFound, OperationNotPermitted {
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

        T taken = null;

        if (count > update.getCount()) {
            taken = (T)lot.shallowClone();
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

        /* The old id belongs to the rest and the assigned / moved lot has a new uuid.
           This should help with concurrent access (the second request might still have chance
           to get the necessary count without reloading its cache.
         */

        if (taken == null) {
            return new LotSplitResult<T>(lot, null);
        } else {
            return new LotSplitResult<T>(taken, lot);
        }
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
