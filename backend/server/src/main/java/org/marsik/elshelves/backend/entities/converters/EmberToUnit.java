package org.marsik.elshelves.backend.entities.converters;

import org.marsik.elshelves.api.entities.UnitApiModel;
import org.marsik.elshelves.backend.entities.Unit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
public class EmberToUnit extends AbstractEmberToEntity<UnitApiModel, Unit> {

	@Autowired
	EmberToNamedObject emberToNamedObject;

	public EmberToUnit() {
		super(Unit.class);
	}

	@Override
	public Unit convert(UnitApiModel object, Unit model, int nested, Map<UUID, Object> cache) {
		emberToNamedObject.convert(object, model, nested, cache);
		model.setSymbol(object.getSymbol());

		if (object.getPrefixes() != null) {
			model.setPrefixes(object.getPrefixes());
		}

		return model;
	}
}
