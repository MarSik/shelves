package org.marsik.elshelves.backend.entities.converters;

import org.marsik.elshelves.api.entities.SourceApiModel;
import org.marsik.elshelves.backend.entities.Source;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
public class SourceToEmber implements CachingConverter<Source, SourceApiModel, UUID> {
	@Autowired
	UserToEmber userToEmber;

	@Override
	public SourceApiModel convert(Source object, int nested, Map<UUID, Object> cache) {
		if (object == null) {
			return null;
		}

		if (cache.containsKey(object.getUuid())) {
			return (SourceApiModel)cache.get(object.getUuid());
		}

		SourceApiModel model = new SourceApiModel();
		if (nested > 0) {
			cache.put(object.getUuid(), model);
		}
		return convert(object, model, nested, cache);
	}

	@Override
	public SourceApiModel convert(Source object, SourceApiModel model, int nested, Map<UUID, Object> cache) {
		model.setName(object.getName());
		model.setUrl(object.getUrl());
		model.setId(object.getUuid());

		if (nested == 0) {
			return model;
		}

		model.setBelongsTo(userToEmber.convert(object.getOwner(), nested - 1, cache));

		return model;
	}
}
