package org.marsik.elshelves.backend.services;

import org.marsik.elshelves.backend.entities.OwnedEntity;
import org.marsik.elshelves.backend.entities.User;
import org.marsik.elshelves.backend.repositories.OwnedEntityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class IdentifiedEntityService extends AbstractReadOnlyRestService<OwnedEntityRepository, OwnedEntity> implements IdentifiedEntityServiceInterface {
    @Autowired
    public IdentifiedEntityService(OwnedEntityRepository repository) {
        super(repository);
    }

    @Override
    protected Iterable<OwnedEntity> getAllEntities(User currentUser) {
        return Collections.emptySet();
    }
}
