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
public class CodeService extends AbstractRestService<CodeRepository, Code, CodeApiModel> {
    @Autowired
    public CodeService(CodeRepository repository,
                       CodeToEmber dbToRest,
                       EmberToCode restToDb,
                       UuidGenerator uuidGenerator) {
        super(repository, dbToRest, restToDb, uuidGenerator);
    }

    @Override
    protected Iterable<Code> getAllEntities(User currentUser) {
        return getRepository().findByOwner(currentUser);
    }

    public CodeApiModel getByTypeAndCode(String type, String code, User currentUser) throws EntityNotFound, PermissionDenied {
        Code c = getRepository().findByTypeAndCodeAndOwner(type, code, currentUser);
        if (c == null) {
            throw new EntityNotFound();
        }

        return get(c.getId(), currentUser);
    }
}
