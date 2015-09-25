package org.marsik.elshelves.backend.entities.converters;

import gnu.trove.set.hash.THashSet;
import org.marsik.elshelves.api.entities.FootprintApiModel;
import org.marsik.elshelves.api.entities.PartGroupApiModel;
import org.marsik.elshelves.api.entities.PartTypeApiModel;
import org.marsik.elshelves.backend.entities.Footprint;
import org.marsik.elshelves.backend.entities.Group;
import org.marsik.elshelves.backend.entities.Type;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
public class EmberToType implements CachingConverter<PartTypeApiModel, Type, UUID> {
	@Autowired
	EmberToFootprint emberToFootprint;

	@Autowired
	EmberToGroup emberToGroup;

	@Autowired
	EmberToLot emberToLot;

	@Autowired
	EmberToNamedObject emberToNamedObject;

	@Override
	public Type convert(PartTypeApiModel object, int nested, Map<UUID, Object> cache) {
		if (object == null) {
			return null;
		}

		if (cache.containsKey(object.getId())) {
			return (Type)cache.get(object.getId());
		}

		Type model = new Type();
		if (nested > 0
				&& object.getId() != null) {
			cache.put(object.getId(), model);
		}
		return convert(object, model, nested, cache);
	}

	@Override
	public Type convert(PartTypeApiModel object, Type model, int nested, Map<UUID, Object> cache) {
		emberToNamedObject.convert(object, model, nested, cache);
		model.setDescription(object.getDescription());
		model.setVendor(object.getVendor());
		model.setCustomId(object.getCustomId());
        model.setSerials(object.getSerials());
		model.setManufacturable(object.getManufacturable());

        model.setBuyMultiple(object.getBuyMultiple());
        model.setMinimumCount(object.getMinimumCount());

		if (nested == 0) {
			return model;
		}

        if (object.getFootprints() != null) {
            model.setFootprints(new THashSet<Footprint>());
            for (FootprintApiModel g : object.getFootprints()) {
                model.getFootprints().add(emberToFootprint.convert(g, nested - 1, cache));
            }
        }
        
		if (object.getGroups() != null) {
			model.setGroups(new THashSet<Group>());
			for (PartGroupApiModel g : object.getGroups()) {
				model.getGroups().add(emberToGroup.convert(g, nested - 1, cache));
			}
		}

        if (object.getSeeAlso() != null) {
            model.setSeeAlso(new THashSet<Type>());
            for (PartTypeApiModel t: object.getSeeAlso()) {
                model.getSeeAlso().add(convert(t, nested - 1, cache));
            }
        }

		return model;
	}
}
