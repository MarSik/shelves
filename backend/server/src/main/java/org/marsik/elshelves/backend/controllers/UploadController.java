package org.marsik.elshelves.backend.controllers;

import gnu.trove.map.hash.THashMap;
import gnu.trove.set.hash.THashSet;
import org.marsik.elshelves.backend.controllers.exceptions.BaseRestException;
import org.marsik.elshelves.ember.EmberModel;
import org.marsik.elshelves.api.entities.DocumentApiModel;
import org.marsik.elshelves.backend.controllers.exceptions.EntityNotFound;
import org.marsik.elshelves.backend.controllers.exceptions.PermissionDenied;
import org.marsik.elshelves.backend.entities.Document;
import org.marsik.elshelves.backend.entities.NamedEntity;
import org.marsik.elshelves.backend.entities.User;
import org.marsik.elshelves.backend.entities.converters.DocumentToEmber;
import org.marsik.elshelves.backend.repositories.NamedEntityRepository;
import org.marsik.elshelves.backend.security.CurrentUser;
import org.marsik.elshelves.backend.services.DocumentService;
import org.marsik.elshelves.backend.services.FileAnalysisDoneHandler;
import org.marsik.elshelves.backend.services.StorageManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/v1/upload")
public class UploadController {
	@Autowired
	DocumentService documentService;

	@Autowired
	StorageManager storageManager;

	@Autowired
	FileAnalysisDoneHandler documentAnalysisDoneService;

	@Autowired
	NamedEntityRepository namedEntityRepository;

	@Autowired
	DocumentToEmber documentToEmber;

	@Transactional
	@RequestMapping(method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.OK)
	public EmberModel upload(@CurrentUser User currentUser,
					   @RequestParam("files[]") MultipartFile[] files,
                       @RequestParam(value = "webcam", required = false) MultipartFile webcam,
					   @RequestParam("entity") UUID entity,
                       HttpServletRequest request) throws IOException, BaseRestException {
		NamedEntity e;

		if (entity != null) {
			e = namedEntityRepository.findById(entity);
			if (e == null) {
				throw new EntityNotFound();
			}

			if (!e.getOwner().equals(currentUser)) {
				throw new PermissionDenied();
			}
		}

		Set<Document> documents = new THashSet<>();

		NamedEntity describesRecord = new NamedEntity();
		describesRecord.setId(entity);

		for (MultipartFile file: files) {
            processUpload(currentUser, documents, describesRecord, file);
		}

        if (webcam != null) {
            processUpload(currentUser, documents, describesRecord, webcam);
        }

		Set<DocumentApiModel> result = new THashSet<>();
		Map<UUID, Object> cache = new THashMap<>();

		for (Document d: documents) {
			result.add(documentToEmber.convert(d, cache));
		}


		EmberModel.Builder<DocumentApiModel> b = new EmberModel.Builder<DocumentApiModel>(result);
		return b.build();
	}

    private void processUpload(User currentUser, Set<Document> documents, NamedEntity describesRecord, MultipartFile file) throws BaseRestException {
        Document d = new Document();
        d.setName(file.getOriginalFilename());
        d.setSize(file.getSize());
        d.setContentType(file.getContentType());
		if (d.getContentType() == null) {
			d.setContentType("application/octet-stream");
		}

        d.setDescribes(new THashSet<>());
        d.getDescribes().add(describesRecord);

        d = documentService.create(d, currentUser);
        if (d == null) {
            return;
        }

        try {
            storageManager.upload(d.getId(), file, documentAnalysisDoneService);
        } catch (IOException ex) {
            ex.printStackTrace();
            documentService.delete(d.getId(), currentUser);
            return;
        }

        documents.add(d);
    }
}
