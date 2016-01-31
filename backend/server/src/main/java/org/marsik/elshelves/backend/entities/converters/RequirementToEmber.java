package org.marsik.elshelves.backend.entities.converters;

import gnu.trove.set.hash.THashSet;
import org.marsik.elshelves.api.entities.LotApiModel;
import org.marsik.elshelves.api.entities.PartTypeApiModel;
import org.marsik.elshelves.api.entities.RequirementApiModel;
import org.marsik.elshelves.backend.entities.Lot;
import org.marsik.elshelves.backend.entities.Requirement;
import org.marsik.elshelves.backend.entities.Type;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Service
@DependsOn("EntityToEmberConversionService")
public class RequirementToEmber extends AbstractEntityToEmber<Requirement, RequirementApiModel> {
	@Autowired
	TypeToEmber typeToEmber;

	@Autowired
	ItemToEmber itemToEmber;

	@Autowired
	LotToEmber lotToEmber;

	@Autowired
	EntityToEmberConversionService conversionService;

	public RequirementToEmber() {
		super(RequirementApiModel.class);
	}

	@PostConstruct
	void postConstruct() {
		conversionService.register(Requirement.class, this);

	}

	@Override
	public RequirementApiModel convert(String path, Requirement object, RequirementApiModel model, Map<UUID, Object> cache, Set<String> include) {
		model.setCount(object.getCount());
        model.setName(object.getName());
        model.setSummary(object.getSummary());

		model.setType(new THashSet<PartTypeApiModel>());
		for (Type t: object.getType()) {
			model.getType().add(typeToEmber.convert(path, "type", t, cache, include));
		}
		model.setItem(itemToEmber.convert(path, "item", object.getItem(), cache, include));

		if (object.getLots() != null) {
			model.setLots(new THashSet<>());
			for (Lot l: object.getLots()) {
				LotApiModel r = conversionService.converter(l, LotApiModel.class)
						.convert(path, "lot", l, cache, include);
				model.getLots().add(r);
			}
		}

		return model;
	}
}
