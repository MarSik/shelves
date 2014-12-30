package org.marsik.elshelves.backend.entities.converters;

import org.marsik.elshelves.api.entities.BoxApiModel;
import org.marsik.elshelves.backend.entities.Box;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class BoxToEmber implements CachingConverter<Box, BoxApiModel, UUID> {
    @Autowired
    UserToEmber userToEmber;

	@Override
	public BoxApiModel convert(Box box, BoxApiModel model, int nested, Map<UUID, Object> cache) {
		model.setId(box.getUuid());
		model.setBelongsTo(userToEmber.convert(box.getOwner(), 1, cache));
		model.setName(box.getName());
		model.setParent(convert(box.getParent(), 1, cache));

		if (box.getContains() != null) {
			List<BoxApiModel> boxes = new ArrayList<>();
			for (Box b : box.getContains()) {
				boxes.add(convert(b, 1, cache));
			}
			model.setBoxes(boxes);
		}

		return model;
	}

	@Override
    public BoxApiModel convert(Box box, int nested, Map<UUID, Object> cache) {
        if (box == null) {
            return null;
        }

        if (cache.containsKey(box.getUuid())) {
            return (BoxApiModel)cache.get(box.getUuid());
        }

        BoxApiModel model = new BoxApiModel();

		if (nested > 0
				&& box.getUuid() != null) {
			cache.put(box.getUuid(), model);
		}

		return convert(box, model, nested, cache);
    }
}
