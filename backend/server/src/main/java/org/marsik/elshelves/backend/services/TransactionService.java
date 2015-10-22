package org.marsik.elshelves.backend.services;

import org.joda.time.DateTime;
import org.marsik.elshelves.backend.controllers.exceptions.OperationNotPermitted;
import org.marsik.elshelves.backend.entities.Purchase;
import org.marsik.elshelves.backend.entities.Transaction;
import org.marsik.elshelves.backend.entities.User;
import org.marsik.elshelves.backend.repositories.BoxRepository;
import org.marsik.elshelves.backend.repositories.PurchaseRepository;
import org.marsik.elshelves.backend.repositories.SourceRepository;
import org.marsik.elshelves.backend.repositories.TransactionRepository;
import org.marsik.elshelves.backend.repositories.TypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;

@Service
public class TransactionService extends AbstractRestService<TransactionRepository, Transaction> {
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
	public TransactionService(TransactionRepository repository,
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

		RelinkService.RelinkContext context = relinkService.newRelinker();
		context.addToCache(t);
		context.addToCache(currentUser);

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
	protected void deleteEntity(Transaction entity) throws OperationNotPermitted {
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
		for (Purchase p: entity.getItems()) {
			saveOrUpdate(p);
		}

		return super.save(entity);
	}
}
