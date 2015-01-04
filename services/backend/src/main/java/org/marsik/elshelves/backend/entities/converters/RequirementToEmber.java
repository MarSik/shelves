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
	ProjectToEmber projectToEmber;

	@Autowired
	LotToEmber lotToEmber;

	@Override
	public RequirementApiModel convert(Requirement object, int nested, Map<UUID, Object> cache) {
		if (object == null) {
			return null;
		}

		if (cache.containsKey(object.getUuid())) {
			return (RequirementApiModel)cache.get(object.getUuid());
		}

		RequirementApiModel model = new RequirementApiModel();
		if (object.getUuid() != null) {
			cache.put(object.getUuid(), model);
		}
		return convert(object, model, nested, cache);
	}

	@Override
	public RequirementApiModel convert(Requirement object, RequirementApiModel model, int nested, Map<UUID, Object> cache) {
		model.setId(object.getUuid());
		model.setCount(object.getCount());

		model.setType(new THashSet<PartTypeApiModel>());
		for (Type t: object.getType()) {
			model.getType().add(typeToEmber.convert(t, nested, cache));
		}
		model.setProject(projectToEmber.convert(object.getProject(), nested, cache));

		model.setLots(new THashSet<LotApiModel>());
		for (Lot l: object.getLots()) {
			model.getLots().add(lotToEmber.convert(l, nested, cache));
		}

		return model;
	}
}
