package org.marsik.elshelves.backend.entities.converters;

import gnu.trove.set.hash.THashSet;
import org.marsik.elshelves.api.entities.ItemApiModel;
import org.marsik.elshelves.api.entities.RequirementApiModel;
import org.marsik.elshelves.api.entities.fields.LotAction;
import org.marsik.elshelves.backend.entities.IdentifiedEntity;
import org.marsik.elshelves.backend.entities.Item;
import org.marsik.elshelves.backend.entities.Requirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Service
@DependsOn("EntityToEmberConversionService")
public class EmberToItem extends AbstractEmberToEntity<ItemApiModel, Item> {
    @Autowired
	EmberToRequirement emberToRequirement;

	@Autowired
	EmberToLot emberToLot;

	@Autowired
	EmberToEntityConversionService conversionService;

	public EmberToItem() {
		super(Item.class);
	}

	@PostConstruct
	void postConstruct() {
		conversionService.register(ItemApiModel.class, this);
	}

	@Override
	public Item convert(String path, ItemApiModel object, Item item, Map<UUID, Object> cache, Set<String> include) {
		emberToLot.convert(path, object, item, cache, include);

		if (object.getStatus() == LotAction.FINISHED) {
			object.setFinished(true);
		} else if (object.getStatus() == LotAction.REOPENED) {
			object.setFinished(false);
		}

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
