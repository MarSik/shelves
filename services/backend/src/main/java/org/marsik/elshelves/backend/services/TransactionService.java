package org.marsik.elshelves.backend.services;

import org.marsik.elshelves.api.entities.TransactionApiModel;
import org.marsik.elshelves.backend.controllers.exceptions.OperationNotPermitted;
import org.marsik.elshelves.backend.entities.Box;
import org.marsik.elshelves.backend.entities.OwnedEntity;
import org.marsik.elshelves.backend.entities.Purchase;
import org.marsik.elshelves.backend.entities.Source;
import org.marsik.elshelves.backend.entities.Transaction;
import org.marsik.elshelves.backend.entities.Type;
import org.marsik.elshelves.backend.entities.User;
import org.marsik.elshelves.backend.entities.converters.CachingConverter;
import org.marsik.elshelves.backend.entities.converters.EmberToTransaction;
import org.marsik.elshelves.backend.entities.converters.TransactionToEmber;
import org.marsik.elshelves.backend.repositories.BoxRepository;
import org.marsik.elshelves.backend.repositories.PurchaseRepository;
import org.marsik.elshelves.backend.repositories.SourceRepository;
import org.marsik.elshelves.backend.repositories.TransactionRepository;
import org.marsik.elshelves.backend.repositories.TypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

@Service
public class TransactionService extends AbstractRestService<TransactionRepository, Transaction, TransactionApiModel> {
	@Autowired
	SourceRepository sourceRepository;

	@Autowired
	PurchaseRepository purchaseRepository;

	@Autowired
	BoxRepository boxRepository;

	@Autowired
	TypeRepository typeRepository;

	@Autowired
	public TransactionService(TransactionRepository repository,
							  TransactionToEmber dbToRest,
							  EmberToTransaction restToDb,
							  UuidGenerator uuidGenerator) {
		super(repository, dbToRest, restToDb, uuidGenerator);
	}

	@Override
	protected Transaction createEntity(TransactionApiModel dto, User currentUser) {
		Transaction t = super.createEntity(dto, currentUser);

		for (Purchase p: t.getItems()) {
			if (p.getOwner() == null) {
				p.setOwner(currentUser);
			}

			if (p.getUuid() == null) {
				p.setUuid(getUuidGenerator().generate());
			}
		}

		return t;
	}

	@Override
	protected void deleteEntity(Transaction entity) throws OperationNotPermitted {
		if (!entity.getItems().isEmpty()) {
			throw new OperationNotPermitted();
		}

		super.deleteEntity(entity);
	}

	@Override
	protected int conversionDepth() {
		return 3;
	}
}
