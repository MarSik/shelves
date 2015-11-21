package org.marsik.elshelves.backend.entities.converters;

import org.marsik.elshelves.api.entities.CodeApiModel;
import org.marsik.elshelves.backend.entities.Code;
import org.marsik.elshelves.backend.entities.NamedEntity;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Service
public class EmberToCode extends AbstractEmberToEntity<CodeApiModel, Code> {
    public EmberToCode() {
        super(Code.class);
    }

    @Override
    public Code convert(String path, CodeApiModel object, Code model, Map<UUID, Object> cache, Set<String> include) {
        model.setType(object.getType());
        model.setCode(object.getCode());

        if (object.getReference() != null) {
            NamedEntity e = new NamedEntity();
            e.setId(object.getReference().getId());
            model.setReference(e);
        } else {
            model.setReference(null);
        }

        return model;
    }
}
