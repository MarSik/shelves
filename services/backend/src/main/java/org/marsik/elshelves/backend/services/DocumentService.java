package org.marsik.elshelves.backend.services;

import org.marsik.elshelves.api.entities.DocumentApiModel;
import org.marsik.elshelves.backend.controllers.exceptions.EntityNotFound;
import org.marsik.elshelves.backend.controllers.exceptions.OperationNotPermitted;
import org.marsik.elshelves.backend.controllers.exceptions.PermissionDenied;
import org.marsik.elshelves.backend.entities.Document;
import org.marsik.elshelves.backend.entities.User;
import org.marsik.elshelves.backend.entities.converters.DocumentToEmber;
import org.marsik.elshelves.backend.entities.converters.EmberToDocument;
import org.marsik.elshelves.backend.repositories.DocumentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.UUID;

@Service
public class DocumentService extends AbstractRestService<DocumentRepository, Document, DocumentApiModel> {
    @Autowired
    StorageManager storageManager;

	@Autowired
	FileAnalysisDoneHandler documentAnalysisDoneService;

	@Autowired
	public DocumentService(DocumentRepository repository,
						   DocumentToEmber dbToRest,
						   EmberToDocument restToDb,
						   UuidGenerator uuidGenerator) {
		super(repository, dbToRest, restToDb, uuidGenerator);
	}

    @Override
    protected Iterable<Document> getAllEntities(User currentUser) {
        return getRepository().findByOwner(currentUser);
    }

    @Override
    public boolean delete(UUID uuid, User currentUser) throws PermissionDenied, OperationNotPermitted, EntityNotFound {
        boolean res = super.delete(uuid, currentUser);
        if (res) {
            try {
                storageManager.delete(uuid);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return res;
    }

    @Async
    private void downloadDoc(UUID uuid, URL url) {
        try {
            storageManager.download(uuid, url, documentAnalysisDoneService);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public DocumentApiModel update(UUID uuid, DocumentApiModel dto, User currentUser) throws PermissionDenied, OperationNotPermitted, EntityNotFound {
        DocumentApiModel doc = super.update(uuid, dto, currentUser);
        if (doc != null && doc.getId() != null && doc.getUrl() != null) {
            downloadDoc(doc.getId(), doc.getUrl());
        }
        return doc;
    }

    @Override
    public DocumentApiModel create(DocumentApiModel dto, User currentUser) throws OperationNotPermitted {
        DocumentApiModel doc = super.create(dto, currentUser);

		if (doc != null
				&& (doc.getName() == null || doc.getName().isEmpty())
				&& doc.getUrl() != null) {
			try {
				doc.setName(new File(doc.getUrl().toURI().getPath()).getName());
			} catch (URISyntaxException ex) {
				ex.printStackTrace();
			}
		}

        if (doc != null && doc.getId() != null && doc.getUrl() != null) {
            downloadDoc(doc.getId(), doc.getUrl());
        }
        return doc;
    }
}
