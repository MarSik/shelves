package org.marsik.elshelves.backend.entities.converters;

import org.marsik.elshelves.api.entities.AbstractEntityApiModel;
import org.marsik.elshelves.backend.entities.IdentifiedEntity;

import java.util.UUID;

public interface EmberToEntityConversionService {
    <F extends AbstractEntityApiModel, T extends IdentifiedEntity> CachingConverter<F, T, UUID> converter(F entity, Class<T> dest);

    <F extends AbstractEntityApiModel, T extends IdentifiedEntity> void register(Class<? extends F> source, CachingConverter<F, T, UUID> converter);
}
