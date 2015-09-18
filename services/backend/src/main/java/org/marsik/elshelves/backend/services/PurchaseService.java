package org.marsik.elshelves.backend.services;

import org.marsik.elshelves.api.entities.PurchaseApiModel;
import org.marsik.elshelves.backend.controllers.exceptions.OperationNotPermitted;
import org.marsik.elshelves.backend.entities.Purchase;
import org.marsik.elshelves.backend.entities.User;
import org.marsik.elshelves.backend.entities.converters.EmberToPurchase;
import org.marsik.elshelves.backend.entities.converters.LotToEmber;
import org.marsik.elshelves.backend.entities.converters.PurchaseToEmber;
import org.marsik.elshelves.backend.repositories.PurchaseRepository;
import org.marsik.elshelves.backend.repositories.TransactionRepository;
import org.marsik.elshelves.backend.repositories.TypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;

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
	protected Purchase updateEntity(Purchase entity, Purchase update, User currentUser) throws IllegalAccessException, InvocationTargetException, OperationNotPermitted {
		if (!entity.canBeUpdated()) {
			throw new OperationNotPermitted();
		}

		return super.updateEntity(entity, update, currentUser);
	}

    @Override
    protected Iterable<Purchase> getAllEntities(User currentUser) {
        return getRepository().findByTransactionOwner(currentUser);
    }
}
