package org.marsik.elshelves.backend.entities.converters;

import org.marsik.elshelves.api.entities.UnitApiModel;
import org.marsik.elshelves.backend.entities.IdentifiedEntity;
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
public class EmberToUnit extends AbstractEmberToEntity<UnitApiModel, Unit> {

	@Autowired
	EmberToNamedObject emberToNamedObject;

	@Autowired
	EmberToEntityConversionService conversionService;

	public EmberToUnit() {
		super(Unit.class);
	}

	@PostConstruct
	void postConstruct() {
		conversionService.register(UnitApiModel.class, this);

	}

	@Override
	public Unit convert(String path, UnitApiModel object, Unit model, Map<UUID, Object> cache, Set<String> include) {
		emberToNamedObject.convert(path, object, model, cache, include);
		model.setSymbol(object.getSymbol());
		model.setPrefixes(object.getPrefixes());
		model.setUnitUses(new IdentifiedEntity.UnprovidedSet<>());

		return model;
	}
}
