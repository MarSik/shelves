package org.marsik.elshelves.backend.entities.converters;

import org.marsik.elshelves.api.entities.BoxApiModel;
import org.marsik.elshelves.backend.entities.Box;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
public class EmberToBox implements CachingConverter<BoxApiModel, Box, UUID> {
    @Autowired
    EmberToUser emberToUser;

    @Override
    public Box convert(BoxApiModel object, int nested, Map<UUID, Object> cache) {
        if (object == null) {
            return null;
        }

        if (cache.containsKey(object.getId())) {
            return (Box)cache.get(object.getId());
        }

        Box box = new org.marsik.elshelves.backend.entities.Box();
		cache.put(object.getId(), box);

		return convert(object, box, nested, cache);
    }

	@Override
	public Box convert(BoxApiModel object, Box box, int nested, Map<UUID, Object> cache) {
		box.setName(object.getName());
		box.setOwner(emberToUser.convert(object.getBelongsTo(), 1, cache));
		box.setParent(convert(object.getParent(), 1, cache));
		box.setUuid(object.getId());

		return box;
	}
}
