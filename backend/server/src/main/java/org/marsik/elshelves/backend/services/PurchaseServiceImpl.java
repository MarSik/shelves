package org.marsik.elshelves.backend.services;

import org.marsik.elshelves.backend.controllers.exceptions.OperationNotPermitted;
import org.marsik.elshelves.backend.entities.Purchase;
import org.marsik.elshelves.backend.entities.Type;
import org.marsik.elshelves.backend.entities.User;
import org.marsik.elshelves.backend.entities.converters.LotToEmber;
import org.marsik.elshelves.backend.repositories.PurchaseRepository;
import org.marsik.elshelves.backend.repositories.TransactionRepository;
import org.marsik.elshelves.backend.repositories.TypeRepository;
import org.modelmapper.internal.util.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.stream.Collectors;

@Service
public class PurchaseServiceImpl extends AbstractRestService<PurchaseRepository, Purchase> implements PurchaseService {
	@Autowired
	TypeRepository typeRepository;

	@Autowired
	TransactionRepository transactionRepository;

	@Autowired
	PurchaseRepository purchaseRepository;

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

	@Override
	public Collection<Purchase> findUndelivered(User user, Type type) {
		return Lists.from(purchaseRepository.findUndelivered(user, type).iterator()).stream()
				.map(purchaseRepository::findById)
				.collect(Collectors.toList());
	}
}
