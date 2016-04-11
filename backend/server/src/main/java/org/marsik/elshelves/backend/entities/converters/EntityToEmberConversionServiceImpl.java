package org.marsik.elshelves.backend.entities.converters;

import org.marsik.elshelves.api.entities.AbstractEntityApiModel;
import org.marsik.elshelves.backend.entities.IdentifiedEntity;
import org.marsik.elshelves.backend.services.AbstractConversionService;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Service("EntityToEmberConversionService")
public class EntityToEmberConversionServiceImpl extends AbstractConversionService<IdentifiedEntity, AbstractEntityApiModel, UUID> implements EntityToEmberConversionService {
    @Override
    public AbstractEntityApiModel convert(String path, String element, IdentifiedEntity object, Map<UUID, Object> cache, Set<String> include) {
        return converter(object, AbstractEntityApiModel.class).convert(path, element, object, cache, include);
    }

    @Override
    public AbstractEntityApiModel convert(IdentifiedEntity object, Map<UUID, Object> cache) {
        return converter(object, AbstractEntityApiModel.class).convert(object, cache);
    }

    @Override
    public AbstractEntityApiModel convert(String path, IdentifiedEntity object, AbstractEntityApiModel model, Map<UUID, Object> cache, Set<String> include) {
        return converter(object, AbstractEntityApiModel.class).convert(path, object, model, cache, include);
    }
}
