package org.marsik.elshelves.backend.entities.converters;

import org.marsik.elshelves.api.entities.AbstractEntityApiModel;
import org.marsik.elshelves.backend.entities.IdentifiedEntity;

import java.util.Map;
import java.util.Set;
import java.util.UUID;


public interface EntityToEmberConversionService extends CachingConverter<IdentifiedEntity, AbstractEntityApiModel, UUID> {
    @Override
    AbstractEntityApiModel convert(String path, String element, IdentifiedEntity object, Map<UUID, Object> cache, Set<String> include);

    @Override
    AbstractEntityApiModel convert(IdentifiedEntity object, Map<UUID, Object> cache);

    @Override
    AbstractEntityApiModel convert(String path, IdentifiedEntity object, AbstractEntityApiModel model, Map<UUID, Object> cache, Set<String> include);

    <F extends IdentifiedEntity, T extends AbstractEntityApiModel> void register(Class<? extends F> source, CachingConverter<F, T, UUID> converter);

    <F extends IdentifiedEntity, T extends AbstractEntityApiModel> CachingConverter<F, T, UUID> converter(F entity, Class<T> dest);
}
