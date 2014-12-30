package org.marsik.elshelves.backend.services;

import org.marsik.elshelves.api.entities.TransactionApiModel;
import org.marsik.elshelves.backend.controllers.exceptions.OperationNotPermitted;
import org.marsik.elshelves.backend.entities.Purchase;
import org.marsik.elshelves.backend.entities.Source;
import org.marsik.elshelves.backend.entities.Transaction;
import org.marsik.elshelves.backend.entities.converters.CachingConverter;
import org.marsik.elshelves.backend.entities.converters.EmberToTransaction;
import org.marsik.elshelves.backend.entities.converters.TransactionToEmber;
import org.marsik.elshelves.backend.repositories.PurchaseRepository;
import org.marsik.elshelves.backend.repositories.SourceRepository;
import org.marsik.elshelves.backend.repositories.TransactionRepository;
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
	public TransactionService(TransactionRepository repository,
							  TransactionToEmber dbToRest,
							  EmberToTransaction restToDb,
							  UuidGenerator uuidGenerator) {
		super(repository, dbToRest, restToDb, uuidGenerator);
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
		return 2;
	}

	@Override
	protected Transaction relink(Transaction entity) {
		if (entity.getSource() != null) {
			Source source = sourceRepository.getSourceByUuid(entity.getSource().getUuid());
			entity.setSource(source);
		}

		if (entity.getItems() != null) {
			Collection<Purchase> items = new ArrayList<>();
			for (Purchase p: entity.getItems()) {
				if (p.getUuid() == null) {
					items.add(p);
					continue;
				}
				Purchase linked = purchaseRepository.getPurchaseByUuid(p.getUuid());
				items.add(linked);
			}

			entity.getItems().clear();
			entity.getItems().addAll(items);
		}

		return super.relink(entity);
	}
}
