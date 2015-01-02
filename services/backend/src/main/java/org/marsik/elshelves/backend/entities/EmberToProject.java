package org.marsik.elshelves.backend.entities;

import gnu.trove.set.hash.THashSet;
import org.marsik.elshelves.api.entities.ProjectApiModel;
import org.marsik.elshelves.api.entities.RequirementApiModel;
import org.marsik.elshelves.backend.entities.converters.CachingConverter;
import org.marsik.elshelves.backend.entities.converters.EmberToNamedObject;
import org.marsik.elshelves.backend.entities.converters.EmberToRequirement;
import org.marsik.elshelves.backend.entities.converters.EmberToUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
public class EmberToProject implements CachingConverter<ProjectApiModel, Project, UUID> {
    @Autowired
	EmberToRequirement emberToRequirement;

	@Autowired
	EmberToNamedObject emberToNamedObject;

    @Override
    public Project convert(ProjectApiModel object, int nested, Map<UUID, Object> cache) {
        if (object == null) {
            return null;
        }

        if (cache.containsKey(object.getId())) {
            return (Project)cache.get(object.getId());
        }

        Project project = new Project();

		if (nested > 0
				&& object.getId() != null) {
			cache.put(object.getId(), project);
		}

		return convert(object, project, nested, cache);
    }

	@Override
	public Project convert(ProjectApiModel object, Project project, int nested, Map<UUID, Object> cache) {
		emberToNamedObject.convert(object, project, nested, cache);
		project.setDescription(object.getDescription());

		if (object.getRequires() != null) {
			project.setRequires(new THashSet<Requirement>());
			for (RequirementApiModel r: object.getRequires()) {
				project.getRequires().add(emberToRequirement.convert(r, nested, cache));
			}
		}

		return project;
	}
}
