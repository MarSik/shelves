package org.marsik.elshelves.backend.entities.converters;

import org.marsik.elshelves.api.entities.SourceApiModel;
import org.marsik.elshelves.backend.entities.Source;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
public class EmberToSource extends AbstractEmberToEntity<SourceApiModel, Source> {
	@Autowired
	EmberToNamedObject emberToNamedObject;

	public EmberToSource() {
		super(Source.class);
	}

	@Override
	public Source convert(SourceApiModel object, Source model, int nested, Map<UUID, Object> cache) {
		emberToNamedObject.convert(object, model, nested, cache);
		model.setUrl(object.getUrl());

		model.setTransactions(null);

		return model;
	}
}
