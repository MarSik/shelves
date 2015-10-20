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
public class EmberToType extends AbstractEmberToEntity<PartTypeApiModel, Type> {
	@Autowired
	EmberToFootprint emberToFootprint;

	@Autowired
	EmberToGroup emberToGroup;

	@Autowired
	EmberToLot emberToLot;

	@Autowired
	EmberToNamedObject emberToNamedObject;

	public EmberToType() {
		super(Type.class);
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
                model.addFootprint(emberToFootprint.convert(g, nested - 1, cache));
            }
        } else {
			model.setFootprints(null);
		}
        
		if (object.getGroups() != null) {
			model.setGroups(new THashSet<Group>());
			for (PartGroupApiModel g : object.getGroups()) {
				model.addGroup(emberToGroup.convert(g, nested - 1, cache));
			}
		} else {
			model.setGroups(null);
		}

        if (object.getSeeAlso() != null) {
            model.setSeeAlso(new THashSet<Type>());
            for (PartTypeApiModel t: object.getSeeAlso()) {
                model.addSeeAlso(convert(t, nested - 1, cache));
            }
        } else {
			model.setSeeAlso(null);
			model.setSeeAlsoIncoming(null);
		}

		return model;
	}
}
