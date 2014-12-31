package org.marsik.elshelves.backend.controllers;

import org.marsik.elshelves.api.ember.EmberModel;
import org.marsik.elshelves.api.entities.LotApiModel;
import org.marsik.elshelves.api.entities.PartGroupApiModel;
import org.marsik.elshelves.api.entities.PartTypeApiModel;
import org.marsik.elshelves.backend.controllers.exceptions.EntityNotFound;
import org.marsik.elshelves.backend.controllers.exceptions.PermissionDenied;
import org.marsik.elshelves.backend.entities.Type;
import org.marsik.elshelves.backend.entities.User;
import org.marsik.elshelves.backend.security.CurrentUser;
import org.marsik.elshelves.backend.services.AbstractRestService;
import org.marsik.elshelves.backend.services.TypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/types")
public class TypeController extends AbstractRestController<Type, PartTypeApiModel> {
	@Autowired
	public TypeController(TypeService service) {
		super(PartTypeApiModel.class, service);
	}

	@Override
	protected void sideLoad(PartTypeApiModel dto, EmberModel.Builder<PartTypeApiModel> builder) {
		builder.sideLoad(dto.getFootprint());
		super.sideLoad(dto, builder);
	}

	@RequestMapping("{uuid}/groups")
	@ResponseBody
	@Transactional
	public EmberModel getGroups(@PathVariable("uuid") UUID uuid,
							    @CurrentUser User currentUser) throws EntityNotFound, PermissionDenied {
		PartTypeApiModel g = getService().get(uuid, currentUser);

		EmberModel.Builder<PartGroupApiModel> builder = new EmberModel.Builder<PartGroupApiModel>(PartGroupApiModel.class, g.getGroups());
		builder.sideLoad(g.getBelongsTo());

		for (PartGroupApiModel t: g.getGroups()) {
			builder.sideLoad(PartGroupApiModel.class, t.getGroups());
			builder.sideLoad(t.getParent());
			builder.sideLoad(t.getBelongsTo());
		}

		return builder.build();
	}

	@RequestMapping("{uuid}/lots")
	@ResponseBody
	@Transactional
	public EmberModel getLots(@PathVariable("uuid") UUID uuid,
								@CurrentUser User currentUser) throws EntityNotFound, PermissionDenied {
		PartTypeApiModel g = getService().get(uuid, currentUser);

		EmberModel.Builder<LotApiModel> builder = new EmberModel.Builder<LotApiModel>(LotApiModel.class, g.getLots());
		builder.sideLoad(g.getBelongsTo());
		builder.sideLoad(g);

		for (LotApiModel t: g.getLots()) {
			builder.sideLoad(t.getLocation());
			builder.sideLoad(t.getPerformedBy());
			builder.sideLoad(t.getPrevious());
		}

		return builder.build();
	}
}
