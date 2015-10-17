package org.marsik.elshelves.backend.entities.converters;

import gnu.trove.set.hash.THashSet;
import org.marsik.elshelves.api.entities.FootprintApiModel;
import org.marsik.elshelves.backend.entities.Footprint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
public class  FootprintToEmber extends AbstractEntityToEmber<Footprint, FootprintApiModel> {
	@Autowired
	NamedObjectToEmber namedObjectToEmber;

	public FootprintToEmber() {
		super(FootprintApiModel.class);
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
			for (Footprint t: object.getSeeAlsoIncoming()) {
				model.getSeeAlso().add(convert(t, nested - 1, cache));
			}
        }

		return model;
	}
}
