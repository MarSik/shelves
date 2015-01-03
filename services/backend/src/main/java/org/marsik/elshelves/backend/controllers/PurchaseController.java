package org.marsik.elshelves.backend.controllers;

import org.marsik.elshelves.api.ember.EmberModel;
import org.marsik.elshelves.api.entities.LotApiModel;
import org.marsik.elshelves.api.entities.PartTypeApiModel;
import org.marsik.elshelves.api.entities.PurchaseApiModel;
import org.marsik.elshelves.backend.controllers.exceptions.EntityNotFound;
import org.marsik.elshelves.backend.controllers.exceptions.PermissionDenied;
import org.marsik.elshelves.backend.entities.Purchase;
import org.marsik.elshelves.backend.entities.User;
import org.marsik.elshelves.backend.security.CurrentUser;
import org.marsik.elshelves.backend.services.AbstractRestService;
import org.marsik.elshelves.backend.services.PurchaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/purchases")
public class PurchaseController extends AbstractRestController<Purchase, PurchaseApiModel> {
	@Autowired
	public PurchaseController(PurchaseService service) {
		super(PurchaseApiModel.class, service);
	}

	@RequestMapping("/{uuid}/next")
	@ResponseBody
	@Transactional
	public EmberModel getNext(@CurrentUser User currentUser,
							  @PathVariable("uuid") UUID id) throws PermissionDenied, EntityNotFound {
		Iterable<LotApiModel> lots = ((PurchaseService)getService()).getNext(id, currentUser);
		EmberModel.Builder<LotApiModel> modelBuilder = new EmberModel.Builder<LotApiModel>(LotApiModel.class, lots);
		return modelBuilder.build();
	}

	@RequestMapping("{uuid}/lots")
	@ResponseBody
	@Transactional
	public EmberModel getLots(@PathVariable("uuid") UUID uuid,
							  @CurrentUser User currentUser) throws EntityNotFound, PermissionDenied {
		PurchaseApiModel g = getService().get(uuid, currentUser);

		EmberModel.Builder<LotApiModel> builder = new EmberModel.Builder<LotApiModel>(LotApiModel.class, g.getLots());
		return builder.build();
	}
}
