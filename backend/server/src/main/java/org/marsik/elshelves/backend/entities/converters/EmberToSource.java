package org.marsik.elshelves.backend.entities.converters;

import org.marsik.elshelves.api.entities.SourceApiModel;
import org.marsik.elshelves.backend.entities.IdentifiedEntity;
import org.marsik.elshelves.backend.entities.Source;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Service
@DependsOn("EntityToEmberConversionService")
public class EmberToSource extends AbstractEmberToEntity<SourceApiModel, Source> {
	@Autowired
	EmberToNamedObject emberToNamedObject;

	@Autowired
	EmberToEntityConversionService conversionService;

	public EmberToSource() {
		super(Source.class);
	}

	@PostConstruct
	void postConstruct() {
		conversionService.register(SourceApiModel.class, this);

	}

	@Override
	public Source convert(String path, SourceApiModel object, Source model, Map<UUID, Object> cache, Set<String> include) {
		emberToNamedObject.convert(path, object, model, cache, include);
		model.setUrl(object.getUrl());
		model.setSkuUrl(object.getSkuUrl());
		model.setTransactions(new IdentifiedEntity.UnprovidedSet<>());

		return model;
	}
}
