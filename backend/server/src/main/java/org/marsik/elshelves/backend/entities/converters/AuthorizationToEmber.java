package org.marsik.elshelves.backend.entities.converters;

import org.marsik.elshelves.api.entities.AuthorizationApiModel;
import org.marsik.elshelves.backend.entities.Authorization;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
public class AuthorizationToEmber extends AbstractEntityToEmber<Authorization, AuthorizationApiModel> {
    public AuthorizationToEmber() {
        super(AuthorizationApiModel.class);
    }

    @Override
    public AuthorizationApiModel convert(Authorization object, AuthorizationApiModel model, int nested, Map<UUID, Object> cache) {
        model.setId(object.getId());
        model.setName(object.getName());
        return model;
    }
}
