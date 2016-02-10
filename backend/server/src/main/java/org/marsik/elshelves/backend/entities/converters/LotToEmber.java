package org.marsik.elshelves.backend.entities.converters;

import org.marsik.elshelves.api.entities.LotApiModel;
import org.marsik.elshelves.backend.entities.Lot;
import org.marsik.elshelves.backend.entities.MixedLot;
import org.marsik.elshelves.backend.entities.PurchasedLot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@DependsOn("EntityToEmberConversionService")
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

	@Autowired
	EntityToEmberConversionService conversionService;

	public LotToEmber() {
		super(LotApiModel.class);
	}

	@PostConstruct
	void postConstruct() {
		conversionService.register(Lot.class, this);
		conversionService.register(MixedLot.class, this);
		conversionService.register(PurchasedLot.class, this);
	}

	public LotApiModel convert(String path, Lot object, LotApiModel entity, Map<UUID, Object> cache, Set<String> include) {
		entity.setId(object.getId());
		entity.setCount(object.getCount());

		entity.setUsed(object.getUsed());
		entity.setUsedInPast(object.getUsedInPast());

		entity.setType(typeToEmber.convert(path, "type", object.getType(), cache, include));
        entity.setExpiration(object.getExpiration());
		entity.setSerials(object.getSerials());
		entity.setUsedBy(requirementToEmber.convert(path, "used-by", object.getUsedBy(), cache, include));
		entity.setLocation(boxToEmber.convert(path, "location", object.getLocation(), cache, include));
		entity.setHistory(lotHistoryToEmber.convert(path, "history", object.getHistory(), cache, include));

		if (object instanceof PurchasedLot) {
			entity.setPurchase(purchaseToEmber.convert(path, "purchase", ((PurchasedLot) object).getPurchase(), cache, include));
		}

		if (object instanceof MixedLot) {
			entity.setParents(((MixedLot) object).getParents().stream()
					.map((l) -> convert(path, "parent", l, cache, include))
					.collect(Collectors.toSet()));
		}

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
