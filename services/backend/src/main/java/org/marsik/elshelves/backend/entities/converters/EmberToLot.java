package org.marsik.elshelves.backend.entities.converters;

import gnu.trove.set.hash.THashSet;
import org.marsik.elshelves.api.entities.LotApiModel;
import org.marsik.elshelves.backend.entities.Lot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
public class EmberToLot implements CachingConverter<LotApiModel, Lot, UUID> {
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
		if (object.getId() != null) {
			cache.put(object.getId(), entity);
		}
		return convert(object, entity, nested, cache);
	}

	@Override
	public Lot convert(LotApiModel object, Lot model, int nested, Map<UUID, Object> cache) {
		model.setCount(object.getCount());

        model.setExpiration(object.getExpiration());
		model.setStatus(object.getStatus());
		model.setLocation(emberToBox.convert(object.getLocation(), nested, cache));
		model.setPurchase(emberToPurchase.convert(object.getPurchase(), nested, cache));
		model.setUsedBy(emberToRequirement.convert(object.getUsedBy(), nested, cache));
		model.setSerials(object.getSerials());

		if (model.getSerials() == null) {
			model.setSerials(new THashSet<>());
		}

		if (object.getSerial() != null
				&& !object.getSerial().isEmpty()) {
			model.getSerials().add(object.getSerial());
		}

		return model;
	}
}
