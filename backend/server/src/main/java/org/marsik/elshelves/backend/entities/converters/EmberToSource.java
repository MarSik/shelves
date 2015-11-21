package org.marsik.elshelves.backend.entities.converters;

import org.marsik.elshelves.api.entities.SourceApiModel;
import org.marsik.elshelves.backend.entities.Source;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Service
public class EmberToSource extends AbstractEmberToEntity<SourceApiModel, Source> {
	@Autowired
	EmberToNamedObject emberToNamedObject;

	public EmberToSource() {
		super(Source.class);
	}

	@Override
	public Source convert(String path, SourceApiModel object, Source model, Map<UUID, Object> cache, Set<String> include) {
		emberToNamedObject.convert(path, object, model, cache, include);
		model.setUrl(object.getUrl());
		model.setSkuUrl(object.getSkuUrl());

		return model;
	}
}
