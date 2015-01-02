package org.marsik.elshelves.backend.entities.converters;

import org.marsik.elshelves.api.entities.RequirementApiModel;
import org.marsik.elshelves.backend.entities.Requirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
public class RequirementToEmber implements CachingConverter<Requirement, RequirementApiModel, UUID> {
	@Autowired
	TypeToEmber typeToEmber;

	@Autowired
	ProjectToEmber projectToEmber;

	@Override
	public RequirementApiModel convert(Requirement object, int nested, Map<UUID, Object> cache) {
		if (object == null) {
			return null;
		}

		if (cache.containsKey(object.getUuid())) {
			return (RequirementApiModel)cache.get(object.getUuid());
		}

		RequirementApiModel model = new RequirementApiModel();
		if (nested > 0
				&& object.getUuid() != null) {
			cache.put(object.getUuid(), model);
		}
		return convert(object, model, nested, cache);
	}

	@Override
	public RequirementApiModel convert(Requirement object, RequirementApiModel model, int nested, Map<UUID, Object> cache) {
		model.setId(object.getUuid());
		model.setCount(object.getCount());

		if (nested == 0) {
			return model;
		}

		model.setType(typeToEmber.convert(object.getType(), nested - 1, cache));
		model.setProject(projectToEmber.convert(object.getProject(), nested - 1, cache));

		return model;
	}
}
