package org.marsik.elshelves.backend.entities.converters;

import gnu.trove.set.hash.THashSet;
import org.marsik.elshelves.api.entities.LotApiModel;
import org.marsik.elshelves.api.entities.PurchaseApiModel;
import org.marsik.elshelves.backend.entities.Lot;
import org.marsik.elshelves.backend.entities.Purchase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
public class PurchaseToEmber implements CachingConverter<Purchase, PurchaseApiModel, UUID> {
	@Autowired
	NamedObjectToEmber namedObjectToEmber;

	@Autowired
	LotToEmber lotToEmber;

	@Autowired
	TypeToEmber typeToEmber;

	@Autowired
	TransactionToEmber transactionToEmber;

	@Override
	public PurchaseApiModel convert(Purchase object, int nested, Map<UUID, Object> cache) {
		if (object == null) {
			return null;
		}

		if (cache.containsKey(object.getId())) {
			return (PurchaseApiModel)cache.get(object.getId());
		}

		PurchaseApiModel model = new PurchaseApiModel();
		if (nested > 0
				&& object.getId() != null) {
			cache.put(object.getId(), model);
		}
		return convert(object, model, nested, cache);
	}

	@Override
	public PurchaseApiModel convert(Purchase object, PurchaseApiModel model, int nested, Map<UUID, Object> cache) {
		model.setCount(object.getCount());

		if (nested == 0) {
			return model;
		}

		model.setSinglePrice(object.getSinglePrice());
		model.setTotalPrice(object.getTotalPrice());
		model.setVat(object.getVat());
		model.setVatIncluded(object.getVatIncluded());
        model.setMissing(object.getMissing());

		model.setType(typeToEmber.convert(object.getType(), nested - 1, cache));
		model.setTransaction(transactionToEmber.convert(object.getTransaction(), nested - 1, cache));

		if (object.getLots() != null) {
			model.setLots(new THashSet<LotApiModel>());
			for (Lot l: object.getLots()) {
				model.getLots().add(lotToEmber.convert(l, nested - 1, cache));
			}
		}

		return model;
	}
}
