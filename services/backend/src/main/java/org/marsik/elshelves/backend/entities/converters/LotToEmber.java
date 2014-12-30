package org.marsik.elshelves.backend.entities.converters;

import org.marsik.elshelves.api.entities.LotApiModel;
import org.marsik.elshelves.backend.entities.Lot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;

@Service
public class LotToEmber implements CachingConverter<Lot, LotApiModel, UUID> {
	@Autowired
	UserToEmber userToEmber;

	@Autowired
	BoxToEmber boxToEmber;

	@Autowired
	TypeToEmber typeToEmber;

	protected LotApiModel createEntity() {
		return new LotApiModel();
	}

	public LotApiModel convert(Lot object, LotApiModel entity, int nested, Map<UUID, Object> cache) {
		entity.setId(object.getUuid());
		entity.setCount(object.getCount());
		entity.setCreated(object.getCreated());
		entity.setAction(object.getAction());

		if (nested == 0) {
			return entity;
		}

		entity.setType(typeToEmber.convert(object.getType(), nested - 1, cache));
		entity.setLocation(boxToEmber.convert(object.getLocation(), nested - 1, cache));
		entity.setPerformedBy(userToEmber.convert(object.getPerformedBy(), nested - 1, cache));
		entity.setPrevious(convert(object.getPrevious(), nested - 1, cache));
		entity.setBelongsTo(userToEmber.convert(object.getOwner(), nested - 1, cache));

		entity.setNext(new ArrayList<LotApiModel>());

		if (object.getNext() != null) {
			for (Lot l : object.getNext()) {
				entity.getNext().add(convert(l, nested - 1, cache));
			}
		}

		return entity;
	}

	@Override
	public LotApiModel convert(Lot object, int nested, Map<UUID, Object> cache) {
		if (object == null) {
			return null;
		}

		if (cache.containsKey(object.getUuid())) {
			return (LotApiModel)cache.get(object.getUuid());
		}

		LotApiModel entity = createEntity();
		if (nested > 0
				&& object.getUuid() != null) {
			cache.put(object.getUuid(), entity);
		}
		return convert(object, entity, nested, cache);
	}
}
