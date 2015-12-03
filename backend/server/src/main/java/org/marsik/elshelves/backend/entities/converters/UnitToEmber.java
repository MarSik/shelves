package org.marsik.elshelves.backend.entities.converters;

import org.marsik.elshelves.api.entities.UnitApiModel;
import org.marsik.elshelves.backend.entities.Unit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Service
@DependsOn("EntityToEmberConversionService")
public class UnitToEmber extends AbstractEntityToEmber<Unit, UnitApiModel> {
	@Autowired
	NamedObjectToEmber namedObjectToEmber;

	@Autowired
	EntityToEmberConversionService conversionService;

	public UnitToEmber() {
		super(UnitApiModel.class);
	}

	@PostConstruct
	void postConstruct() {
		conversionService.register(Unit.class, getTarget(), this);

	}

	@Override
	public UnitApiModel convert(String path, Unit object, UnitApiModel model, Map<UUID, Object> cache, Set<String> include) {
		namedObjectToEmber.convert(path, object, model, cache, include);

		model.setSymbol(object.getSymbol());
		model.setPrefixes(object.getPrefixes());
		return model;
	}
}
