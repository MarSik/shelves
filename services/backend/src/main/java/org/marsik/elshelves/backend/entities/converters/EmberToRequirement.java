package org.marsik.elshelves.backend.entities.converters;

import org.marsik.elshelves.api.entities.BoxApiModel;
import org.marsik.elshelves.api.entities.RequirementApiModel;
import org.marsik.elshelves.backend.entities.Box;
import org.marsik.elshelves.backend.entities.EmberToProject;
import org.marsik.elshelves.backend.entities.Requirement;
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

		model.setType(emberToType.convert(object.getType(), nested, cache));
		model.setProject(emberToProject.convert(object.getProject(), nested, cache));

		return model;
	}
}
