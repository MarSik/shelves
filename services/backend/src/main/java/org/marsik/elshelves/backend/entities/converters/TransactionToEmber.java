package org.marsik.elshelves.backend.entities.converters;

import gnu.trove.set.hash.THashSet;
import org.marsik.elshelves.api.entities.PurchaseApiModel;
import org.marsik.elshelves.api.entities.TransactionApiModel;
import org.marsik.elshelves.backend.entities.Purchase;
import org.marsik.elshelves.backend.entities.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
public class TransactionToEmber implements CachingConverter<Transaction, TransactionApiModel, UUID> {
	@Autowired
	PurchaseToEmber purchaseToEmber;

	@Autowired
	SourceToEmber sourceToEmber;

	@Autowired
	UserToEmber userToEmber;

	@Override
	public TransactionApiModel convert(Transaction object, int nested, Map<UUID, Object> cache) {
		if (object == null) {
			return null;
		}

		if (cache.containsKey(object.getUuid())) {
			return (TransactionApiModel)cache.get(object.getUuid());
		}

		TransactionApiModel entity = new TransactionApiModel();
		if (nested > 0
				&& object.getUuid() != null) {
			cache.put(object.getUuid(), entity);
		}

		return convert(object, entity, nested, cache);
	}

	@Override
	public TransactionApiModel convert(Transaction object, TransactionApiModel model, int nested, Map<UUID, Object> cache) {
		model.setId(object.getUuid());

		if (nested == 0) {
			return model;
		}

		model.setName(object.getName());
		model.setDate(object.getDate());

		model.setBelongsTo(userToEmber.convert(object.getOwner(), nested - 1, cache));
		model.setSource(sourceToEmber.convert(object.getSource(), nested - 1, cache));

		if (object.getItems() != null) {
			model.setItems(new THashSet<PurchaseApiModel>());
			for (Purchase p : object.getItems()) {
				model.getItems().add(purchaseToEmber.convert(p, nested - 1, cache));
			}
		}

		return model;
	}
}
