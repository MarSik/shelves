package org.marsik.elshelves.backend.entities.converters;

import org.marsik.elshelves.api.entities.PartGroupApiModel;
import org.marsik.elshelves.backend.entities.Group;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
public class EmberToGroup implements CachingConverter<PartGroupApiModel, Group, UUID> {
	@Autowired
	EmberToUser emberToUser;

	@Override
	public Group convert(PartGroupApiModel object, int nested, Map<UUID, Object> cache) {
		if (object == null) {
			return null;
		}

		if (cache.containsKey(object.getId())) {
			return (Group)cache.get(object.getId());
		}

		Group model = new Group();
		cache.put(object.getId(), model);

		return convert(object, model, nested, cache);
	}

	@Override
	public Group convert(PartGroupApiModel object, Group model, int nested, Map<UUID, Object> cache) {
		model.setUuid(object.getId());
		model.setOwner(emberToUser.convert(object.getBelongsTo(), 1, cache));
		model.setName(object.getName());
		model.setParent(convert(object.getParent(), 1, cache));
		return model;
	}
}
