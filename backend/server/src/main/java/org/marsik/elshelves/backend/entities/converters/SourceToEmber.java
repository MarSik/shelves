package org.marsik.elshelves.backend.entities.converters;

import org.marsik.elshelves.api.entities.SourceApiModel;
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
public class SourceToEmber extends AbstractEntityToEmber<Source, SourceApiModel> {
	@Autowired
	NamedObjectToEmber namedObjectToEmber;

	@Autowired
	EntityToEmberConversionService conversionService;

	public SourceToEmber() {
		super(SourceApiModel.class);
	}

	@PostConstruct
	void postConstruct() {
		conversionService.register(Source.class, getTarget(), this);

	}

	@Override
	public SourceApiModel convert(String path, Source object, SourceApiModel model, Map<UUID, Object> cache, Set<String> include) {
		namedObjectToEmber.convert(path, object, model, cache, include);

		model.setHasIcon(object.isHasIcon());
		model.setUrl(object.getUrl());
		model.setSkuUrl(object.getSkuUrl());

		return model;
	}
}
