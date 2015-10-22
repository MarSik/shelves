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

		purchase.getLots().add(lot);

		return lot;
	}

    public Lot move(UUID previous, Box location0, User currentUser) throws EntityNotFound, PermissionDenied, OperationNotPermitted {
        Box location = boxRepository.findById(location0.getId());
        Lot lot0 = lotRepository.findById(previous);

        if (lot0 == null || location == null) {
            throw new EntityNotFound();
        }

        if (!lot0.getOwner().equals(currentUser)) {
            throw new PermissionDenied();
        }

        if (!location.getOwner().equals(currentUser)) {
            throw new PermissionDenied();
        }

        lot0.move(currentUser, location, uuidGenerator);
        save(lot0);

        return lot0;
    }

	public LotSplitResult split(UUID source, Long count, User currentUser, Requirement requirement0) throws PermissionDenied, EntityNotFound, OperationNotPermitted {
		Lot lot = lotRepository.findById(source);
        Requirement requirement = null;

        if (requirement0 != null) {
            requirement = requirementRepository.findById(requirement0.getId());
        }

        if (lot == null
                || (requirement0 != null && requirement == null)) {
            throw new EntityNotFound();
        }

        if (!lot.getOwner().equals(currentUser)
                || (requirement != null && !requirement.getOwner().equals(currentUser))) {
            throw new PermissionDenied();
        }

        if (count <= 0
                || (count < lot.getCount() && !lot.isCanBeSplit())
                || (requirement != null && !lot.isCanBeAssigned())) {
            throw new OperationNotPermitted();
        }

		Lot result = lot.take(count, currentUser, uuidGenerator, requirement);
		if (result == null) {
			throw new OperationNotPermitted();
		}

		save(result);
        save(lot);

		return new LotSplitResult(result, result.equals(lot) ? null : lot);
	}

	public Lot destroy(UUID source, User currentUser) throws PermissionDenied, EntityNotFound {
		Lot lot = lotRepository.findById(source);

		if (lot == null) {
			throw new EntityNotFound();
		}

		if (!lot.getOwner().equals(currentUser)) {
			throw new PermissionDenied();
		}

		lot.destroy(currentUser, uuidGenerator);
		save(lot);

		return lot;
	}

    public Lot solder(UUID source, User currentUser, Requirement requirement0) throws PermissionDenied, EntityNotFound, OperationNotPermitted {
        Lot lot = lotRepository.findById(source);
        Requirement requirement = null;

        if (requirement0 != null) {
            requirement = requirementRepository.findById(requirement0.getId());
        }

        if (lot == null
                || (requirement0 != null && requirement == null)) {
            throw new EntityNotFound();
        }

        if (!lot.getOwner().equals(currentUser)
                || (requirement != null && !requirement.getOwner().equals(currentUser))) {
            throw new PermissionDenied();
        }

        if (!lot.isCanBeSoldered()
                || (requirement != null && !lot.isCanBeMoved())) {
            throw new OperationNotPermitted();
        }

        lot.solder(currentUser, requirement, uuidGenerator);
        save(lot);

        return lot;
    }

    public Lot unsolder(UUID source, User currentUser) throws PermissionDenied, EntityNotFound, OperationNotPermitted {
        Lot lot = lotRepository.findById(source);

        if (lot == null) {
            throw new EntityNotFound();
        }

        if (!lot.getOwner().equals(currentUser)) {
            throw new PermissionDenied();
        }

        if (!lot.isCanBeUnsoldered()) {
            throw new OperationNotPermitted();
        }

        lot.unsolder(currentUser, uuidGenerator);
        save(lot);

        return lot;
    }

    public Lot assign(UUID source, User currentUser, Requirement requirement0) throws OperationNotPermitted, PermissionDenied, EntityNotFound {
        Lot lot = lotRepository.findById(source);
        Requirement requirement = requirementRepository.findById(requirement0.getId());

        if (lot == null
                || requirement == null) {
            throw new EntityNotFound();
        }

        if (!lot.getOwner().equals(currentUser)) {
            throw new PermissionDenied();
        }

        if (!requirement.getOwner().equals(currentUser)) {
            throw new PermissionDenied();
        }

        if (!lot.isCanBeAssigned()) {
            throw new OperationNotPermitted();
        }

        lot.assign(currentUser, requirement, uuidGenerator);
        save(lot);

        return lot;
    }

    public Lot unassign(UUID source, User currentUser) throws OperationNotPermitted, PermissionDenied, EntityNotFound {
        Lot lot = lotRepository.findById(source);

        if (lot == null) {
            throw new EntityNotFound();
        }

        if (!lot.getOwner().equals(currentUser)) {
            throw new PermissionDenied();
        }

        if (!lot.isCanBeUnassigned()) {
            throw new OperationNotPermitted();
        }

        lot.unassign(currentUser, uuidGenerator);
        save(lot);

        return lot;
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
