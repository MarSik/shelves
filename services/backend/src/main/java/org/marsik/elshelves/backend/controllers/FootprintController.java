package org.marsik.elshelves.backend.controllers;

import org.marsik.elshelves.api.entities.FootprintApiModel;
import org.marsik.elshelves.backend.entities.Footprint;
import org.marsik.elshelves.backend.services.FootprintService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/footprints")
public class FootprintController extends AbstractRestController<Footprint, FootprintApiModel, FootprintService> {
	@Autowired
	public FootprintController(FootprintService service) {
		super(FootprintApiModel.class, service);
	}
}
