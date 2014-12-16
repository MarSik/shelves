package org.marsik.elshelves.backend.entities.converters;

import org.marsik.elshelves.api.entities.Box;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
public class EmberToBox implements CachingConverter<Box, org.marsik.elshelves.backend.entities.Box, UUID> {
    @Autowired
    EmberToUser emberToUser;

    @Override
    public org.marsik.elshelves.backend.entities.Box convert(Box object, Map<UUID, Object> cache) {
        if (object == null) {
            return null;
        }

        if (cache.containsKey(object.getId())) {
            return (org.marsik.elshelves.backend.entities.Box)cache.get(object.getId());
        }

        org.marsik.elshelves.backend.entities.Box box = new org.marsik.elshelves.backend.entities.Box();
        box.setName(object.getName());
        box.setOwner(emberToUser.convert(object.getBelongsTo(), cache));
        box.setParent(convert(object.getParent(), cache));
        box.setUuid(object.getId());

        return box;
    }
}
