package org.marsik.elshelves.backend.controllers;

import org.apache.commons.io.IOUtils;
import org.marsik.elshelves.api.entities.DocumentApiModel;
import org.marsik.elshelves.backend.controllers.exceptions.EntityNotFound;
import org.marsik.elshelves.backend.controllers.exceptions.PermissionDenied;
import org.marsik.elshelves.backend.entities.Document;
import org.marsik.elshelves.backend.entities.User;
import org.marsik.elshelves.backend.security.CurrentUser;
import org.marsik.elshelves.backend.services.DocumentService;
import org.marsik.elshelves.backend.services.StorageManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/v1/documents")
public class DocumentController extends AbstractRestController<Document, DocumentApiModel, DocumentService> {
	@Autowired
	StorageManager storageManager;

	@Autowired
	public DocumentController(DocumentService service) {
		super(DocumentApiModel.class, service);
	}

	@Transactional(readOnly = true)
	@RequestMapping("/{uuid}/download")
	@ResponseStatus(HttpStatus.OK)
	public void download(@CurrentUser User currentUser,
						 @PathVariable("uuid") UUID uuid,
						 HttpServletResponse response) throws EntityNotFound, PermissionDenied, IOException, FileNotFoundException {
		DocumentApiModel d = getService().get(uuid, currentUser);
		String name = d.getName() != null ? d.getName() : d.getId().toString();

		response.setContentType(d.getContentType());
		response.setHeader("Content-Disposition", "attachment; filename=" + name);
		IOUtils.copy(storageManager.retrieve(uuid), response.getOutputStream());
		response.flushBuffer();
	}
}
