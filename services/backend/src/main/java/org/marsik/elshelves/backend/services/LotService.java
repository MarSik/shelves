package org.marsik.elshelves.backend.services;

import gnu.trove.map.hash.THashMap;
import org.marsik.elshelves.api.entities.LotApiModel;
import org.marsik.elshelves.backend.controllers.exceptions.EntityNotFound;
import org.marsik.elshelves.backend.controllers.exceptions.OperationNotPermitted;
import org.marsik.elshelves.backend.controllers.exceptions.PermissionDenied;
import org.marsik.elshelves.backend.dtos.LotSplitResult;
import org.marsik.elshelves.backend.entities.Lot;
import org.marsik.elshelves.backend.entities.User;
import org.marsik.elshelves.backend.entities.converters.EmberToLot;
import org.marsik.elshelves.backend.entities.converters.LotToEmber;
import org.marsik.elshelves.backend.repositories.LotRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class LotService {
	LotRepository lotRepository;
	LotToEmber lotToEmber;
	EmberToLot emberToLot;
	UuidGenerator uuidGenerator;

	@Autowired
	public LotService(LotRepository lotRepository, LotToEmber lotToEmber, EmberToLot emberToLot, UuidGenerator uuidGenerator) {
		this.lotRepository = lotRepository;
		this.lotToEmber = lotToEmber;
		this.emberToLot = emberToLot;
		this.uuidGenerator = uuidGenerator;
	}

	public Collection<LotApiModel> getAll(User currentUser) {
		Collection<LotApiModel> lots = new ArrayList<>();
		Map<UUID, Object> cache = new THashMap<>();
		for (Lot l: lotRepository.findAll()) {
			lots.add(lotToEmber.convert(l, 1, cache));
		}

		return lots;
	}

	public LotApiModel get(UUID id, User currentUser) throws PermissionDenied, EntityNotFound {
		Lot lot = lotRepository.getLotByUuid(id);

		if (lot == null) {
			throw new EntityNotFound();
		}

		if (!lot.getOwner().equals(currentUser)) {
			throw new PermissionDenied();
		}

		return lotToEmber.convert(lot, 1, new THashMap<UUID, Object>());
	}

	public Iterable<LotApiModel> getNext(UUID id, User currentUser) throws PermissionDenied, EntityNotFound {
		Lot lot = lotRepository.getLotByUuid(id);

		if (lot == null) {
			throw new EntityNotFound();
		}

		if (!lot.getOwner().equals(currentUser)) {
			throw new PermissionDenied();
		}

		Map<UUID, Object> cache = new THashMap<>();

		List<LotApiModel> lots = new ArrayList<>();
		for (Lot l: lot.getNext()) {
			lots.add(lotToEmber.convert(l, 1, cache));
		}

		return lots;
	}

	public LotSplitResult split(UUID source, Long count, User currentUser) throws PermissionDenied, EntityNotFound, OperationNotPermitted {
		Lot lot = lotRepository.getLotByUuid(source);

		if (lot == null) {
			throw new EntityNotFound();
		}

		if (!lot.getOwner().equals(currentUser)) {
			throw new PermissionDenied();
		}

		Map<UUID, Object> cache = new THashMap<>();

		Lot.SplitResult result = lot.split(count, currentUser, uuidGenerator);
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
		Lot lot = lotRepository.getLotByUuid(source);

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

	public LotApiModel solder(UUID source, User currentUser) throws PermissionDenied, EntityNotFound {
		Lot lot = lotRepository.getLotByUuid(source);

		if (lot == null) {
			throw new EntityNotFound();
		}

		if (!lot.getOwner().equals(currentUser)) {
			throw new PermissionDenied();
		}

		Map<UUID, Object> cache = new THashMap<>();

		Lot updated = lot.solder(currentUser, uuidGenerator);
		lotRepository.save(updated);

		return lotToEmber.convert(updated, 1, cache);
	}
}
