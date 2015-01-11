package org.marsik.elshelves.backend.controllers;

import org.marsik.elshelves.api.entities.RequirementApiModel;
import org.marsik.elshelves.backend.entities.Requirement;
import org.marsik.elshelves.backend.services.RequirementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/requirements")
public class RequirementController extends AbstractRestController<Requirement, RequirementApiModel> {
	@Autowired
	public RequirementController(RequirementService service) {
		super(RequirementApiModel.class, service);
	}
}
