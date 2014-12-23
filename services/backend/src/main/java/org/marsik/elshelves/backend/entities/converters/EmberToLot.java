package org.marsik.elshelves.backend.entities.converters;

import org.marsik.elshelves.api.entities.LotApiModel;
import org.marsik.elshelves.backend.entities.Lot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
public class EmberToLot implements CachingConverter<LotApiModel, Lot, UUID> {
	@Autowired
	EmberToUser emberToUser;

	@Autowired
	EmberToType emberToType;

	@Autowired
	EmberToBox emberToBox;

	@Override
	public Lot convert(LotApiModel object, int nested, Map<UUID, Object> cache) {
		if (object == null) {
			return null;
		}

		if (cache.containsKey(object.getId())) {
			return (Lot)cache.get(object.getId());
		}

		Lot entity = new Lot();
		cache.put(object.getId(), entity);

		return convert(object, entity, nested, cache);
	}

	@Override
	public Lot convert(LotApiModel object, Lot model, int nested, Map<UUID, Object> cache) {
		model.setOwner(emberToUser.convert(object.getBelongsTo(), 1, cache));
		model.setUuid(object.getId());
		model.setCount(object.getCount());
		model.setCreated(object.getCreated());
		model.setType(emberToType.convert(object.getType(), 1, cache));
		model.setAction(object.getAction());
		model.setPerformedBy(emberToUser.convert(object.getPerformedBy(), 1, cache));
		model.setLocation(emberToBox.convert(object.getLocation(), 1, cache));
		return model;
	}
}
