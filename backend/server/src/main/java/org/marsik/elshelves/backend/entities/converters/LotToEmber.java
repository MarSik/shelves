package org.marsik.elshelves.backend.entities.converters;

import org.marsik.elshelves.api.entities.LotApiModel;
import org.marsik.elshelves.backend.entities.Lot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;
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

	public LotApiModel convert(String path, Lot object, LotApiModel entity, Map<UUID, Object> cache, Set<String> include) {
		entity.setId(object.getId());
		entity.setCount(object.getCount());

        entity.setExpiration(object.getExpiration());
		entity.setSerials(object.getSerials());
		entity.setAction(object.getStatus());
		entity.setPurchase(purchaseToEmber.convert(path, "purchase", object.getPurchase(), cache, include));
		entity.setUsedBy(requirementToEmber.convert(path, "used-by", object.getUsedBy(), cache, include));
		entity.setLocation(boxToEmber.convert(path, "location", object.getLocation(), cache, include));
		entity.setHistory(lotHistoryToEmber.convert(path, "history", object.getHistory(), cache, include));

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
