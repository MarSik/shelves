package org.marsik.elshelves.backend.controllers;

import org.marsik.elshelves.api.ember.EmberModel;
import org.marsik.elshelves.api.entities.PartGroupApiModel;
import org.marsik.elshelves.api.entities.PartTypeApiModel;
import org.marsik.elshelves.backend.controllers.exceptions.EntityNotFound;
import org.marsik.elshelves.backend.controllers.exceptions.PermissionDenied;
import org.marsik.elshelves.backend.entities.Group;
import org.marsik.elshelves.backend.entities.User;
import org.marsik.elshelves.backend.security.CurrentUser;
import org.marsik.elshelves.backend.services.AbstractRestService;
import org.marsik.elshelves.backend.services.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/groups")
public class GroupController extends AbstractRestController<Group, PartGroupApiModel> {
	@Autowired
	public GroupController(GroupService service) {
		super(PartGroupApiModel.class, service);
	}

	@RequestMapping("{uuid}/types")
	@ResponseBody
	@Transactional
	public EmberModel getTypes(@PathVariable("uuid") UUID uuid,
							   @CurrentUser User currentUser) throws EntityNotFound, PermissionDenied {
		PartGroupApiModel g = getService().get(uuid, currentUser);

		EmberModel.Builder<PartTypeApiModel> builder = new EmberModel.Builder<PartTypeApiModel>(PartTypeApiModel.class, g.getTypes());
		builder.sideLoad(g.getBelongsTo());

		for (PartTypeApiModel t: g.getTypes()) {
			builder.sideLoad(t.getFootprint());
			builder.sideLoad(t.getBelongsTo());
		}

		return builder.build();
	}

	@RequestMapping("{uuid}/groups")
	@ResponseBody
	@Transactional
	public EmberModel getGroups(@PathVariable("uuid") UUID uuid,
								@CurrentUser User currentUser) throws EntityNotFound, PermissionDenied {
		PartGroupApiModel g = getService().get(uuid, currentUser);

		EmberModel.Builder<PartGroupApiModel> builder = new EmberModel.Builder<PartGroupApiModel>(PartGroupApiModel.class, g.getGroups());
		builder.sideLoad(g.getBelongsTo());

		for (PartGroupApiModel t: g.getGroups()) {
			builder.sideLoad(PartGroupApiModel.class, t.getGroups());
			builder.sideLoad(t.getParent());
			builder.sideLoad(t.getBelongsTo());
		}

		return builder.build();
	}
}
