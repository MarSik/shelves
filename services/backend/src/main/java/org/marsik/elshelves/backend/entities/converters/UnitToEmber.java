package org.marsik.elshelves.backend.entities.converters;

import org.marsik.elshelves.api.entities.UnitApiModel;
import org.marsik.elshelves.backend.entities.Unit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Map;
import java.util.UUID;

@Service
public class UnitToEmber implements CachingConverter<Unit, UnitApiModel, UUID> {
	@Autowired
	NamedObjectToEmber namedObjectToEmber;

	@Override
	public UnitApiModel convert(Unit object, int nested, Map<UUID, Object> cache) {
		if (object == null) {
			return null;
		}

		if (cache.containsKey(object.getUuid())) {
			return (UnitApiModel) cache.get(object.getUuid());
		}

		UnitApiModel model = new UnitApiModel();
		if (nested > 0
				&& object.getUuid() != null) {
			cache.put(object.getUuid(), model);
		}
		return convert(object, model, nested, cache);
	}

	@Override
	public UnitApiModel convert(Unit object, UnitApiModel model, int nested, Map<UUID, Object> cache) {
		namedObjectToEmber.convert(object, model, nested, cache);

		if (nested == 0) {
			return model;
		}

		model.setSymbol(object.getSymbol());
		model.setPrefixes(object.getPrefixes());
		return model;
	}
}
