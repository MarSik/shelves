package org.marsik.elshelves.backend.entities.converters;

import gnu.trove.set.hash.THashSet;
import org.marsik.elshelves.api.entities.PartTypeApiModel;
import org.marsik.elshelves.api.entities.RequirementApiModel;
import org.marsik.elshelves.backend.entities.Requirement;
import org.marsik.elshelves.backend.entities.Type;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
public class EmberToRequirement extends AbstractEmberToEntity<RequirementApiModel, Requirement> {
    @Autowired
    EmberToType emberToType;

	@Autowired
	EmberToItem emberToItem;

	@Autowired
	EmberToLot emberToLot;

	public EmberToRequirement() {
		super(Requirement.class);
	}

	@Override
	public Requirement convert(RequirementApiModel object, Requirement model, int nested, Map<UUID, Object> cache) {
		model.setId(object.getId());
		model.setCount(object.getCount());
        model.setName(object.getName());
        model.setSummary(object.getSummary());

		if (object.getType() != null) {
			model.setType(new THashSet<Type>());
			for (PartTypeApiModel p: object.getType()) {
				model.addType(emberToType.convert(p, nested, cache));
			}
		}

		model.setItem(emberToItem.convert(object.getItem(), nested, cache));

		return model;
	}
}
