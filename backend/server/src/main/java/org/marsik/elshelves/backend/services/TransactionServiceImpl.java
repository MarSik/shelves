package org.marsik.elshelves.backend.services;

import org.joda.time.DateTime;
import org.marsik.elshelves.backend.controllers.exceptions.OperationNotPermitted;
import org.marsik.elshelves.backend.entities.Purchase;
import org.marsik.elshelves.backend.entities.Transaction;
import org.marsik.elshelves.backend.entities.User;
import org.marsik.elshelves.backend.interfaces.Relinker;
import org.marsik.elshelves.backend.repositories.BoxRepository;
import org.marsik.elshelves.backend.repositories.PurchaseRepository;
import org.marsik.elshelves.backend.repositories.SourceRepository;
import org.marsik.elshelves.backend.repositories.TransactionRepository;
import org.marsik.elshelves.backend.repositories.TypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TransactionServiceImpl extends AbstractRestService<TransactionRepository, Transaction>
		implements TransactionService {
	@Autowired
	SourceRepository sourceRepository;

	@Autowired
	PurchaseRepository purchaseRepository;

	@Autowired
	BoxRepository boxRepository;

	@Autowired
	TypeRepository typeRepository;

	@Autowired
	PurchaseService purchaseService;

	@Autowired
	public TransactionServiceImpl(TransactionRepository repository,
			UuidGenerator uuidGenerator) {
		super(repository, uuidGenerator);
	}

	@Override
	protected Transaction createEntity(Transaction dto, User currentUser) {
		if (dto.getDate() == null) {
			dto.setDate(new DateTime());
		}

		// Create empty transaction, by recording the items and then
		// clearing the list (we have to give them an UUID first before reinserting)
		List<Purchase> items = new ArrayList<>(dto.getItems());
		dto.getItems().clear();

		Transaction t = super.createEntity(dto, currentUser);

		Relinker context = relinkService.newRelinker();
		context.currentUser(currentUser)
				.addToCache(t)
				.addToCache(currentUser);

		// Reinsert all items
		for (Purchase p: items) {
			p = context.fixUuid(p);

			context
					.ensureOwner(p, currentUser)
					.relink(p);

			p.setTransaction(t);
		}

		return t;
	}

	@Override
	public void deleteEntity(Transaction entity) throws OperationNotPermitted {
		for (Purchase p: entity.getItems()) {
			purchaseService.deleteEntity(p);
		}

		super.deleteEntity(entity);
	}

    @Override
    protected Iterable<Transaction> getAllEntities(User currentUser) {
        return getRepository().findByOwner(currentUser);
    }

	@Override
	protected Transaction save(Transaction entity) {
		Transaction t = super.save(entity);

		for (Purchase p: t.getItems()) {
			saveOrUpdate(p.getSku());
			saveOrUpdate(p);
		}

		return t;
	}
}
