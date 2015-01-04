package org.marsik.elshelves.backend.services;

import org.marsik.elshelves.api.entities.ProjectApiModel;
import org.marsik.elshelves.backend.entities.converters.EmberToProject;
import org.marsik.elshelves.backend.entities.Project;
import org.marsik.elshelves.backend.entities.converters.ProjectToEmber;
import org.marsik.elshelves.backend.repositories.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProjectService extends AbstractRestService<ProjectRepository, Project, ProjectApiModel> {
	@Autowired
	public ProjectService(ProjectRepository repository,
						  ProjectToEmber dbToRest,
						  EmberToProject restToDb,
						  UuidGenerator uuidGenerator) {
		super(repository, dbToRest, restToDb, uuidGenerator);
	}

	@Override
	protected int conversionDepth() {
		return 2;
	}
}
