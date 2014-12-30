package org.marsik.elshelves.backend.services;

import org.marsik.elshelves.api.entities.PurchaseApiModel;
import org.marsik.elshelves.backend.controllers.exceptions.OperationNotPermitted;
import org.marsik.elshelves.backend.entities.Purchase;
import org.marsik.elshelves.backend.entities.Source;
import org.marsik.elshelves.backend.entities.Transaction;
import org.marsik.elshelves.backend.entities.Type;
import org.marsik.elshelves.backend.entities.converters.CachingConverter;
import org.marsik.elshelves.backend.entities.converters.EmberToPurchase;
import org.marsik.elshelves.backend.entities.converters.PurchaseToEmber;
import org.marsik.elshelves.backend.repositories.PurchaseRepository;
import org.marsik.elshelves.backend.repositories.SourceRepository;
import org.marsik.elshelves.backend.repositories.TransactionRepository;
import org.marsik.elshelves.backend.repositories.TypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.util.UUID;

@Service
public class PurchaseService extends AbstractRestService<PurchaseRepository, Purchase, PurchaseApiModel> {
	@Autowired
	TypeRepository typeRepository;

	@Autowired
	TransactionRepository transactionRepository;

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
	protected Purchase updateEntity(Purchase entity, PurchaseApiModel dto) throws IllegalAccessException, InvocationTargetException, OperationNotPermitted {
		if (!entity.canBeUpdated()) {
			throw new OperationNotPermitted();
		}

		return super.updateEntity(entity, dto);
	}

	@Override
	protected Purchase relink(Purchase entity) {
		if (entity.getTransaction() != null) {
			Transaction transaction = transactionRepository.getTransactionByUuid(entity.getTransaction().getUuid());
			entity.setTransaction(transaction);
		}

		if (entity.getType() != null) {
			Type type = typeRepository.getTypeByUuid(entity.getType().getUuid());
			entity.setType(type);
		}

		return super.relink(entity);
	}
}
