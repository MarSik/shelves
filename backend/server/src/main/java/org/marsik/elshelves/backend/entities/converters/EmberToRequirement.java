package org.marsik.elshelves.backend.entities.converters;

import gnu.trove.set.hash.THashSet;
import org.marsik.elshelves.api.entities.PartTypeApiModel;
import org.marsik.elshelves.api.entities.RequirementApiModel;
import org.marsik.elshelves.backend.entities.Requirement;
import org.marsik.elshelves.backend.entities.Type;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Service
@DependsOn("EntityToEmberConversionService")
public class EmberToRequirement extends AbstractEmberToEntity<RequirementApiModel, Requirement> {
    @Autowired
    EmberToType emberToType;

	@Autowired
	EmberToItem emberToItem;

	@Autowired
	EmberToLot emberToLot;

	@Autowired
	EmberToEntityConversionService conversionService;

	public EmberToRequirement() {
		super(Requirement.class);
	}

	@PostConstruct
	void postConstruct() {
		conversionService.register(RequirementApiModel.class, this);

	}

	@Override
	public Requirement convert(String path, RequirementApiModel object, Requirement model, Map<UUID, Object> cache, Set<String> include) {
		model.setCount(object.getCount());
        model.setName(object.getName());
        model.setSummary(object.getSummary());

		if (object.getType() != null) {
			model.setType(new THashSet<Type>());
			for (PartTypeApiModel p: object.getType()) {
				model.addType(emberToType.convert(path, "type", p, cache, include));
			}
		} else {
			model.setType(null);
		}

		model.setItem(emberToItem.convert(path, "item", object.getItem(), cache, include));

		return model;
	}
}
