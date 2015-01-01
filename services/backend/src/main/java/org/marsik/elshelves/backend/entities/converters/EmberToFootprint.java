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
public class EmberToFootprint implements CachingConverter<FootprintApiModel, Footprint, UUID> {
	@Autowired
	EmberToUser emberToUser;

	@Autowired
	EmberToDocument emberToDocument;

	@Override
	public Footprint convert(FootprintApiModel object, int nested, Map<UUID, Object> cache) {
		if (object == null) {
			return null;
		}

		if (cache.containsKey(object.getId())) {
			return (Footprint)cache.get(object.getId());
		}

		Footprint model = new Footprint();

		if (nested > 0
				&& object.getId() != null) {
			cache.put(object.getId(), model);
		}

		return convert(object, model, nested, cache);
	}

	@Override
	public Footprint convert(FootprintApiModel object, Footprint model, int nested, Map<UUID, Object> cache) {
		model.setUuid(object.getId());
		model.setName(object.getName());
		model.setHoles(object.getHoles());
		model.setNpth(object.getNpth());
		model.setPads(object.getPads());
		model.setKicad(object.getKicad());
		model.setOwner(emberToUser.convert(object.getBelongsTo(), 1, cache));

		if (object.getDescribedBy() != null) {
			model.setDescribedBy(new THashSet<Document>());
			for (DocumentApiModel d : object.getDescribedBy()) {
				model.getDescribedBy().add(emberToDocument.convert(d, 1, cache));
			}
		}

		return model;
	}
}
