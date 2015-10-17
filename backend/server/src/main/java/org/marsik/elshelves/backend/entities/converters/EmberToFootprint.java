package org.marsik.elshelves.backend.entities.converters;

import gnu.trove.set.hash.THashSet;
import org.marsik.elshelves.api.entities.FootprintApiModel;
import org.marsik.elshelves.backend.entities.Footprint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
public class EmberToFootprint extends AbstractEmberToEntity<FootprintApiModel, Footprint> {
	@Autowired
	EmberToNamedObject emberToNamedObject;

	public EmberToFootprint() {
		super(Footprint.class);
	}

	@Override
	public Footprint convert(FootprintApiModel object, Footprint model, int nested, Map<UUID, Object> cache) {
		emberToNamedObject.convert(object, model, nested, cache);

		model.setHoles(object.getHoles());
		model.setNpth(object.getNpth());
		model.setPads(object.getPads());
		model.setKicad(object.getKicad());
        model.setType(object.getType());

        if (object.getSeeAlso() != null) {
            model.setSeeAlso(new THashSet<Footprint>());
            for (FootprintApiModel t: object.getSeeAlso()) {
                model.addSeeAlso(convert(t, nested - 1, cache));
            }
        }

		return model;
	}
}
