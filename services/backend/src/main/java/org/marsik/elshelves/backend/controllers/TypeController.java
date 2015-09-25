package org.marsik.elshelves.backend.controllers;

import org.marsik.elshelves.api.entities.PartTypeApiModel;
import org.marsik.elshelves.backend.entities.Type;
import org.marsik.elshelves.backend.services.TypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/types")
public class TypeController extends AbstractRestController<Type, PartTypeApiModel, TypeService> {
	@Autowired
	public TypeController(TypeService service) {
		super(PartTypeApiModel.class, service);
	}
}
