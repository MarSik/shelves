package org.marsik.elshelves.backend.services;

import gnu.trove.map.hash.THashMap;
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
import org.marsik.elshelves.backend.entities.converters.EmberToLot;
import org.marsik.elshelves.backend.entities.converters.LotToEmber;
import org.marsik.elshelves.backend.repositories.BoxRepository;
import org.marsik.elshelves.backend.repositories.LotRepository;
import org.marsik.elshelves.backend.repositories.PurchaseRepository;
import org.marsik.elshelves.backend.repositories.RequirementRepository;
import org.mozilla.javascript.commonjs.module.Require;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

@Service
public class LotService {
	LotRepository lotRepository;
	PurchaseRepository purchaseRepository;
	BoxRepository boxRepository;
	LotToEmber lotToEmber;
	EmberToLot emberToLot;
	UuidGenerator uuidGenerator;
    RequirementRepository requirementRepository;

    @Autowired
    public LotService(LotRepository lotRepository, PurchaseRepository purchaseRepository, BoxRepository boxRepository, LotToEmber lotToEmber, EmberToLot emberToLot, UuidGenerator uuidGenerator, RequirementRepository requirementRepository) {
        this.lotRepository = lotRepository;
        this.purchaseRepository = purchaseRepository;
        this.boxRepository = boxRepository;
        this.lotToEmber = lotToEmber;
        this.emberToLot = emberToLot;
        this.uuidGenerator = uuidGenerator;
        this.requirementRepository = requirementRepository;
    }

    protected Iterable<Lot> getAllEntities(User currentUser) {
        return lotRepository.findByOwner(currentUser);
    }

	public Collection<LotApiModel> getAll(User currentUser) {
		Collection<LotApiModel> lots = new ArrayList<>();
		Map<UUID, Object> cache = new THashMap<>();
		for (Lot l: getAllEntities(currentUser)) {
			lots.add(lotToEmber.convert(l, 1, cache));
		}

		return lots;
	}

	public LotApiModel get(UUID id, User currentUser) throws PermissionDenied, EntityNotFound {
		Lot lot = lotRepository.findByUuid(id);

		if (lot == null) {
			throw new EntityNotFound();
		}

		if (!lot.getOwner().equals(currentUser)) {
			throw new PermissionDenied();
		}

		return lotToEmber.convert(lot, 1, new THashMap<UUID, Object>());
	}

	public LotApiModel delivery(LotApiModel newLot0, Date expiration, User currentUser) throws EntityNotFound, PermissionDenied, OperationNotPermitted {
		Purchase purchase = purchaseRepository.findByUuid(newLot0.getPurchase().getId());
		Box location = boxRepository.findByUuid(newLot0.getLocation().getId());

		if (purchase == null || location == null) {
			throw new EntityNotFound();
		}

		if (!purchase.getOwner().equals(currentUser)) {
			throw new PermissionDenied();
		}

		if (!location.getOwner().equals(currentUser)) {
			throw new PermissionDenied();
		}

		Lot lot = Lot.delivery(purchase, uuidGenerator.generate(), newLot0.getCount(), location, expiration, currentUser);
		lotRepository.save(lot);

		purchase.getNext().add(lot);

		return lotToEmber.convert(lot, 1, new THashMap<UUID, Object>());
	}

    public LotApiModel move(UUID previous, BoxApiModel location0, User currentUser) throws EntityNotFound, PermissionDenied, OperationNotPermitted {
        Box location = boxRepository.findByUuid(location0.getId());
        Lot lot0 = lotRepository.findByUuid(previous);

        if (lot0 == null || location == null) {
            throw new EntityNotFound();
        }

        if (!lot0.getOwner().equals(currentUser)) {
            throw new PermissionDenied();
        }

        if (!location.getOwner().equals(currentUser)) {
            throw new PermissionDenied();
        }

        Lot lot = lot0.move(currentUser, uuidGenerator, location);
        lotRepository.save(lot);

        return lotToEmber.convert(lot, 1, new THashMap<UUID, Object>());
    }

	public LotSplitResult split(UUID source, Long count, User currentUser, RequirementApiModel requirement0) throws PermissionDenied, EntityNotFound, OperationNotPermitted {
		Lot lot = lotRepository.findByUuid(source);
        Requirement requirement = null;

        if (requirement0 != null) {
            requirement = requirementRepository.findByUuid(requirement0.getId());
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

		Lot.SplitResult result = lot.split(count, currentUser, uuidGenerator, requirement);
		if (result == null) {
			throw new OperationNotPermitted();
		}

		lotRepository.save(result.getRequested());
		if (result.getRemainder() != null) {
			lotRepository.save(result.getRemainder());
		}

		return new LotSplitResult(lotToEmber.convert(result.getRequested(), 1, cache),
				lotToEmber.convert(result.getRemainder(), 1, cache));
	}

	public LotApiModel destroy(UUID source, User currentUser) throws PermissionDenied, EntityNotFound {
		Lot lot = lotRepository.findByUuid(source);

		if (lot == null) {
			throw new EntityNotFound();
		}

		if (!lot.getOwner().equals(currentUser)) {
			throw new PermissionDenied();
		}

		Map<UUID, Object> cache = new THashMap<>();

		Lot updated = lot.destroy(currentUser, uuidGenerator);
		lotRepository.save(updated);

		return lotToEmber.convert(updated, 1, cache);
	}

    public LotApiModel solder(UUID source, User currentUser, RequirementApiModel requirement0) throws PermissionDenied, EntityNotFound, OperationNotPermitted {
        Lot lot = lotRepository.findByUuid(source);
        Requirement requirement = null;

        if (requirement0 != null) {
            requirement = requirementRepository.findByUuid(requirement0.getId());
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

        Lot updated = lot.solder(currentUser, uuidGenerator, requirement);
        lotRepository.save(updated);

        return lotToEmber.convert(updated, 1, cache);
    }

    public LotApiModel unsolder(UUID source, User currentUser) throws PermissionDenied, EntityNotFound, OperationNotPermitted {
        Lot lot = lotRepository.findByUuid(source);

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

        Lot updated = lot.unsolder(currentUser, uuidGenerator);
        lotRepository.save(updated);

        return lotToEmber.convert(updated, 1, cache);
    }

    public LotApiModel assign(UUID source, User currentUser, RequirementApiModel requirement0) throws OperationNotPermitted, PermissionDenied, EntityNotFound {
        Lot lot = lotRepository.findByUuid(source);
        Requirement requirement = requirementRepository.findByUuid(requirement0.getId());

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

        Lot updated = lot.assign(currentUser, uuidGenerator, requirement);
        lotRepository.save(updated);

        return lotToEmber.convert(updated, 1, cache);
    }

    public LotApiModel unassign(UUID source, User currentUser) throws OperationNotPermitted, PermissionDenied, EntityNotFound {
        Lot lot = lotRepository.findByUuid(source);

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

        Lot updated = lot.unassign(currentUser, uuidGenerator);
        lotRepository.save(updated);

        return lotToEmber.convert(updated, 1, cache);
    }
}
