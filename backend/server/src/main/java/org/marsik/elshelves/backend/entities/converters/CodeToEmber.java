package org.marsik.elshelves.backend.entities.converters;

import org.marsik.elshelves.api.entities.AbstractNamedEntityApiModel;
import org.marsik.elshelves.api.entities.CodeApiModel;
import org.marsik.elshelves.backend.entities.Code;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Service
@DependsOn("EntityToEmberConversionService")
public class CodeToEmber extends AbstractEntityToEmber<Code, CodeApiModel> {
    @Autowired
    UserToEmber userToEmber;

    @Autowired
    EntityToEmberConversionService conversionService;

    public CodeToEmber() {
        super(CodeApiModel.class);
    }

    @PostConstruct
    void postConstruct() {
        conversionService.register(Code.class, this);
    }

    @Override
    public CodeApiModel convert(String path, Code object, CodeApiModel model, Map<UUID, Object> cache, Set<String> include) {
        model.setType(object.getType());
        model.setCode(object.getCode());

        model.setBelongsTo(userToEmber.convert(path, "belongs-to", object.getOwner(), cache, include));

        if (object.getReference() != null) {
            AbstractNamedEntityApiModel r = conversionService.converter(object.getReference(), AbstractNamedEntityApiModel.class)
                    .convert(path, "reference", object.getReference(), cache, include);
            model.setReference(r);
        }

        return model;
    }
}
