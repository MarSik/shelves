package org.marsik.elshelves.backend.entities.converters;

import org.marsik.elshelves.api.entities.CodeApiModel;
import org.marsik.elshelves.backend.entities.Code;
import org.marsik.elshelves.backend.entities.NamedEntity;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
public class EmberToCode extends AbstractEmberToEntity<CodeApiModel, Code> {
    public EmberToCode() {
        super(Code.class);
    }

    @Override
    public Code convert(CodeApiModel object, Code model, int nested, Map<UUID, Object> cache) {
        model.setType(object.getType());
        model.setCode(object.getCode());

        if (object.getReference() != null) {
            NamedEntity e = new NamedEntity();
            e.setId(object.getReference().getId());
            model.setReference(e);
        }

        return model;
    }
}
