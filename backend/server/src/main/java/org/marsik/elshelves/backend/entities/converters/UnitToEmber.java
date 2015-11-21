package org.marsik.elshelves.backend.entities.converters;

import org.marsik.elshelves.api.entities.UnitApiModel;
import org.marsik.elshelves.backend.entities.Unit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Service
public class UnitToEmber extends AbstractEntityToEmber<Unit, UnitApiModel> {
	@Autowired
	NamedObjectToEmber namedObjectToEmber;

	public UnitToEmber() {
		super(UnitApiModel.class);
	}

	@Override
	public UnitApiModel convert(String path, Unit object, UnitApiModel model, Map<UUID, Object> cache, Set<String> include) {
		namedObjectToEmber.convert(path, object, model, cache, include);

		model.setSymbol(object.getSymbol());
		model.setPrefixes(object.getPrefixes());
		return model;
	}
}
