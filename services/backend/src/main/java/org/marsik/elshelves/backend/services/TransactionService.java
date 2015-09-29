package org.marsik.elshelves.backend.services;

import org.joda.time.DateTime;
import org.marsik.elshelves.api.entities.TransactionApiModel;
import org.marsik.elshelves.backend.controllers.exceptions.OperationNotPermitted;
import org.marsik.elshelves.backend.entities.Purchase;
import org.marsik.elshelves.backend.entities.Transaction;
import org.marsik.elshelves.backend.entities.User;
import org.marsik.elshelves.backend.entities.converters.EmberToTransaction;
import org.marsik.elshelves.backend.entities.converters.TransactionToEmber;
import org.marsik.elshelves.backend.repositories.BoxRepository;
import org.marsik.elshelves.backend.repositories.PurchaseRepository;
import org.marsik.elshelves.backend.repositories.SourceRepository;
import org.marsik.elshelves.backend.repositories.TransactionRepository;
import org.marsik.elshelves.backend.repositories.TypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

		Transaction t = super.createEntity(dto, currentUser);

		for (Purchase p: t.getItems()) {
			if (p.getOwner() == null) {
				p.setOwner(currentUser);
			}

			if (p.getId() == null) {
				p.setId(getUuidGenerator().generate());
			}
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
}
