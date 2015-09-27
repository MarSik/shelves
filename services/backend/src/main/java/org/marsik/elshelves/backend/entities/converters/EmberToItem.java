package org.marsik.elshelves.backend.entities.converters;

import gnu.trove.set.hash.THashSet;
import org.marsik.elshelves.api.entities.ItemApiModel;
import org.marsik.elshelves.api.entities.ProjectApiModel;
import org.marsik.elshelves.api.entities.RequirementApiModel;
import org.marsik.elshelves.backend.entities.Item;
import org.marsik.elshelves.backend.entities.Requirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
public class EmberToItem implements CachingConverter<ItemApiModel, Item, UUID> {
    @Autowired
	EmberToRequirement emberToRequirement;

	@Autowired
	EmberToLot emberToLot;

    @Override
    public Item convert(ItemApiModel object, int nested, Map<UUID, Object> cache) {
        if (object == null) {
            return null;
        }

        if (cache.containsKey(object.getId())) {
            return (Item)cache.get(object.getId());
        }

        Item item = new Item();

		if (nested > 0
				&& object.getId() != null) {
			cache.put(object.getId(), item);
		}

		return convert(object, item, nested, cache);
    }

	@Override
	public Item convert(ItemApiModel object, Item item, int nested, Map<UUID, Object> cache) {
		emberToLot.convert(object, item, nested, cache);

		item.setFinished(object.getFinished());

		if (object.getRequirements() != null) {
			item.setRequires(new THashSet<Requirement>());
			for (RequirementApiModel r: object.getRequirements()) {
				item.getRequires().add(emberToRequirement.convert(r, nested, cache));
			}
		}

		return item;
	}
}
