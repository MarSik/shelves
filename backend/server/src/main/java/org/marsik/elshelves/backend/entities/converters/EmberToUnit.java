package org.marsik.elshelves.backend.entities.converters;

import org.marsik.elshelves.api.entities.UnitApiModel;
import org.marsik.elshelves.backend.entities.Unit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Service
public class EmberToUnit extends AbstractEmberToEntity<UnitApiModel, Unit> {

	@Autowired
	EmberToNamedObject emberToNamedObject;

	public EmberToUnit() {
		super(Unit.class);
	}

	@Override
	public Unit convert(String path, UnitApiModel object, Unit model, Map<UUID, Object> cache, Set<String> include) {
		emberToNamedObject.convert(path, object, model, cache, include);
		model.setSymbol(object.getSymbol());
		model.setPrefixes(object.getPrefixes());

		return model;
	}
}
