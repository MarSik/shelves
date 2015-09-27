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
public class ItemToEmber implements CachingConverter<Item, ItemApiModel, UUID> {
	@Autowired
	LotToEmber lotToEmber;

	@Autowired
	RequirementToEmber requirementToEmber;

	@Override
	public ItemApiModel convert(Item object, int nested, Map<UUID, Object> cache) {
		if (object == null) {
			return null;
		}

		if (cache.containsKey(object.getUuid())) {
			return (ItemApiModel)cache.get(object.getUuid());
		}

		ItemApiModel model = new ItemApiModel();
		if (nested > 0
				&& object.getUuid() != null) {
			cache.put(object.getUuid(), model);
		}
		return convert(object, model, nested, cache);
	}

	@Override
	public ItemApiModel convert(Item object, ItemApiModel model, int nested, Map<UUID, Object> cache) {
		lotToEmber.convert(object, model, nested, cache);

		if (nested == 0) {
			return model;
		}

		if (object.getRequires() != null) {
			model.setRequirements(new THashSet<RequirementApiModel>());
			for (Requirement r: object.getRequires()) {
				model.getRequirements().add(requirementToEmber.convert(r, nested - 1, cache));
			}
		}

		return model;
	}
}
