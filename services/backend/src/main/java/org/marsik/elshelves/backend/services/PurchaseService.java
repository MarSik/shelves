package org.marsik.elshelves.backend.services;

import gnu.trove.map.hash.THashMap;
import org.marsik.elshelves.api.entities.LotApiModel;
import org.marsik.elshelves.api.entities.PurchaseApiModel;
import org.marsik.elshelves.backend.controllers.exceptions.EntityNotFound;
import org.marsik.elshelves.backend.controllers.exceptions.OperationNotPermitted;
import org.marsik.elshelves.backend.controllers.exceptions.PermissionDenied;
import org.marsik.elshelves.backend.entities.Lot;
import org.marsik.elshelves.backend.entities.Purchase;
import org.marsik.elshelves.backend.entities.Source;
import org.marsik.elshelves.backend.entities.Transaction;
import org.marsik.elshelves.backend.entities.Type;
import org.marsik.elshelves.backend.entities.User;
import org.marsik.elshelves.backend.entities.converters.CachingConverter;
import org.marsik.elshelves.backend.entities.converters.EmberToPurchase;
import org.marsik.elshelves.backend.entities.converters.LotToEmber;
import org.marsik.elshelves.backend.entities.converters.PurchaseToEmber;
import org.marsik.elshelves.backend.repositories.PurchaseRepository;
import org.marsik.elshelves.backend.repositories.SourceRepository;
import org.marsik.elshelves.backend.repositories.TransactionRepository;
import org.marsik.elshelves.backend.repositories.TypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class PurchaseService extends AbstractRestService<PurchaseRepository, Purchase, PurchaseApiModel> {
	@Autowired
	TypeRepository typeRepository;

	@Autowired
	TransactionRepository transactionRepository;

	@Autowired
	LotToEmber lotToEmber;

	@Autowired
	public PurchaseService(PurchaseRepository repository,
						   PurchaseToEmber dbToRest,
						   EmberToPurchase restToDb,
						   UuidGenerator uuidGenerator) {
		super(repository, dbToRest, restToDb, uuidGenerator);
	}

	@Override
	protected void deleteEntity(Purchase entity) throws OperationNotPermitted {
		if (!entity.canBeDeleted()) {
			throw new OperationNotPermitted();
		}

		super.deleteEntity(entity);
	}

	@Override
	protected Purchase updateEntity(Purchase entity, Purchase update) throws IllegalAccessException, InvocationTargetException, OperationNotPermitted {
		if (!entity.canBeUpdated()) {
			throw new OperationNotPermitted();
		}

		return super.updateEntity(entity, update);
	}

	public Iterable<LotApiModel> getNext(UUID id, User currentUser) throws PermissionDenied, EntityNotFound {
		Purchase purchase = getRepository().findByUuid(id);

		if (purchase == null) {
			throw new EntityNotFound();
		}

		if (!purchase.getOwner().equals(currentUser)) {
			throw new PermissionDenied();
		}

		Map<UUID, Object> cache = new THashMap<>();

		List<LotApiModel> lots = new ArrayList<>();
		for (Lot l: purchase.getNext()) {
			lots.add(lotToEmber.convert(l, 1, cache));
		}

		return lots;
	}
}
