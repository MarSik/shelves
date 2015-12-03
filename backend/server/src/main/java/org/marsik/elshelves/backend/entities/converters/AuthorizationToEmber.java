package org.marsik.elshelves.backend.entities.converters;

import org.marsik.elshelves.api.entities.AuthorizationApiModel;
import org.marsik.elshelves.backend.entities.Authorization;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Service
public class AuthorizationToEmber extends AbstractEntityToEmber<Authorization, AuthorizationApiModel> {
    @Autowired
    EntityToEmberConversionService conversionService;

    public AuthorizationToEmber() {
        super(AuthorizationApiModel.class);
    }

    @PostConstruct
    void postConstruct() {
        conversionService.register(Authorization.class, getTarget(), this);
    }

    @Override
    public AuthorizationApiModel convert(String path, Authorization object, AuthorizationApiModel model, Map<UUID, Object> cache, Set<String> include) {
        model.setId(object.getId());
        model.setName(object.getName());
        return model;
    }
}
