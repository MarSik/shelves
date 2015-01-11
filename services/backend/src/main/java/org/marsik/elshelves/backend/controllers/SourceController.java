package org.marsik.elshelves.backend.controllers;

import org.marsik.elshelves.api.entities.SourceApiModel;
import org.marsik.elshelves.backend.entities.Source;
import org.marsik.elshelves.backend.services.SourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sources")
public class SourceController extends AbstractRestController<Source, SourceApiModel> {
	final SourceService sourceService;

	@Autowired
	public SourceController(SourceService service) {
		super(SourceApiModel.class, service);
		this.sourceService = service;
	}
}
