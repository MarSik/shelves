package org.marsik.elshelves.backend.entities.converters;

import gnu.trove.set.hash.THashSet;
import org.marsik.elshelves.api.entities.ItemApiModel;
import org.marsik.elshelves.api.entities.RequirementApiModel;
import org.marsik.elshelves.backend.entities.IdentifiedEntity;
import org.marsik.elshelves.backend.entities.Item;
import org.marsik.elshelves.backend.entities.Requirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;
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
	public Item convert(String path, ItemApiModel object, Item item, Map<UUID, Object> cache, Set<String> include) {
		emberToLot.convert(path, object, item, cache, include);

		item.setFinished(object.getFinished());

		if (object.getRequirements() != null) {
			item.setRequires(new THashSet<Requirement>());
			for (RequirementApiModel r: object.getRequirements()) {
				item.addRequirement(emberToRequirement.convert(path, "requirement", r, cache, include));
			}
		} else {
			item.setRequires(new IdentifiedEntity.UnprovidedSet<>());
		}

		return item;
	}
}
