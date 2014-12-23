package org.marsik.elshelves.backend.entities.converters;

import org.marsik.elshelves.api.entities.PurchaseApiModel;
import org.marsik.elshelves.backend.entities.Purchase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
public class PurchaseToEmber implements CachingConverter<Purchase, PurchaseApiModel, UUID> {
	@Autowired
	LotToEmber lotToEmber;


	@Override
	public PurchaseApiModel convert(Purchase object, int nested, Map<UUID, Object> cache) {
		if (object == null) {
			return null;
		}

		if (cache.containsKey(object.getUuid())) {
			return (PurchaseApiModel)cache.get(object.getUuid());
		}

		PurchaseApiModel model = new PurchaseApiModel();
		cache.put(object.getUuid(), model);

		return convert(object, model, nested, cache);
	}

	@Override
	public PurchaseApiModel convert(Purchase object, PurchaseApiModel model, int nested, Map<UUID, Object> cache) {
		lotToEmber.convert(object, model, nested, cache);
		model.setSinglePrice(object.getSinglePrice());
		model.setTotalPrice(object.getTotalPrice());
		model.setVat(object.getVat());
		model.setVatIncluded(object.getVatIncluded());
		return model;
	}
}
