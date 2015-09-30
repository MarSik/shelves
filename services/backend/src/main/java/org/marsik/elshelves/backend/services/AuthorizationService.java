package org.marsik.elshelves.backend.services;

import org.marsik.elshelves.backend.controllers.exceptions.EntityNotFound;
import org.marsik.elshelves.backend.controllers.exceptions.OperationNotPermitted;
import org.marsik.elshelves.backend.controllers.exceptions.PermissionDenied;
import org.marsik.elshelves.backend.entities.Authorization;
import org.marsik.elshelves.backend.entities.User;
import org.marsik.elshelves.backend.repositories.AuthorizationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthorizationService extends AbstractRestService<AuthorizationRepository, Authorization> {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    public AuthorizationService(AuthorizationRepository repository,
                                UuidGenerator uuidGenerator) {
        super(repository, uuidGenerator);
    }

    @Override
    protected Iterable<Authorization> getAllEntities(User currentUser) {
        return getRepository().findByOwner(currentUser);
    }

    @Override
    public Authorization update(Authorization dto, User currentUser) throws PermissionDenied, OperationNotPermitted, EntityNotFound {
        throw new OperationNotPermitted();
    }

    @Override
    protected Authorization createEntity(Authorization dto, User currentUser) {
        String hashed = passwordEncoder.encode(dto.getSecret());
        dto.setSecret(hashed);
        return super.createEntity(dto, currentUser);
    }
}
