package org.marsik.elshelves.backend.controllers;

import org.marsik.elshelves.api.ember.EmberModel;
import org.marsik.elshelves.api.entities.DocumentApiModel;
import org.marsik.elshelves.backend.controllers.exceptions.EntityNotFound;
import org.marsik.elshelves.backend.controllers.exceptions.PermissionDenied;
import org.marsik.elshelves.backend.entities.NamedEntity;
import org.marsik.elshelves.backend.entities.User;
import org.marsik.elshelves.backend.security.CurrentUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.neo4j.support.Neo4jTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.UUID;

@RestController
@RequestMapping("/upload")
public class UploadController {
	@Autowired
	Neo4jTemplate neo4jTemplate;

	@RequestMapping(method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.OK)
	public EmberModel upload(@CurrentUser User currentUser,
					   @RequestParam("files[]") MultipartFile[] f,
					   @RequestParam(value = "entity", required = false) UUID entity) throws EntityNotFound, PermissionDenied {
		NamedEntity e = null;

		if (entity != null) {
			e = neo4jTemplate.findByIndexedValue(NamedEntity.class, "uuid", entity).singleOrNull();
			if (e == null) {
				throw new EntityNotFound();
			}

			if (!e.getOwner().equals(currentUser)) {
				throw new PermissionDenied();
			}
		}

		DocumentApiModel d = new DocumentApiModel();

		EmberModel.Builder<DocumentApiModel> b = new EmberModel.Builder<DocumentApiModel>(d);
		return b.build();
	}
}
