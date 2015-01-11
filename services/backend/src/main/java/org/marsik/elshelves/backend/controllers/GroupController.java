package org.marsik.elshelves.backend.controllers;

import org.marsik.elshelves.api.entities.PartGroupApiModel;
import org.marsik.elshelves.backend.entities.Group;
import org.marsik.elshelves.backend.services.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/groups")
public class GroupController extends AbstractRestController<Group, PartGroupApiModel> {
	@Autowired
	public GroupController(GroupService service) {
		super(PartGroupApiModel.class, service);
	}
}
