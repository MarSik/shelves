package org.marsik.elshelves.backend.entities.converters;

import org.marsik.elshelves.api.entities.CodeApiModel;
import org.marsik.elshelves.backend.entities.Code;
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
public class EmberToCode extends AbstractEmberToEntity<CodeApiModel, Code> {
    public EmberToCode() {
        super(Code.class);
    }

    @PostConstruct
    void postConstruct() {
        conversionService.register(CodeApiModel.class, this);
    }

    @Autowired
    EmberToEntityConversionService conversionService;

    @Override
    public Code convert(String path, CodeApiModel object, Code model, Map<UUID, Object> cache, Set<String> include) {
        model.setType(object.getType());
        model.setCode(object.getCode());

        if (object.getReference() != null) {
            NamedEntity e = conversionService.converter(object.getReference(), NamedEntity.class)
                    .convert(path, "reference", object.getReference(), cache, include);
            model.setReference(e);
        } else {
            model.setReference(null);
        }

        return model;
    }
}
