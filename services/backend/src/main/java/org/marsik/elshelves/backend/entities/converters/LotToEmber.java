package org.marsik.elshelves.backend.entities.converters;

import gnu.trove.set.hash.THashSet;
import org.marsik.elshelves.api.entities.LotApiModel;
import org.marsik.elshelves.backend.entities.Lot;
import org.marsik.elshelves.backend.entities.Purchase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;

@Service
public class LotToEmber implements CachingConverter<Lot, LotApiModel, UUID> {
	@Autowired
	UserToEmber userToEmber;

	@Autowired
	BoxToEmber boxToEmber;

	@Autowired
	TypeToEmber typeToEmber;

	@Autowired
	PurchaseToEmber purchaseToEmber;

	@Autowired
	LotBaseToEmber lotBaseToEmber;

	@Autowired
	RequirementToEmber requirementToEmber;

	protected LotApiModel createEntity() {
		return new LotApiModel();
	}

	public LotApiModel convert(Lot object, LotApiModel entity, int nested, Map<UUID, Object> cache) {
		lotBaseToEmber.convert(object, entity, nested, cache);
		entity.setAction(object.getAction());
		entity.setPurchase(purchaseToEmber.convert(object.getPurchase(), nested, cache));
		entity.setPerformedBy(userToEmber.convert(object.getPerformedBy(), nested, cache));
		entity.setUsedBy(requirementToEmber.convert(object.getUsedBy(), nested, cache));

		if (nested == 0) {
			return entity;
		}


		entity.setLocation(boxToEmber.convert(object.getLocation(), nested - 1, cache));
		entity.setPrevious(convert(object.getPrevious(), nested - 1, cache));

		return entity;
	}

	@Override
	public LotApiModel convert(Lot object, int nested, Map<UUID, Object> cache) {
		if (object == null) {
			return null;
		}

		if (cache.containsKey(object.getUuid())) {
			return (LotApiModel)cache.get(object.getUuid());
		}

		LotApiModel entity = createEntity();
		if (nested > 0
				&& object.getUuid() != null) {
			cache.put(object.getUuid(), entity);
		}
		return convert(object, entity, nested, cache);
	}
}
