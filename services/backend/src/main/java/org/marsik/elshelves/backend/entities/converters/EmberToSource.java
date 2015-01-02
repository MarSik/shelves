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
	EmberToNamedObject emberToNamedObject;

	@Override
	public Source convert(SourceApiModel object, int nested, Map<UUID, Object> cache) {
		if (object == null) {
			return null;
		}

		if (cache.containsKey(object.getId())) {
			return (Source)cache.get(object.getId());
		}

		Source model = new Source();
		if (nested > 0
				&& object.getId() != null) {
			cache.put(object.getId(), model);
		}
		return convert(object, model, nested, cache);
	}

	@Override
	public Source convert(SourceApiModel object, Source model, int nested, Map<UUID, Object> cache) {
		emberToNamedObject.convert(object, model, nested, cache);
		model.setUrl(object.getUrl());
		return model;
	}
}
