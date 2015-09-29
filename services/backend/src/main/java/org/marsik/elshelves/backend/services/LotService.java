package org.marsik.elshelves.backend.services;

import gnu.trove.map.hash.THashMap;
import org.joda.time.DateTime;
import org.marsik.elshelves.api.entities.BoxApiModel;
import org.marsik.elshelves.api.entities.LotApiModel;
import org.marsik.elshelves.api.entities.RequirementApiModel;
import org.marsik.elshelves.backend.controllers.exceptions.EntityNotFound;
import org.marsik.elshelves.backend.controllers.exceptions.OperationNotPermitted;
import org.marsik.elshelves.backend.controllers.exceptions.PermissionDenied;
import org.marsik.elshelves.backend.dtos.LotSplitResult;
import org.marsik.elshelves.backend.entities.Box;
import org.marsik.elshelves.backend.entities.Lot;
import org.marsik.elshelves.backend.entities.Purchase;
import org.marsik.elshelves.backend.entities.Requirement;
import org.marsik.elshelves.backend.entities.User;
import org.marsik.elshelves.backend.entities.converters.LotToEmber;
import org.marsik.elshelves.backend.repositories.BoxRepository;
import org.marsik.elshelves.backend.repositories.LotRepository;
import org.marsik.elshelves.backend.repositories.PurchaseRepository;
import org.marsik.elshelves.backend.repositories.RequirementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;

@Service
public class LotService {
	LotRepository lotRepository;
	PurchaseRepository purchaseRepository;
	BoxRepository boxRepository;
	UuidGenerator uuidGenerator;
    RequirementRepository requirementRepository;

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
		lotRepository.save(lot);

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
        lotRepository.save(lot0);

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


		Map<UUID, Object> cache = new THashMap<>();

		Lot result = lot.take(count, currentUser, uuidGenerator, requirement);
		if (result == null) {
			throw new OperationNotPermitted();
		}

		lotRepository.save(result);

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

		Map<UUID, Object> cache = new THashMap<>();

		lot.destroy(currentUser, uuidGenerator);
		lotRepository.save(lot);

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

        Map<UUID, Object> cache = new THashMap<>();

        lot.solder(currentUser, requirement, uuidGenerator);
        lotRepository.save(lot);

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

        Map<UUID, Object> cache = new THashMap<>();

        lot.unsolder(currentUser, uuidGenerator);
        lotRepository.save(lot);

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

        Map<UUID, Object> cache = new THashMap<>();

        lot.assign(currentUser, requirement, uuidGenerator);
        lotRepository.save(lot);

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

        Map<UUID, Object> cache = new THashMap<>();

        lot.unassign(currentUser, uuidGenerator);
        lotRepository.save(lot);

        return lot;
    }
}
