package org.marsik.elshelves.backend.entities.converters;

import org.joda.time.DateTime;
import org.marsik.elshelves.api.entities.LotApiModel;
import org.marsik.elshelves.backend.entities.Lot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

		if (nested == 0) {
			return entity;
		}

        entity.setExpiration(new DateTime(object.getExpiration()));
		entity.setAction(object.getAction());
		entity.setPurchase(purchaseToEmber.convert(object.getPurchase(), nested - 1, cache));
		entity.setPerformedBy(userToEmber.convert(object.getPerformedBy(), nested - 1, cache));
		entity.setUsedBy(requirementToEmber.convert(object.getUsedBy(), nested - 1, cache));

		entity.setLocation(boxToEmber.convert(object.getLocation(), nested - 1, cache));
		entity.setPrevious(convert(object.getPrevious(), nested - 1, cache));

		entity.setCanBeAssigned(object.isCanBeAssigned());
		entity.setCanBeUnassigned(object.isCanBeUnassigned());
		entity.setCanBeSoldered(object.isCanBeSoldered());
		entity.setCanBeUnsoldered(object.isCanBeUnsoldered());
        entity.setCanBeSplit(object.isCanBeSplit());
        entity.setCanBeMoved(object.isCanBeMoved());
        entity.setValid(object.isValid());

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
