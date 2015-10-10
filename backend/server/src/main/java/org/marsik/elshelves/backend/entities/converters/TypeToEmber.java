package org.marsik.elshelves.backend.entities.converters;

import gnu.trove.set.hash.THashSet;
import org.marsik.elshelves.api.entities.FootprintApiModel;
import org.marsik.elshelves.api.entities.LotApiModel;
import org.marsik.elshelves.api.entities.PartGroupApiModel;
import org.marsik.elshelves.api.entities.PartTypeApiModel;
import org.marsik.elshelves.api.entities.PolymorphicRecord;
import org.marsik.elshelves.backend.entities.Footprint;
import org.marsik.elshelves.backend.entities.Group;
import org.marsik.elshelves.backend.entities.Lot;
import org.marsik.elshelves.backend.entities.Type;
import org.marsik.elshelves.backend.entities.fields.PartCount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
public class TypeToEmber implements CachingConverter<Type, PartTypeApiModel, UUID> {
	@Autowired
	GroupToEmber groupToEmber;

	@Autowired
	FootprintToEmber footprintToEmber;

	@Autowired
	LotToEmber lotToEmber;

	@Autowired
	NamedObjectToEmber namedObjectToEmber;

	@Override
	public PartTypeApiModel convert(Type object, int nested, Map<UUID, Object> cache) {
		if (object == null) {
			return null;
		}

		if (cache.containsKey(object.getId())) {
			return (PartTypeApiModel)cache.get(object.getId());
		}

		PartTypeApiModel model = new PartTypeApiModel();
		if (nested > 0
				&& object.getId() != null) {
			cache.put(object.getId(), model);
		}
		return convert(object, model, nested, cache);
	}

	@Override
	public PartTypeApiModel convert(Type object, PartTypeApiModel model, int nested, Map<UUID, Object> cache) {
		namedObjectToEmber.convert(object, model, nested, cache);

		if (nested == 0) {
			return model;
		}

		model.setDescription(object.getDescription());
		model.setVendor(object.getVendor());
		model.setCustomId(object.getCustomId());
        model.setSerials(object.getSerials());
		model.setManufacturable(object.getManufacturable());

        model.setMinimumCount(object.getMinimumCount());
        model.setBuyMultiple(object.getBuyMultiple());

        PartCount count = object.getAvailable();

        model.setFree(count.free);
        model.setAvailable(count.available);
        model.setTotal(count.total);

        if (object.getFootprints() != null) {
            model.setFootprints(new THashSet<FootprintApiModel>());
            for (Footprint g : object.getFootprints()) {
                model.getFootprints().add(footprintToEmber.convert(g, nested - 1, cache));
            }
        }
        
		if (object.getGroups() != null) {
			model.setGroups(new THashSet<PartGroupApiModel>());
			for (Group g : object.getGroups()) {
				model.getGroups().add(groupToEmber.convert(g, nested - 1, cache));
			}
		}

		if (object.getLots() != null) {
			model.setLots(new THashSet<PolymorphicRecord>());
			for (Lot l: object.getLots()) {
				PolymorphicRecord r = new PolymorphicRecord();
				r.setId(l.getId());
				r.setType(l.getEmberType());
				model.getLots().add(r);
			}
		}

        if (object.getSeeAlso() != null) {
            model.setSeeAlso(new THashSet<PartTypeApiModel>());
            for (Type t: object.getSeeAlso()) {
                model.getSeeAlso().add(convert(t, nested - 1, cache));
            }
			for (Type t: object.getSeeAlsoIncoming()) {
				model.getSeeAlso().add(convert(t, nested - 1, cache));
			}
        }

		return model;
	}
}
