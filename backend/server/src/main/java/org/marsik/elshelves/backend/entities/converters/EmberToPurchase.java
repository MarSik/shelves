package org.marsik.elshelves.backend.entities.converters;

import gnu.trove.set.hash.THashSet;
import org.marsik.elshelves.api.entities.LotApiModel;
import org.marsik.elshelves.api.entities.PolymorphicRecord;
import org.marsik.elshelves.api.entities.PurchaseApiModel;
import org.marsik.elshelves.backend.entities.Lot;
import org.marsik.elshelves.backend.entities.Purchase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
public class EmberToPurchase implements CachingConverter<PurchaseApiModel, Purchase, UUID> {
	@Autowired
	EmberToTransaction emberToTransaction;

	@Autowired
	EmberToLot emberToLot;

	@Autowired
	EmberToType emberToType;

	@Override
	public Purchase convert(PurchaseApiModel object, int nested, Map<UUID, Object> cache) {
		if (object == null) {
			return null;
		}

		if (cache.containsKey(object.getId())) {
			return (Purchase)cache.get(object.getId());
		}

		Purchase entity = new Purchase();
		if (object.getId() != null) {
			cache.put(object.getId(), entity);
		}
		return convert(object, entity, nested, cache);
	}

	@Override
	public Purchase convert(PurchaseApiModel object, Purchase model, int nested, Map<UUID, Object> cache) {
		model.setId(object.getId());
		model.setCount(object.getCount());
		model.setSinglePrice(object.getSinglePrice());
		model.setTotalPrice(object.getTotalPrice());
		model.setVat(object.getVat());
		model.setVatIncluded(object.getVatIncluded());
		model.setTransaction(emberToTransaction.convert(object.getTransaction(), nested, cache));
		model.setType(emberToType.convert(object.getType(), nested, cache));

		if (object.getLots() != null) {
			model.setLots(new THashSet<Lot>());
			for (PolymorphicRecord l: object.getLots()) {
				model.addLot(new Lot(l.getId()));
			}
		}

		return model;
	}
}
