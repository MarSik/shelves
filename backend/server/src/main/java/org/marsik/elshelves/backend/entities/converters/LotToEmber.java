package org.marsik.elshelves.backend.entities.converters;

import org.marsik.elshelves.api.entities.LotApiModel;
import org.marsik.elshelves.api.entities.LotHistoryApiModel;
import org.marsik.elshelves.backend.entities.Lot;
import org.modelmapper.ModelMapper;
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
	RequirementToEmber requirementToEmber;


	@Autowired
	ModelMapper modelMapper;

	protected LotApiModel createEntity() {
		return new LotApiModel();
	}

	public LotApiModel convert(Lot object, LotApiModel entity, int nested, Map<UUID, Object> cache) {
		entity.setId(object.getId());
		entity.setCount(object.getCount());

		if (nested == 0) {
			return entity;
		}

        entity.setExpiration(object.getExpiration());
		entity.setSerials(object.getSerials());
		entity.setAction(object.getStatus());
		entity.setPurchase(purchaseToEmber.convert(object.getPurchase(), nested - 1, cache));
		entity.setUsedBy(requirementToEmber.convert(object.getUsedBy(), nested - 1, cache));
		entity.setLocation(boxToEmber.convert(object.getLocation(), nested - 1, cache));
		entity.setHistory(modelMapper.map(object.getHistory(), LotHistoryApiModel.class));

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

		if (cache.containsKey(object.getId())) {
			return (LotApiModel)cache.get(object.getId());
		}

		LotApiModel entity = createEntity();
		if (nested > 0
				&& object.getId() != null) {
			cache.put(object.getId(), entity);
		}
		return convert(object, entity, nested, cache);
	}
}
