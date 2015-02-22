package org.marsik.elshelves.backend.services;

import gnu.trove.map.hash.THashMap;
import org.marsik.elshelves.api.entities.ProjectApiModel;
import org.marsik.elshelves.api.entities.RequirementApiModel;
import org.marsik.elshelves.backend.controllers.exceptions.EntityNotFound;
import org.marsik.elshelves.backend.controllers.exceptions.OperationNotPermitted;
import org.marsik.elshelves.backend.controllers.exceptions.PermissionDenied;
import org.marsik.elshelves.backend.entities.Project;
import org.marsik.elshelves.backend.entities.Requirement;
import org.marsik.elshelves.backend.entities.User;
import org.marsik.elshelves.backend.entities.converters.EmberToProject;
import org.marsik.elshelves.backend.entities.converters.ProjectToEmber;
import org.marsik.elshelves.backend.repositories.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class ProjectService extends AbstractRestService<ProjectRepository, Project, ProjectApiModel> {
	@Autowired
	RequirementService requirementService;

    @Autowired
    DocumentService documentService;

	@Autowired
	public ProjectService(ProjectRepository repository,
						  ProjectToEmber dbToRest,
						  EmberToProject restToDb,
						  UuidGenerator uuidGenerator) {
		super(repository, dbToRest, restToDb, uuidGenerator);
	}

    @Override
    protected Iterable<Project> getAllEntities(User currentUser) {
        return getRepository().findByOwner(currentUser);
    }

	@Override
	protected void deleteEntity(Project entity) throws OperationNotPermitted {
		// Delete requirements
		for (Requirement r: entity.getRequires()) {
			requirementService.deleteEntity(r);
		}

		super.deleteEntity(entity);
	}

    public ProjectApiModel importRequirements(UUID projectId, UUID document, User currentUser, List<RequirementApiModel> newRequirements) throws OperationNotPermitted, EntityNotFound, PermissionDenied, IOException {
        Project project = getSingleEntity(projectId);

        if (project == null) {
            throw new EntityNotFound();
        }

        if (!project.getOwner().equals(currentUser)) {
            throw new PermissionDenied();
        }

        ProjectApiModel dummyProject = new ProjectApiModel();
        dummyProject.setId(projectId);

        Map<UUID, Object> cache = new THashMap<>();

        List<RequirementApiModel> requirements = documentService.analyzeSchematics(document, currentUser);
        for (RequirementApiModel r: requirements) {
            r.setProject(dummyProject);
            RequirementApiModel newR = requirementService.create(r, currentUser);
            newRequirements.add(newR);
            cache.put(newR.getId(), newR);
        }

        return getDbToRest().convert(project, 1, cache);
    }
}
