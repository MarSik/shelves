package org.marsik.elshelves.backend.entities.converters;

import org.marsik.elshelves.api.entities.UnitApiModel;
import org.marsik.elshelves.api.entities.fields.SiPrefix;
import org.marsik.elshelves.backend.entities.Unit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
public class EmberToUnit implements CachingConverter<UnitApiModel, Unit, UUID> {

	@Autowired
	EmberToNamedObject emberToNamedObject;

	@Override
	public Unit convert(UnitApiModel object, int nested, Map<UUID, Object> cache) {
		if (object == null) {
			return null;
		}

		if (cache.containsKey(object.getId())) {
			return (Unit)cache.get(object.getId());
		}

		Unit model = new Unit();
		if (nested > 0
				&& object.getId() != null) {
			cache.put(object.getId(), model);
		}
		return convert(object, model, nested, cache);
	}

	@Override
	public Unit convert(UnitApiModel object, Unit model, int nested, Map<UUID, Object> cache) {
		emberToNamedObject.convert(object, model, nested, cache);
		model.setSymbol(object.getSymbol());

		if (object.getPrefixes() != null) {
			SiPrefix[] prefixArray = new SiPrefix[object.getPrefixes().size()];
			object.getPrefixes().toArray(prefixArray);
			model.setPrefixes(prefixArray);
		}

		return model;
	}
}
