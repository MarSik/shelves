package org.marsik.elshelves.backend.entities.converters;

import org.marsik.elshelves.api.entities.PartTypeApiModel;
import org.marsik.elshelves.backend.entities.Type;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
public class EmberToType implements CachingConverter<PartTypeApiModel, Type, UUID> {
	@Autowired
	EmberToUser emberToUser;

	@Autowired
	EmberToFootprint emberToFootprint;

	@Override
	public Type convert(PartTypeApiModel object, int nested, Map<UUID, Object> cache) {
		if (object == null) {
			return null;
		}

		if (cache.containsKey(object.getId())) {
			return (Type)cache.get(object.getId());
		}

		Type model = new Type();
		cache.put(object.getId(), model);

		return convert(object, model, nested, cache);
	}

	@Override
	public Type convert(PartTypeApiModel object, Type model, int nested, Map<UUID, Object> cache) {
		model.setUuid(object.getId());
		model.setOwner(emberToUser.convert(object.getBelongsTo(), 1, cache));
		model.setName(object.getName());
		model.setDescription(object.getDescription());
		model.setFootprint(emberToFootprint.convert(object.getFootprint(), 1, cache));
		model.setVendor(object.getVendor());
		model.setVendorId(object.getVendorId());
		return model;
	}
}
