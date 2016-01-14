package org.marsik.elshelves.backend.services;

import org.marsik.elshelves.backend.controllers.exceptions.EntityNotFound;
import org.marsik.elshelves.backend.controllers.exceptions.PermissionDenied;
import org.marsik.elshelves.backend.entities.Code;
import org.marsik.elshelves.backend.entities.User;
import org.marsik.elshelves.backend.repositories.CodeRepository;

public interface CodeService extends AbstractRestServiceIntf<CodeRepository, Code> {
    Code getByTypeAndCode(String type, String code, User currentUser) throws EntityNotFound, PermissionDenied;
}
