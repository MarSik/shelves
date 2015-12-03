package org.marsik.elshelves.backend.entities.converters;

import org.marsik.elshelves.api.entities.AuthorizationApiModel;
import org.marsik.elshelves.backend.entities.Authorization;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Service
@DependsOn("EntityToEmberConversionService")
public class EmberToAuthorization extends AbstractEmberToEntity<AuthorizationApiModel, Authorization> {
    public EmberToAuthorization() {
        super(Authorization.class);
    }

    @PostConstruct
    void postConstruct() {
        conversionService.register(AuthorizationApiModel.class, getTarget(), this);
    }

    @Autowired
    EmberToEntityConversionService conversionService;

    @Override
    public Authorization convert(String path, AuthorizationApiModel object, Authorization model, Map<UUID, Object> cache, Set<String> include) {
        model.setId(object.getId());
        model.setName(object.getName());
        model.setSecret(object.getSecret());
        return model;
    }
}
