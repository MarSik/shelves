package org.marsik.elshelves.backend.entities.converters;

import gnu.trove.set.hash.THashSet;
import org.marsik.elshelves.api.entities.ItemApiModel;
import org.marsik.elshelves.api.entities.RequirementApiModel;
import org.marsik.elshelves.backend.entities.Item;
import org.marsik.elshelves.backend.entities.Requirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Service
public class ItemToEmber extends AbstractEntityToEmber<Item, ItemApiModel> {
	@Autowired
	LotToEmber lotToEmber;

	@Autowired
	RequirementToEmber requirementToEmber;

	public ItemToEmber() {
		super(ItemApiModel.class);
	}

	@Override
	public ItemApiModel convert(String path, Item object, ItemApiModel model, Map<UUID, Object> cache, Set<String> include) {
		lotToEmber.convert(path, object, model, cache, include);

		model.setFinished(object.getFinished());

		if (object.getRequires() != null) {
			model.setRequirements(new THashSet<RequirementApiModel>());
			for (Requirement r: object.getRequires()) {
				model.getRequirements().add(requirementToEmber.convert(path, "requirement", r, cache, include));
			}
		}

		return model;
	}
}
