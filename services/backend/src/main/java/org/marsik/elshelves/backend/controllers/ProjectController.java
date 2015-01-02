package org.marsik.elshelves.backend.controllers;

import org.marsik.elshelves.api.entities.ProjectApiModel;
import org.marsik.elshelves.backend.entities.Project;
import org.marsik.elshelves.backend.services.AbstractRestService;
import org.marsik.elshelves.backend.services.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/projects")
public class ProjectController extends AbstractRestController<Project, ProjectApiModel> {
	@Autowired
	public ProjectController(ProjectService service) {
		super(ProjectApiModel.class, service);
	}
}
