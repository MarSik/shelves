package org.marsik.elshelves.backend.entities.converters;

import gnu.trove.set.hash.THashSet;
import org.marsik.elshelves.api.entities.ItemApiModel;
import org.marsik.elshelves.api.entities.RequirementApiModel;
import org.marsik.elshelves.backend.entities.Item;
import org.marsik.elshelves.backend.entities.Requirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
public class EmberToItem extends AbstractEmberToEntity<ItemApiModel, Item> {
    @Autowired
	EmberToRequirement emberToRequirement;

	@Autowired
	EmberToLot emberToLot;

	public EmberToItem() {
		super(Item.class);
	}

	@Override
	public Item convert(ItemApiModel object, Item item, int nested, Map<UUID, Object> cache) {
		emberToLot.convert(object, item, nested, cache);

		item.setFinished(object.getFinished());

		if (object.getRequirements() != null) {
			item.setRequires(new THashSet<Requirement>());
			for (RequirementApiModel r: object.getRequirements()) {
				item.addRequirement(emberToRequirement.convert(r, nested, cache));
			}
		}

		return item;
	}
}
