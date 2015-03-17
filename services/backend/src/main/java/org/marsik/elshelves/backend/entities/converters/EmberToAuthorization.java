package org.marsik.elshelves.backend.entities.converters;

import org.marsik.elshelves.api.entities.AuthorizationApiModel;
import org.marsik.elshelves.backend.entities.Authorization;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
public class EmberToAuthorization extends AbstractEmberToEntity<AuthorizationApiModel, Authorization> {
    public EmberToAuthorization() {
        super(Authorization.class);
    }

    @Override
    public Authorization convert(AuthorizationApiModel object, Authorization model, int nested, Map<UUID, Object> cache) {
        model.setUuid(object.getId());
        model.setName(object.getName());
        model.setSecret(object.getSecret());
        return model;
    }
}
