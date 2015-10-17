package org.marsik.elshelves.backend.entities.converters;

import gnu.trove.set.hash.THashSet;
import org.marsik.elshelves.api.entities.LotApiModel;
import org.marsik.elshelves.api.entities.PartTypeApiModel;
import org.marsik.elshelves.api.entities.PolymorphicRecord;
import org.marsik.elshelves.api.entities.RequirementApiModel;
import org.marsik.elshelves.backend.entities.Lot;
import org.marsik.elshelves.backend.entities.Requirement;
import org.marsik.elshelves.backend.entities.Type;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
public class RequirementToEmber extends AbstractEntityToEmber<Requirement, RequirementApiModel> {
	@Autowired
	TypeToEmber typeToEmber;

	@Autowired
	ItemToEmber itemToEmber;

	@Autowired
	LotToEmber lotToEmber;

	public RequirementToEmber() {
		super(RequirementApiModel.class);
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

		model.setLots(new THashSet<PolymorphicRecord>());
		for (Lot l: object.getLots()) {
			PolymorphicRecord r = new PolymorphicRecord();
			r.setId(l.getId());
			r.setType(l.getEmberType());
			model.getLots().add(r);
		}

		return model;
	}
}
