package org.marsik.elshelves.backend.controllers;

import org.marsik.elshelves.api.entities.PartGroupApiModel;
import org.marsik.elshelves.backend.entities.Group;
import org.marsik.elshelves.backend.entities.converters.EmberToGroup;
import org.marsik.elshelves.backend.entities.converters.GroupToEmber;
import org.marsik.elshelves.backend.services.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/groups")
public class GroupController extends AbstractRestController<Group, PartGroupApiModel, GroupService> {
	@Autowired
	public GroupController(GroupService service,
						   GroupToEmber dbToRest,
						   EmberToGroup restToDb) {
		super(PartGroupApiModel.class, service, dbToRest, restToDb);
	}
}
