package org.marsik.elshelves.backend.controllers;

import org.marsik.elshelves.api.entities.UnitApiModel;
import org.marsik.elshelves.backend.entities.Unit;
import org.marsik.elshelves.backend.services.UnitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/units")
public class UnitController extends AbstractRestController<Unit, UnitApiModel, UnitService> {
	@Autowired
	public UnitController(UnitService service) {
		super(UnitApiModel.class, service);
	}
}
