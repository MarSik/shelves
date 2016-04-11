package org.marsik.elshelves.backend.entities.converters;

import org.marsik.elshelves.api.entities.AbstractEntityApiModel;
import org.marsik.elshelves.backend.entities.IdentifiedEntity;
import org.marsik.elshelves.backend.services.AbstractConversionService;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service("EmberToEntityConversionService")
public class EmberToEntityConversionServiceImpl extends AbstractConversionService<AbstractEntityApiModel, IdentifiedEntity, UUID> implements EmberToEntityConversionService {
    @Override
    public <F extends AbstractEntityApiModel, T extends IdentifiedEntity> CachingConverter<F, T, UUID> converter(F entity, Class<T> dest) {
        if (entity.getEntityType() != null) {
            return (CachingConverter<F, T, UUID>)converter(entity, entity.getEntityType());
        } else {
            return super.converter(entity, dest);
        }
    }
}
