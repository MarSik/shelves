package org.marsik.elshelves.backend.services;

import org.marsik.elshelves.backend.controllers.exceptions.EntityNotFound;
import org.marsik.elshelves.backend.controllers.exceptions.PermissionDenied;
import org.marsik.elshelves.backend.entities.Code;
import org.marsik.elshelves.backend.entities.OwnedEntity;
import org.marsik.elshelves.backend.entities.User;
import org.marsik.elshelves.backend.repositories.CodeRepository;
import org.marsik.elshelves.backend.repositories.OwnedEntityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.UUID;

@Service
public class IdentifiedEntityService extends AbstractReadOnlyRestService<OwnedEntityRepository, OwnedEntity> implements IdentifiedEntityServiceInterface {
    @Autowired
    CodeRepository codeRepository;

    @Autowired
    public IdentifiedEntityService(OwnedEntityRepository repository) {
        super(repository);
    }

    @Override
    protected Iterable<OwnedEntity> getAllEntities(User currentUser) {
        return Collections.emptySet();
    }

    @Override
    public OwnedEntity get(UUID uuid, User currentUser) throws PermissionDenied, EntityNotFound {
        OwnedEntity one = getSingleEntity(uuid);

        if (one == null) {
            Code code = codeRepository.findByTypeAndCodeAndOwner("QR+SHV", uuid.toString(), currentUser);
            if (code != null) {
                one = code.getReference();
            }
        }

        if (one == null) {
            throw new EntityNotFound();
        }

        if (!one.getOwner().equals(currentUser)) {
            throw new PermissionDenied();
        }

        return one;
    }
}
