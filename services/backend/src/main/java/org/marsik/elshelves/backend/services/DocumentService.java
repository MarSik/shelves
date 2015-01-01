package org.marsik.elshelves.backend.services;

import org.marsik.elshelves.api.entities.DocumentApiModel;
import org.marsik.elshelves.backend.entities.Document;
import org.marsik.elshelves.backend.entities.converters.CachingConverter;
import org.marsik.elshelves.backend.entities.converters.DocumentToEmber;
import org.marsik.elshelves.backend.entities.converters.EmberToDocument;
import org.marsik.elshelves.backend.repositories.DocumentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class DocumentService extends AbstractRestService<DocumentRepository, Document, DocumentApiModel> {
	@Autowired
	public DocumentService(DocumentRepository repository,
						   DocumentToEmber dbToRest,
						   EmberToDocument restToDb,
						   UuidGenerator uuidGenerator) {
		super(repository, dbToRest, restToDb, uuidGenerator);
	}
}
