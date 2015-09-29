package org.marsik.elshelves.backend.services;

import org.marsik.elshelves.api.entities.CodeApiModel;
import org.marsik.elshelves.backend.controllers.exceptions.EntityNotFound;
import org.marsik.elshelves.backend.controllers.exceptions.PermissionDenied;
import org.marsik.elshelves.backend.entities.Code;
import org.marsik.elshelves.backend.entities.User;
import org.marsik.elshelves.backend.entities.converters.CodeToEmber;
import org.marsik.elshelves.backend.entities.converters.EmberToCode;
import org.marsik.elshelves.backend.repositories.CodeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CodeService extends AbstractRestService<CodeRepository, Code> {
    @Autowired
    public CodeService(CodeRepository repository,
                       UuidGenerator uuidGenerator) {
        super(repository, uuidGenerator);
    }

    @Override
    protected Iterable<Code> getAllEntities(User currentUser) {
        return getRepository().findByOwner(currentUser);
    }

    public Code getByTypeAndCode(String type, String code, User currentUser) throws EntityNotFound, PermissionDenied {
        Code c = getRepository().findByTypeAndCodeAndOwner(type, code, currentUser);
        if (c == null) {
            throw new EntityNotFound();
        }

        return get(c.getId(), currentUser);
    }
}
