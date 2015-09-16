package org.marsik.elshelves.backend.entities.converters;

import gnu.trove.set.hash.THashSet;
import org.joda.time.DateTime;
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

    @Autowired
    NamedObjectToEmber namedObjectToEmber;

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
		namedObjectToEmber.convert(object, model, nested, cache);

		if (nested == 0) {
			return model;
		}

		model.setDate(new DateTime(object.getDate()));
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
