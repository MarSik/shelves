package org.marsik.elshelves.backend.entities.converters;

import gnu.trove.set.hash.THashSet;
import org.marsik.elshelves.api.entities.DocumentApiModel;
import org.marsik.elshelves.api.entities.FootprintApiModel;
import org.marsik.elshelves.backend.entities.Document;
import org.marsik.elshelves.backend.entities.Footprint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
public class FootprintToEmber implements CachingConverter<Footprint, FootprintApiModel, UUID> {
	@Autowired
	UserToEmber userToEmber;

	@Autowired
	DocumentToEmber documentToEmber;

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
		model.setName(object.getName());
		model.setId(object.getUuid());
		model.setHoles(object.getHoles());
		model.setPads(object.getPads());
		model.setNpth(object.getNpth());
		model.setKicad(object.getKicad());

		if (nested == 0) {
			return model;
		}

		model.setBelongsTo(userToEmber.convert(object.getOwner(), nested - 1, cache));


		if (object.getDescribedBy() != null) {
			model.setDescribedBy(new THashSet<DocumentApiModel>());
			for (Document d : object.getDescribedBy()) {
				model.getDescribedBy().add(documentToEmber.convert(d, nested - 1, cache));
			}
		}

		return model;
	}
}
