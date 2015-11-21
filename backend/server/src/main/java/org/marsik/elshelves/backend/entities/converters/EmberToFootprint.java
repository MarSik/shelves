package org.marsik.elshelves.backend.entities.converters;

import gnu.trove.set.hash.THashSet;
import org.marsik.elshelves.api.entities.FootprintApiModel;
import org.marsik.elshelves.backend.entities.Footprint;
import org.marsik.elshelves.backend.entities.IdentifiedEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Service
public class EmberToFootprint extends AbstractEmberToEntity<FootprintApiModel, Footprint> {
	@Autowired
	EmberToNamedObject emberToNamedObject;

	public EmberToFootprint() {
		super(Footprint.class);
	}

	@Override
	public Footprint convert(String path, FootprintApiModel object, Footprint model, Map<UUID, Object> cache, Set<String> include) {
		emberToNamedObject.convert(path, object, model, cache, include);

		model.setHoles(object.getHoles());
		model.setNpth(object.getNpth());
		model.setPads(object.getPads());
		model.setKicad(object.getKicad());
        model.setType(object.getType());

        if (object.getSeeAlso() != null) {
            model.setSeeAlso(new THashSet<Footprint>());
            for (FootprintApiModel t: object.getSeeAlso()) {
                model.addSeeAlso(convert(path, "see-also", t, cache, include));
            }
        } else {
			model.setSeeAlso(new IdentifiedEntity.UnprovidedSet<>());
			model.setSeeAlsoIncoming(new IdentifiedEntity.UnprovidedSet<>());
		}

		return model;
	}
}
