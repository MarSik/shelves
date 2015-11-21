package org.marsik.elshelves.backend.entities.converters;

import org.marsik.elshelves.api.entities.AuthorizationApiModel;
import org.marsik.elshelves.backend.entities.Authorization;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Service
public class EmberToAuthorization extends AbstractEmberToEntity<AuthorizationApiModel, Authorization> {
    public EmberToAuthorization() {
        super(Authorization.class);
    }

    @Override
    public Authorization convert(String path, AuthorizationApiModel object, Authorization model, Map<UUID, Object> cache, Set<String> include) {
        model.setId(object.getId());
        model.setName(object.getName());
        model.setSecret(object.getSecret());
        return model;
    }
}
