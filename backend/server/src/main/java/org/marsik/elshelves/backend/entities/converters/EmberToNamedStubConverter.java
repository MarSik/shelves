package org.marsik.elshelves.backend.entities.converters;

import org.marsik.elshelves.api.entities.AbstractEntityApiModel;
import org.marsik.elshelves.api.entities.AbstractNamedEntityApiModel;
import org.marsik.elshelves.api.entities.SourceApiModel;
import org.marsik.elshelves.backend.entities.IdentifiedEntity;
import org.marsik.elshelves.backend.entities.NamedEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Service
@DependsOn("EntityToEmberConversionService")
public class EmberToNamedStubConverter extends AbstractEmberToEntity<AbstractNamedEntityApiModel, NamedEntity> {
    public EmberToNamedStubConverter() {
        super(NamedEntity.class);
    }

    @Autowired
    EmberToEntityConversionService conversionService;

    @PostConstruct
    void postConstruct() {
        conversionService.register(AbstractNamedEntityApiModel.class, getTarget(), this);
    }

    @Override
    public NamedEntity convert(String path,
            AbstractNamedEntityApiModel object,
            NamedEntity model,
            Map<UUID, Object> cache,
            Set<String> include) {
        return model;
    }
}
