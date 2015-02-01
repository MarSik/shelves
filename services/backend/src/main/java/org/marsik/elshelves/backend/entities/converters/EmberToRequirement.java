package org.marsik.elshelves.backend.entities.converters;

import gnu.trove.set.hash.THashSet;
import org.marsik.elshelves.api.entities.LotApiModel;
import org.marsik.elshelves.api.entities.PartTypeApiModel;
import org.marsik.elshelves.api.entities.RequirementApiModel;
import org.marsik.elshelves.backend.entities.Lot;
import org.marsik.elshelves.backend.entities.Requirement;
import org.marsik.elshelves.backend.entities.Type;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
public class EmberToRequirement implements CachingConverter<RequirementApiModel, Requirement, UUID> {
    @Autowired
    EmberToType emberToType;

	@Autowired
	EmberToProject emberToProject;

	@Autowired
	EmberToLot emberToLot;

    @Override
    public Requirement convert(RequirementApiModel object, int nested, Map<UUID, Object> cache) {
        if (object == null) {
            return null;
        }

        if (cache.containsKey(object.getId())) {
            return (Requirement)cache.get(object.getId());
        }

        Requirement model = new Requirement();

		if (nested > 0
				&& object.getId() != null) {
			cache.put(object.getId(), model);
		}

		return convert(object, model, nested, cache);
    }

	@Override
	public Requirement convert(RequirementApiModel object, Requirement model, int nested, Map<UUID, Object> cache) {
		model.setUuid(object.getId());
		model.setCount(object.getCount());
        model.setName(object.getName());
        model.setSummary(object.getSummary());

		if (object.getType() != null) {
			model.setType(new THashSet<Type>());
			for (PartTypeApiModel p: object.getType()) {
				model.getType().add(emberToType.convert(p, nested, cache));
			}
		}

		model.setProject(emberToProject.convert(object.getProject(), nested, cache));

		return model;
	}
}
