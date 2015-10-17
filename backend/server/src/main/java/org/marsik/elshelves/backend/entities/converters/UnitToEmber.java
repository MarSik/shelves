package org.marsik.elshelves.backend.entities.converters;

import org.marsik.elshelves.api.entities.UnitApiModel;
import org.marsik.elshelves.backend.entities.Unit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
public class UnitToEmber extends AbstractEntityToEmber<Unit, UnitApiModel> {
	@Autowired
	NamedObjectToEmber namedObjectToEmber;

	public UnitToEmber() {
		super(UnitApiModel.class);
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
