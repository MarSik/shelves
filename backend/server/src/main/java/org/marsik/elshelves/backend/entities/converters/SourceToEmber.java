package org.marsik.elshelves.backend.entities.converters;

import org.marsik.elshelves.api.entities.SourceApiModel;
import org.marsik.elshelves.backend.entities.Source;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
public class SourceToEmber extends AbstractEntityToEmber<Source, SourceApiModel> {
	@Autowired
	NamedObjectToEmber namedObjectToEmber;

	public SourceToEmber() {
		super(SourceApiModel.class);
	}

	@Override
	public SourceApiModel convert(Source object, SourceApiModel model, int nested, Map<UUID, Object> cache) {
		namedObjectToEmber.convert(object, model, nested, cache);

		if (nested == 0) {
			return model;
		}

		model.setHasIcon(object.isHasIcon());
		model.setUrl(object.getUrl());
		model.setSkuUrl(object.getSkuUrl());

		return model;
	}
}
