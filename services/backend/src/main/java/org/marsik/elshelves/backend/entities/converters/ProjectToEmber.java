package org.marsik.elshelves.backend.entities.converters;

import gnu.trove.set.hash.THashSet;
import org.marsik.elshelves.api.entities.ProjectApiModel;
import org.marsik.elshelves.api.entities.RequirementApiModel;
import org.marsik.elshelves.backend.entities.Project;
import org.marsik.elshelves.backend.entities.Requirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
public class ProjectToEmber implements CachingConverter<Project, ProjectApiModel, UUID> {
	@Autowired
	NamedObjectToEmber namedObjectToEmber;

	@Autowired
	RequirementToEmber requirementToEmber;

	@Override
	public ProjectApiModel convert(Project object, int nested, Map<UUID, Object> cache) {
		if (object == null) {
			return null;
		}

		if (cache.containsKey(object.getUuid())) {
			return (ProjectApiModel)cache.get(object.getUuid());
		}

		ProjectApiModel model = new ProjectApiModel();
		if (nested > 0
				&& object.getUuid() != null) {
			cache.put(object.getUuid(), model);
		}
		return convert(object, model, nested, cache);
	}

	@Override
	public ProjectApiModel convert(Project object, ProjectApiModel model, int nested, Map<UUID, Object> cache) {
		namedObjectToEmber.convert(object, model, nested, cache);

		if (nested == 0) {
			return model;
		}

		if (object.getRequires() != null) {
			model.setRequirements(new THashSet<RequirementApiModel>());
			for (Requirement r: object.getRequires()) {
				model.getRequirements().add(requirementToEmber.convert(r, nested - 1, cache));
			}
		}

		return model;
	}
}
