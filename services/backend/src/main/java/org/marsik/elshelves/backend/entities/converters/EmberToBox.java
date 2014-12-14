package org.marsik.elshelves.backend.entities.converters;

import org.marsik.elshelves.api.entities.Box;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
public class EmberToBox implements CachingConverter<Box, org.marsik.elshelves.backend.entities.Box, UUID> {
    @Override
    public org.marsik.elshelves.backend.entities.Box convert(Box object, Map<UUID, Object> cache) {
        if (cache.containsKey(object.getId())) {
            return (org.marsik.elshelves.backend.entities.Box)cache.get(object.getId());
        }

        return null;
    }
}
