package org.marsik.elshelves.backend.controllers;

import org.marsik.elshelves.api.entities.PurchaseApiModel;
import org.marsik.elshelves.backend.entities.Purchase;
import org.marsik.elshelves.backend.services.PurchaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/purchases")
public class PurchaseController extends AbstractRestController<Purchase, PurchaseApiModel, PurchaseService> {
	@Autowired
	public PurchaseController(PurchaseService service) {
		super(PurchaseApiModel.class, service);
	}
}
