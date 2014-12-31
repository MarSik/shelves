package org.marsik.elshelves.backend.entities.converters;

import gnu.trove.set.hash.THashSet;
import org.marsik.elshelves.api.entities.BoxApiModel;
import org.marsik.elshelves.api.entities.LotApiModel;
import org.marsik.elshelves.backend.entities.Box;
import org.marsik.elshelves.backend.entities.Lot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Service
public class BoxToEmber implements CachingConverter<Box, BoxApiModel, UUID> {
    @Autowired
    UserToEmber userToEmber;

	@Autowired
	LotToEmber lotToEmber;

	@Override
	public BoxApiModel convert(Box box, BoxApiModel model, int nested, Map<UUID, Object> cache) {
		model.setId(box.getUuid());
		model.setName(box.getName());

		if (nested == 0) {
			return model;
		}

		model.setParent(convert(box.getParent(), nested - 1, cache));
		model.setBelongsTo(userToEmber.convert(box.getOwner(), nested - 1, cache));

		if (box.getContains() != null) {
			Set<BoxApiModel> boxes = new THashSet<>();
			for (Box b : box.getContains()) {
				boxes.add(convert(b, nested - 1, cache));
			}
			model.setBoxes(boxes);
		}

		if (box.getLots() != null) {
			Set<LotApiModel> lots = new THashSet<>();
			for (Lot l: box.getLots()) {
				lots.add(lotToEmber.convert(l, nested - 1, cache));
			}
			model.setLots(lots);
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
