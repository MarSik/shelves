package org.marsik.elshelves.backend.services;

import gnu.trove.map.hash.THashMap;
import org.apache.commons.lang3.StringUtils;
import org.marsik.elshelves.api.entities.DocumentApiModel;
import org.marsik.elshelves.api.entities.RequirementApiModel;
import org.marsik.elshelves.backend.controllers.exceptions.EntityNotFound;
import org.marsik.elshelves.backend.controllers.exceptions.OperationNotPermitted;
import org.marsik.elshelves.backend.controllers.exceptions.PermissionDenied;
import org.marsik.elshelves.backend.entities.Document;
import org.marsik.elshelves.backend.entities.User;
import org.marsik.elshelves.backend.entities.converters.DocumentToEmber;
import org.marsik.elshelves.backend.entities.converters.EmberToDocument;
import org.marsik.elshelves.backend.repositories.DocumentRepository;
import org.marsik.elshelves.backend.repositories.TypeRepository;
import org.marsik.elshelves.kicad.SchemaComponents;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class DocumentService extends AbstractRestService<DocumentRepository, Document, DocumentApiModel> {
    @Autowired
    StorageManager storageManager;

	@Autowired
	FileAnalysisDoneHandler documentAnalysisDoneService;

    @Autowired
    TypeRepository typeRepository;

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

    public List<RequirementApiModel> analyzeSchematics(UUID uuid, User currentUser) throws EntityNotFound, PermissionDenied, IOException {
        Document document = getSingleEntity(uuid);
        List<RequirementApiModel> requirements = new ArrayList<>();

        if (document == null) {
            throw new EntityNotFound();
        }

        if (!document.getOwner().equals(currentUser)) {
            throw new PermissionDenied();
        }

        SchemaComponents schemaComponents = new SchemaComponents();
        Map<String, List<SchemaComponents.Component>> components = schemaComponents.fetchComponents(storageManager.retrieve(uuid));

        Map<String, List<String>> bom = new THashMap<>();

        for (Map.Entry<String, List<SchemaComponents.Component>> e: components.entrySet()) {
            SchemaComponents.Component c = e.getValue().get(0);

            String summary = c.type;

            if (c.value != null && !c.value.isEmpty()) {
                summary += " " + c.value;
            }

            if (c.footprint != null && !c.footprint.isEmpty()) {
                summary += " " + c.footprint;
            }

            if (!bom.containsKey(summary)) {
                bom.put(summary, new ArrayList<String>());
            }

            bom.get(summary).add(e.getKey());
        }

        for (Map.Entry<String, List<String>> e: bom.entrySet()) {
            RequirementApiModel r = new RequirementApiModel();
            r.setName(StringUtils.join(e.getValue(), ", "));
            r.setSummary(e.getKey());
            r.setCount((long) e.getValue().size());
            requirements.add(r);
        }

        return requirements;
    }
}
