package org.marsik.elshelves.backend.services;

import org.marsik.elshelves.backend.controllers.exceptions.BaseRestException;
import org.marsik.elshelves.backend.controllers.exceptions.EntityNotFound;
import org.marsik.elshelves.backend.controllers.exceptions.PermissionDenied;
import org.marsik.elshelves.backend.entities.Document;
import org.marsik.elshelves.backend.entities.Requirement;
import org.marsik.elshelves.backend.entities.User;
import org.marsik.elshelves.backend.repositories.DocumentRepository;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public interface DocumentService extends AbstractRestServiceIntf<DocumentRepository, Document> {
    List<Requirement> analyzeSchematics(UUID uuid, User currentUser) throws EntityNotFound, PermissionDenied,
            IOException;

    void processUpload(Document d, MultipartFile file) throws BaseRestException, IOException;
}
