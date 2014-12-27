package org.marsik.elshelves.backend.entities.converters;

import org.marsik.elshelves.api.entities.SourceApiModel;
import org.marsik.elshelves.backend.entities.Source;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
public class EmberToSource implements CachingConverter<SourceApiModel, Source, UUID> {
	@Autowired
	EmberToUser emberToUser;

	@Override
	public Source convert(SourceApiModel object, int nested, Map<UUID, Object> cache) {
		if (object == null) {
			return null;
		}

		if (cache.containsKey(object.getId())) {
			return (Source)cache.get(object.getId());
		}

		Source model = new Source();
		if (nested > 0) {
			cache.put(object.getId(), model);
		}
		return convert(object, model, nested, cache);
	}

	@Override
	public Source convert(SourceApiModel object, Source model, int nested, Map<UUID, Object> cache) {
		model.setUuid(object.getId());
		model.setName(object.getName());
		model.setUrl(object.getUrl());
		model.setOwner(emberToUser.convert(object.getBelongsTo(), 1, cache));
		return model;
	}
}
