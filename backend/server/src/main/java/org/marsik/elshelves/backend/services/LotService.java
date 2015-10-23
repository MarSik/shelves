package org.marsik.elshelves.backend.services;

import org.joda.time.DateTime;
import org.marsik.elshelves.backend.controllers.exceptions.EntityNotFound;
import org.marsik.elshelves.backend.controllers.exceptions.OperationNotPermitted;
import org.marsik.elshelves.backend.controllers.exceptions.PermissionDenied;
import org.marsik.elshelves.backend.dtos.LotSplitResult;
import org.marsik.elshelves.backend.entities.Box;
import org.marsik.elshelves.backend.entities.IdentifiedEntityInterface;
import org.marsik.elshelves.backend.entities.Lot;
import org.marsik.elshelves.backend.entities.Purchase;
import org.marsik.elshelves.backend.entities.Requirement;
import org.marsik.elshelves.backend.entities.User;
import org.marsik.elshelves.backend.repositories.BoxRepository;
import org.marsik.elshelves.backend.repositories.LotRepository;
import org.marsik.elshelves.backend.repositories.PurchaseRepository;
import org.marsik.elshelves.backend.repositories.RequirementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Collection;
import java.util.UUID;

@Service
public class LotService {
	LotRepository lotRepository;
	PurchaseRepository purchaseRepository;
	BoxRepository boxRepository;
	UuidGenerator uuidGenerator;
    RequirementRepository requirementRepository;

    @PersistenceContext
    EntityManager entityManager;

    @Autowired
    RelinkService relinkService;

    @Autowired
    public LotService(LotRepository lotRepository, PurchaseRepository purchaseRepository, BoxRepository boxRepository,
                      UuidGenerator uuidGenerator,
                      RequirementRepository requirementRepository) {
        this.lotRepository = lotRepository;
        this.purchaseRepository = purchaseRepository;
        this.boxRepository = boxRepository;
        this.uuidGenerator = uuidGenerator;
        this.requirementRepository = requirementRepository;
    }

    protected Collection<Lot> getAllEntities(User currentUser) {
        return lotRepository.findByOwner(currentUser);
    }

	public Collection<Lot> getAll(User currentUser) {
		return getAllEntities(currentUser);
	}

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

	public Lot delivery(Lot newLot0, DateTime expiration, User currentUser) throws EntityNotFound, PermissionDenied, OperationNotPermitted {
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

		Lot lot = Lot.delivery(purchase, uuidGenerator.generate(), newLot0.getCount(), location, expiration, currentUser, uuidGenerator);
		save(lot);

		purchase.addLot(lot);

		return lot;
	}



	public <T extends Lot> LotSplitResult<T> update(T lot, T update, User currentUser) throws PermissionDenied, EntityNotFound, OperationNotPermitted {
        if (lot == null) {
            throw new EntityNotFound();
        }

        if (!lot.getOwner().equals(currentUser)) {
            throw new PermissionDenied();
        }

        long count = lot.getCount();

        RelinkService.RelinkContext context = relinkService.newRelinker();
        context.relink(update);

		lot.updateFrom(update);

        T rest = null;

        if (count > lot.getCount()) {
            rest = (T)lot.shallowClone();
            rest.setCount(count - lot.getCount());
            save(rest);
        }

        // TODO record history to lot
        save(lot);

		return new LotSplitResult<T>(lot, rest);
	}

    protected Lot save(Lot entity) {
        saveOrUpdate(entity.getHistory());
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
