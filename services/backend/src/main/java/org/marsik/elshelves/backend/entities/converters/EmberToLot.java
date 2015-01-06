package org.marsik.elshelves.backend.entities.converters;

import org.marsik.elshelves.api.entities.LotApiModel;
import org.marsik.elshelves.backend.entities.Lot;
import org.marsik.elshelves.backend.entities.Purchase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
public class EmberToLot implements CachingConverter<LotApiModel, Lot, UUID> {
	@Autowired
	EmberToLotBase emberToLotBase;

	@Autowired
	EmberToBox emberToBox;

	@Autowired
	EmberToPurchase emberToPurchase;

	@Autowired
	EmberToUser emberToUser;

	@Autowired
	EmberToRequirement emberToRequirement;

	@Override
	public Lot convert(LotApiModel object, int nested, Map<UUID, Object> cache) {
		if (object == null) {
			return null;
		}

		if (cache.containsKey(object.getId())) {
			return (Lot)cache.get(object.getId());
		}

		Lot entity = new Lot();
		if (nested > 0
				&& object.getId() != null) {
			cache.put(object.getId(), entity);
		}
		return convert(object, entity, nested, cache);
	}

	@Override
	public Lot convert(LotApiModel object, Lot model, int nested, Map<UUID, Object> cache) {
		emberToLotBase.convert(object, model, nested, cache);

		model.setAction(object.getAction());
		model.setLocation(emberToBox.convert(object.getLocation(), nested, cache));
		model.setPurchase(emberToPurchase.convert(object.getPurchase(), nested, cache));
		model.setPrevious(convert(object.getPrevious(), nested, cache));
		model.setPerformedBy(emberToUser.convert(object.getPerformedBy(), nested, cache));
		model.setUsedBy(emberToRequirement.convert(object.getUsedBy(), nested, cache));

		return model;
	}
}
