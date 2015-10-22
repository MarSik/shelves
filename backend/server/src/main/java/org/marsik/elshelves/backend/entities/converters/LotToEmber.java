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
public class LotToEmber extends AbstractEntityToEmber<Lot, LotApiModel> {
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
	LotHistoryToEmber lotHistoryToEmber;

	public LotToEmber() {
		super(LotApiModel.class);
	}

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
		entity.setHistory(lotHistoryToEmber.convert(object.getHistory(), nested - 1, cache));

		entity.setCanBeAssigned(object.isCanBeAssigned());
		entity.setCanBeUnassigned(object.isCanBeUnassigned());
		entity.setCanBeSoldered(object.isCanBeSoldered());
		entity.setCanBeUnsoldered(object.isCanBeUnsoldered());
		entity.setCanBeSplit(object.isCanBeSplit());
		entity.setCanBeMoved(object.isCanBeMoved());
		entity.setValid(object.isValid());

		return entity;
	}
}
