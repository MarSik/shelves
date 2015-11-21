package org.marsik.elshelves.backend.entities.converters;

import org.marsik.elshelves.api.entities.CodeApiModel;
import org.marsik.elshelves.api.entities.PolymorphicRecord;
import org.marsik.elshelves.backend.entities.Code;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Service
public class CodeToEmber extends AbstractEntityToEmber<Code, CodeApiModel> {
    @Autowired
    UserToEmber userToEmber;

    public CodeToEmber() {
        super(CodeApiModel.class);
    }

    @Override
    public CodeApiModel convert(String path, Code object, CodeApiModel model, Map<UUID, Object> cache, Set<String> include) {
        model.setType(object.getType());
        model.setCode(object.getCode());

        model.setBelongsTo(userToEmber.convert(path, "belongs-to", object.getOwner(), cache, include));

        if (object.getReference() != null) {
            PolymorphicRecord r = new PolymorphicRecord();
            r.setId(object.getReference().getId());
            r.setType(object.getReference().getEmberType());
            model.setReference(r);
        }

        return model;
    }
}
