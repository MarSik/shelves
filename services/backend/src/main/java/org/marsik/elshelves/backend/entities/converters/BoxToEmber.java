package org.marsik.elshelves.backend.entities.converters;

import org.marsik.elshelves.backend.entities.Box;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class BoxToEmber implements CachingConverter<Box, org.marsik.elshelves.api.entities.Box, UUID> {
    @Autowired
    UserToEmber userToEmber;

    @Override
    public org.marsik.elshelves.api.entities.Box convert(Box box, Map<UUID, Object> cache) {
        if (box == null) {
            return null;
        }

        if (cache.containsKey(box.getUuid())) {
            return (org.marsik.elshelves.api.entities.Box)cache.get(box.getUuid());
        }

        org.marsik.elshelves.api.entities.Box model = new org.marsik.elshelves.api.entities.Box();
        model.setId(box.getUuid());
        model.setBelongsTo(userToEmber.convert(box.getOwner(), cache));
        model.setName(box.getName());
        model.setParent(convert(box.getParent(), cache));

        List<org.marsik.elshelves.api.entities.Box> boxes = new ArrayList<>();
        for (Box b: box.getContains()) {
            boxes.add(convert(b, cache));
        }
        model.setBoxes(boxes);
        return model;
    }
}
