package org.marsik.elshelves.backend.controllers;

import gnu.trove.map.hash.THashMap;
import org.apache.commons.io.IOUtils;
import org.marsik.elshelves.api.entities.DocumentApiModel;
import org.marsik.elshelves.backend.controllers.exceptions.BaseRestException;
import org.marsik.elshelves.backend.controllers.exceptions.EntityNotFound;
import org.marsik.elshelves.backend.controllers.exceptions.PermissionDenied;
import org.marsik.elshelves.backend.entities.Document;
import org.marsik.elshelves.backend.entities.User;
import org.marsik.elshelves.backend.entities.converters.DocumentToEmber;
import org.marsik.elshelves.backend.entities.converters.EmberToDocument;
import org.marsik.elshelves.backend.security.CurrentUser;
import org.marsik.elshelves.backend.services.DocumentService;
import org.marsik.elshelves.backend.services.StorageManager;
import org.marsik.elshelves.ember.EmberModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/v1/documents")
public class DocumentController extends AbstractReadOnlyRestController<Document, DocumentApiModel, DocumentService> {
	@Autowired
	StorageManager storageManager;

	EmberToDocument restToDb;

	@Autowired
	public DocumentController(DocumentService service,
							  DocumentToEmber dbToRest,
							  EmberToDocument restToDb) {
		super(DocumentApiModel.class, dbToRest, service);
		this.restToDb = restToDb;
	}

	@Transactional(readOnly = true)
	@RequestMapping("/{uuid}/download")
	@ResponseStatus(HttpStatus.OK)
	public void download(@CurrentUser User currentUser,
						 @PathVariable("uuid") UUID uuid,
						 HttpServletResponse response) throws EntityNotFound, PermissionDenied, IOException, FileNotFoundException {
		Document d = getService().get(uuid, currentUser);
		String name = d.getName() != null ? d.getName() : d.getId().toString();

		response.setContentType(d.getContentType());
		response.setHeader("Content-Disposition", "attachment; filename=" + name);
		IOUtils.copy(storageManager.retrieve(uuid), response.getOutputStream());
		response.flushBuffer();
	}

	@Transactional
	@RequestMapping(method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<EmberModel> upload(@CurrentUser User currentUser,
											 @RequestParam("file") MultipartFile file,
											 @RequestParam("entity") @Valid DocumentApiModel entity) throws IOException, BaseRestException {

		Document incoming = getRestToDb().convert(entity, new THashMap<>());
		incoming = service.create(incoming, currentUser);

		// Flush is needed to get the updated version
		service.flush();

		DocumentApiModel result = getDbToRest().convert(incoming, new THashMap<>());

		EmberModel.Builder<DocumentApiModel> builder = new EmberModel.Builder<>(result);
		sideLoad(entity, builder);

		getService().processUpload(incoming, file);

		return ResponseEntity
				.ok()
				.eTag(entity.getVersion().toString())
				.lastModified(incoming.getLastModified().getMillis())
				.body(builder.build());
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.PUT)
	@ResponseBody
	@Transactional
	public ResponseEntity<EmberModel> update(@CurrentUser User currentUser,
			@PathVariable("id") UUID uuid,
			@Valid @RequestBody DocumentApiModel item) throws BaseRestException {
		// The REST entity does not contain id during PUT, because that is
		// provided by the URL
		item.setId(uuid);
		Document update = getRestToDb().convert(item, new THashMap<>());
		Document entity = service.update(update, currentUser);

		// Flush is needed to get the updated version
		service.flush();

		DocumentApiModel result = getDbToRest().convert(entity, new THashMap<>());
		EmberModel.Builder<DocumentApiModel> builder = new EmberModel.Builder<>(result);
		sideLoad(result, builder);

		return ResponseEntity
				.ok()
				.eTag(entity.getVersion().toString())
				.lastModified(entity.getLastModified().getMillis())
				.body(builder.build());
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	@ResponseBody
	@Transactional
	public Map<Object, Object> deleteOne(@CurrentUser User currentUser,
			@PathVariable("id") UUID uuid) throws BaseRestException {
		service.delete(uuid, currentUser);
		return new THashMap<>();
	}

	public EmberToDocument getRestToDb() {
		return restToDb;
	}
}
