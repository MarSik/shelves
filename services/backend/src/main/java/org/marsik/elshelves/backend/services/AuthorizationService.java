package org.marsik.elshelves.backend.services;

import org.marsik.elshelves.api.entities.AuthorizationApiModel;
import org.marsik.elshelves.backend.controllers.exceptions.EntityNotFound;
import org.marsik.elshelves.backend.controllers.exceptions.OperationNotPermitted;
import org.marsik.elshelves.backend.controllers.exceptions.PermissionDenied;
import org.marsik.elshelves.backend.entities.Authorization;
import org.marsik.elshelves.backend.entities.User;
import org.marsik.elshelves.backend.entities.converters.AuthorizationToEmber;
import org.marsik.elshelves.backend.entities.converters.CachingConverter;
import org.marsik.elshelves.backend.entities.converters.EmberToAuthorization;
import org.marsik.elshelves.backend.repositories.AuthorizationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AuthorizationService extends AbstractRestService<AuthorizationRepository, Authorization, AuthorizationApiModel> {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    public AuthorizationService(AuthorizationRepository repository,
                                AuthorizationToEmber dbToRest,
                                EmberToAuthorization restToDb,
                                UuidGenerator uuidGenerator) {
        super(repository, dbToRest, restToDb, uuidGenerator);
    }

    @Override
    protected Iterable<Authorization> getAllEntities(User currentUser) {
        return getRepository().findByOwner(currentUser);
    }

    @Override
    public AuthorizationApiModel update(UUID uuid, AuthorizationApiModel dto, User currentUser) throws PermissionDenied, OperationNotPermitted, EntityNotFound {
        throw new OperationNotPermitted();
    }

    @Override
    protected Authorization createEntity(AuthorizationApiModel dto, User currentUser) {
        String hashed = passwordEncoder.encode(dto.getSecret());
        dto.setSecret(hashed);
        return super.createEntity(dto, currentUser);
    }
}
