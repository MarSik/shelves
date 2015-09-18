package org.marsik.elshelves.backend.controllers;

import gnu.trove.set.hash.THashSet;
import org.marsik.elshelves.api.ember.EmberModel;
import org.marsik.elshelves.api.entities.DocumentApiModel;
import org.marsik.elshelves.api.entities.PolymorphicRecord;
import org.marsik.elshelves.backend.controllers.exceptions.EntityNotFound;
import org.marsik.elshelves.backend.controllers.exceptions.OperationNotPermitted;
import org.marsik.elshelves.backend.controllers.exceptions.PermissionDenied;
import org.marsik.elshelves.backend.entities.NamedEntity;
import org.marsik.elshelves.backend.entities.User;
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
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/upload")
public class UploadController {
	@Autowired
	DocumentService documentService;

	@Autowired
	StorageManager storageManager;

	@Autowired
	FileAnalysisDoneHandler documentAnalysisDoneService;

	@Autowired
	NamedEntityRepository namedEntityRepository;

	@Transactional
	@RequestMapping(method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.OK)
	public EmberModel upload(@CurrentUser User currentUser,
					   @RequestParam("files[]") MultipartFile[] files,
                       @RequestParam(value = "webcam", required = false) MultipartFile webcam,
					   @RequestParam("entity") UUID entity,
                       HttpServletRequest request) throws IOException, OperationNotPermitted, EntityNotFound, PermissionDenied {
		NamedEntity e;

		if (entity != null) {
			e = namedEntityRepository.findByUuid(entity);
			if (e == null) {
				throw new EntityNotFound();
			}

			if (!e.getOwner().equals(currentUser)) {
				throw new PermissionDenied();
			}
		}

		Set<DocumentApiModel> documents = new THashSet<>();

		PolymorphicRecord describesRecord = new PolymorphicRecord();
		describesRecord.setId(entity);

		for (MultipartFile file: files) {
            processUpload(currentUser, documents, describesRecord, file);
		}

        if (webcam != null) {
            processUpload(currentUser, documents, describesRecord, webcam);
        }

		EmberModel.Builder<DocumentApiModel> b = new EmberModel.Builder<DocumentApiModel>(DocumentApiModel.class, documents);
		return b.build();
	}

    private void processUpload(User currentUser, Set<DocumentApiModel> documents, PolymorphicRecord describesRecord, MultipartFile file) throws OperationNotPermitted, PermissionDenied, EntityNotFound {
        DocumentApiModel d = new DocumentApiModel();
        d.setName(file.getOriginalFilename());
        d.setSize(file.getSize());
        d.setContentType(file.getContentType());
        d.setDescribes(new THashSet<PolymorphicRecord>());
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
