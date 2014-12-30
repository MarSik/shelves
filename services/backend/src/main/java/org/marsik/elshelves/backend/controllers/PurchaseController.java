package org.marsik.elshelves.backend.controllers;

import org.marsik.elshelves.api.ember.EmberModel;
import org.marsik.elshelves.api.entities.PurchaseApiModel;
import org.marsik.elshelves.backend.entities.Purchase;
import org.marsik.elshelves.backend.services.AbstractRestService;
import org.marsik.elshelves.backend.services.PurchaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/purchases")
public class PurchaseController extends AbstractRestController<Purchase, PurchaseApiModel> {
	@Autowired
	public PurchaseController(PurchaseService service) {
		super(PurchaseApiModel.class, service);
	}

	@Override
	protected void sideLoad(PurchaseApiModel dto, EmberModel.Builder<PurchaseApiModel> builder) {
		builder.sideLoad(dto.getBelongsTo());
		builder.sideLoad(dto.getLocation());
		builder.sideLoad(dto.getPerformedBy());
		builder.sideLoad(dto.getType());
		super.sideLoad(dto, builder);
	}
}
