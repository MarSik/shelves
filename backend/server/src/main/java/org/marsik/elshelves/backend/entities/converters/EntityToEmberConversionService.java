package org.marsik.elshelves.backend.entities.converters;

import org.marsik.elshelves.api.entities.AbstractEntityApiModel;
import org.marsik.elshelves.backend.entities.IdentifiedEntity;
import org.marsik.elshelves.backend.services.AbstractConversionService;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service("EntityToEmberConversionService")
public class EntityToEmberConversionService extends AbstractConversionService<IdentifiedEntity, AbstractEntityApiModel, UUID> {
}
