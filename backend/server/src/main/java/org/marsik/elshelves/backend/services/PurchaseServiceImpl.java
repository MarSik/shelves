package org.marsik.elshelves.backend.services;

import org.marsik.elshelves.backend.controllers.exceptions.OperationNotPermitted;
import org.marsik.elshelves.backend.entities.Purchase;
import org.marsik.elshelves.backend.entities.User;
import org.marsik.elshelves.backend.entities.converters.LotToEmber;
import org.marsik.elshelves.backend.repositories.PurchaseRepository;
import org.marsik.elshelves.backend.repositories.TransactionRepository;
import org.marsik.elshelves.backend.repositories.TypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;

@Service
public class PurchaseServiceImpl extends AbstractRestService<PurchaseRepository, Purchase> implements PurchaseService {
	@Autowired
	TypeRepository typeRepository;

	@Autowired
	TransactionRepository transactionRepository;

	@Autowired
	LotToEmber lotToEmber;

	@Autowired
	public PurchaseServiceImpl(PurchaseRepository repository,
			UuidGenerator uuidGenerator) {
		super(repository, uuidGenerator);
	}

	@Override
	public void deleteEntity(Purchase entity) throws OperationNotPermitted {
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

	@Override
	protected Purchase save(Purchase entity) {
		Purchase p = super.save(entity);
		saveOrUpdate(p.getSku());
		return p;
	}
}
