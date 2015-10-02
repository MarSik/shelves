package org.marsik.elshelves.backend.entities.converters;

import gnu.trove.set.hash.THashSet;
import org.marsik.elshelves.api.entities.LotApiModel;
import org.marsik.elshelves.api.entities.PartTypeApiModel;
import org.marsik.elshelves.api.entities.RequirementApiModel;
import org.marsik.elshelves.backend.entities.Lot;
import org.marsik.elshelves.backend.entities.Requirement;
import org.marsik.elshelves.backend.entities.Type;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
public class RequirementToEmber implements CachingConverter<Requirement, RequirementApiModel, UUID> {
	@Autowired
	TypeToEmber typeToEmber;

	@Autowired
	ItemToEmber itemToEmber;

	@Autowired
	LotToEmber lotToEmber;

	@Override
	public RequirementApiModel convert(Requirement object, int nested, Map<UUID, Object> cache) {
		if (object == null) {
			return null;
		}

		if (cache.containsKey(object.getId())) {
			return (RequirementApiModel)cache.get(object.getId());
		}

		RequirementApiModel model = new RequirementApiModel();
		if (nested > 0
				&& object.getId() != null) {
			cache.put(object.getId(), model);
		}
		return convert(object, model, nested, cache);
	}

	@Override
	public RequirementApiModel convert(Requirement object, RequirementApiModel model, int nested, Map<UUID, Object> cache) {
		model.setId(object.getId());

		if (nested == 0) {
			return model;
		}

		model.setCount(object.getCount());
        model.setName(object.getName());
        model.setSummary(object.getSummary());

		model.setType(new THashSet<PartTypeApiModel>());
		for (Type t: object.getType()) {
			model.getType().add(typeToEmber.convert(t, nested, cache));
		}
		model.setItem(itemToEmber.convert(object.getItem(), nested, cache));

		model.setLots(new THashSet<LotApiModel>());
		for (Lot l: object.getLots()) {
			model.getLots().add(lotToEmber.convert(l, nested, cache));
		}

		return model;
	}
}
