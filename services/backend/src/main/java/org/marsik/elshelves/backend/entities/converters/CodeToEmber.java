package org.marsik.elshelves.backend.entities.converters;

import gnu.trove.set.hash.THashSet;
import org.marsik.elshelves.api.entities.CodeApiModel;
import org.marsik.elshelves.api.entities.PolymorphicRecord;
import org.marsik.elshelves.backend.entities.Code;
import org.marsik.elshelves.backend.entities.NamedEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
public class CodeToEmber extends AbstractEntityToEmber<Code, CodeApiModel> {
    @Autowired
    UserToEmber userToEmber;

    public CodeToEmber() {
        super(CodeApiModel.class);
    }

    @Override
    public CodeApiModel convert(Code object, CodeApiModel model, int nested, Map<UUID, Object> cache) {
        model.setId(object.getUuid());

        if (nested == 0) {
            return model;
        }

        model.setType(object.getType());
        model.setCode(object.getCode());

        model.setBelongsTo(userToEmber.convert(object.getOwner(), nested - 1, cache));

        if (object.getReference() != null) {
            PolymorphicRecord r = new PolymorphicRecord();
            r.setId(object.getReference().getUuid());
            r.setType(object.getReference().getEmberType());
            model.setReference(r);
        }

        return model;
    }
}
