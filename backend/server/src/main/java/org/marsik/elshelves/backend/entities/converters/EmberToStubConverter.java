package org.marsik.elshelves.backend.entities.converters;

import org.marsik.elshelves.api.entities.AbstractEntityApiModel;
import org.marsik.elshelves.api.entities.SourceApiModel;
import org.marsik.elshelves.backend.entities.IdentifiedEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Service
@DependsOn("EntityToEmberConversionService")
public class EmberToStubConverter extends AbstractEmberToEntity<AbstractEntityApiModel, IdentifiedEntity> {
    public EmberToStubConverter() {
        super(IdentifiedEntity.class);
    }

    @Autowired
    EmberToEntityConversionService conversionService;

    @PostConstruct
    void postConstruct() {
        conversionService.register(AbstractEntityApiModel.class, getTarget(), this);
    }

    @Override
    public IdentifiedEntity convert(String path,
            AbstractEntityApiModel object,
            IdentifiedEntity model,
            Map<UUID, Object> cache,
            Set<String> include) {
        return model;
    }
}
