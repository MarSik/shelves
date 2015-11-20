package org.marsik.elshelves.backend.services;

import gnu.trove.map.hash.THashMap;
import gnu.trove.set.hash.THashSet;
import org.apache.commons.lang3.StringUtils;
import org.marsik.elshelves.backend.controllers.exceptions.BaseRestException;
import org.marsik.elshelves.backend.controllers.exceptions.EntityNotFound;
import org.marsik.elshelves.backend.controllers.exceptions.OperationNotPermitted;
import org.marsik.elshelves.backend.controllers.exceptions.PermissionDenied;
import org.marsik.elshelves.backend.entities.Document;
import org.marsik.elshelves.backend.entities.Requirement;
import org.marsik.elshelves.backend.entities.Type;
import org.marsik.elshelves.backend.entities.User;
import org.marsik.elshelves.backend.repositories.DocumentRepository;
import org.marsik.elshelves.kicad.SchemaComponents;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class DocumentService extends AbstractRestService<DocumentRepository, Document> {
    @Autowired
    StorageManager storageManager;

	@Autowired
	FileAnalysisDoneHandler documentAnalysisDoneService;

    @Autowired
    TypeService typeService;

	@Autowired
	public DocumentService(DocumentRepository repository,
						   UuidGenerator uuidGenerator) {
		super(repository, uuidGenerator);
	}

    @Override
    protected Iterable<Document> getAllEntities(User currentUser) {
        return getRepository().findByOwner(currentUser);
    }

    @Override
    public boolean delete(UUID uuid, User currentUser) throws BaseRestException {
        boolean res = super.delete(uuid, currentUser);
        if (res) {
            try {
                if (!storageManager.delete(uuid)) {
                    return false;
                }
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
    public Document update(Document dto, User currentUser) throws BaseRestException {
        Document doc = super.update(dto, currentUser);
        if (doc != null && doc.getId() != null && doc.getUrl() != null) {
            downloadDoc(doc.getId(), doc.getUrl());
        }
        return doc;
    }

    @Override
    public Document create(Document dto, User currentUser) throws OperationNotPermitted {
		if ((dto.getName() == null || dto.getName().isEmpty())
                && dto.getUrl() != null) {
			try {
				dto.setName(new File(dto.getUrl().toURI().getPath()).getName());
			} catch (URISyntaxException ex) {
				ex.printStackTrace();
			}
		}

        if (dto.getContentType() == null) {
            dto.setContentType("application/octet-stream");
        }

        if (dto.getSize() == null) {
            dto.setSize(0L);
        }

        Document doc = super.create(dto, currentUser);

        if (doc != null && doc.getId() != null && doc.getUrl() != null) {
            downloadDoc(doc.getId(), doc.getUrl());
        }
        return doc;
    }

    public List<Requirement> analyzeSchematics(UUID uuid, User currentUser) throws EntityNotFound, PermissionDenied, IOException {
        Document document = getSingleEntity(uuid);
        List<Requirement> requirements = new ArrayList<>();

        if (document == null) {
            throw new EntityNotFound();
        }

        if (!document.getOwner().equals(currentUser)) {
            throw new PermissionDenied();
        }

        SchemaComponents schemaComponents = new SchemaComponents();
        Map<String, List<SchemaComponents.Component>> components = schemaComponents.fetchComponents(storageManager.retrieve(uuid));

        Map<String, List<String>> bom = new THashMap<>();
        Map<String, Long> bomCount = new THashMap<>();
        Map<String, Type> bomType = new THashMap<>();

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
                bomCount.put(summary, 0L);

                if (c.type != null
                        && !c.type.isEmpty()
                        && c.footprint != null
                        && !c.footprint.isEmpty()) {
                    Type type = typeService.getUniqueTypeByNameAndFootprint(c.type, c.footprint, currentUser);
                    if (type != null) {
                        bomType.put(summary, type);
                    }
                }
            }

            bom.get(summary).add(e.getKey());
            bomCount.put(summary, bomCount.get(summary) + 1);
        }

        for (Map.Entry<String, List<String>> e: bom.entrySet()) {
            Requirement r = new Requirement();
            r.setName(StringUtils.join(e.getValue(), ", "));
            r.setSummary(e.getKey());
            r.setCount(bomCount.get(e.getKey()));

            if (bomType.containsKey(e.getKey())) {
                r.setType(new THashSet<Type>());
                r.getType().add(bomType.get(e.getKey()));
            }

            requirements.add(r);
        }

        return requirements;
    }

    public void processUpload(Document d, MultipartFile file) throws BaseRestException, IOException {
        d.setSize(file.getSize());
        d.setContentType(file.getContentType());
        if (d.getContentType() == null) {
            d.setContentType("application/octet-stream");
        }

        storageManager.upload(d.getId(), file, documentAnalysisDoneService);
    }
}
