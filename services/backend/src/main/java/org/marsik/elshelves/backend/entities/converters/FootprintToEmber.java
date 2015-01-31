package org.marsik.elshelves.backend.entities.converters;

import gnu.trove.set.hash.THashSet;
import org.marsik.elshelves.api.entities.FootprintApiModel;
import org.marsik.elshelves.backend.entities.Footprint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
public class  FootprintToEmber implements CachingConverter<Footprint, FootprintApiModel, UUID> {
	@Autowired
	NamedObjectToEmber namedObjectToEmber;

	@Override
	public FootprintApiModel convert(Footprint object, int nested, Map<UUID, Object> cache) {
		if (object == null) {
			return null;
		}

		if (cache.containsKey(object.getUuid())) {
			return (FootprintApiModel)cache.get(object.getUuid());
		}

		FootprintApiModel model = new FootprintApiModel();
		if (nested > 0
				&& object.getUuid() != null) {
			cache.put(object.getUuid(), model);
		}
		return convert(object, model, nested, cache);
	}

	@Override
	public FootprintApiModel convert(Footprint object, FootprintApiModel model, int nested, Map<UUID, Object> cache) {
		namedObjectToEmber.convert(object, model, nested, cache);

		if (nested == 0) {
			return model;
		}

		model.setHoles(object.getHoles());
		model.setPads(object.getPads());
		model.setNpth(object.getNpth());
		model.setKicad(object.getKicad());
        model.setType(object.getType());

        if (object.getSeeAlso() != null) {
            model.setSeeAlso(new THashSet<FootprintApiModel>());
            for (Footprint t: object.getSeeAlso()) {
                model.getSeeAlso().add(convert(t, nested - 1, cache));
            }
        }

		return model;
	}
}
