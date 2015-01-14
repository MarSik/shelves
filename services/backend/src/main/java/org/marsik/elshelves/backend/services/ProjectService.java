package org.marsik.elshelves.backend.services;

import org.marsik.elshelves.api.entities.ProjectApiModel;
import org.marsik.elshelves.backend.controllers.exceptions.OperationNotPermitted;
import org.marsik.elshelves.backend.entities.Project;
import org.marsik.elshelves.backend.entities.Requirement;
import org.marsik.elshelves.backend.entities.User;
import org.marsik.elshelves.backend.entities.converters.EmberToProject;
import org.marsik.elshelves.backend.entities.converters.ProjectToEmber;
import org.marsik.elshelves.backend.repositories.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.neo4j.support.Neo4jTemplate;
import org.springframework.stereotype.Service;

@Service
public class ProjectService extends AbstractRestService<ProjectRepository, Project, ProjectApiModel> {
	@Autowired
	RequirementService requirementService;

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
}
