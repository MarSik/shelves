package org.marsik.elshelves.backend.entities.converters;

import gnu.trove.set.hash.THashSet;
import org.marsik.elshelves.api.entities.FootprintApiModel;
import org.marsik.elshelves.backend.entities.Footprint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Service
@DependsOn("EntityToEmberConversionService")
public class  FootprintToEmber extends AbstractEntityToEmber<Footprint, FootprintApiModel> {
	@Autowired
	NamedObjectToEmber namedObjectToEmber;

	@Autowired
	EntityToEmberConversionService conversionService;

	public FootprintToEmber() {
		super(FootprintApiModel.class);
	}

	@PostConstruct
	void postConstruct() {
		conversionService.register(Footprint.class, getTarget(), this);

	}

	@Override
	public FootprintApiModel convert(String path, Footprint object, FootprintApiModel model, Map<UUID, Object> cache, Set<String> include) {
		namedObjectToEmber.convert(path, object, model, cache, include);

		model.setHoles(object.getHoles());
		model.setPads(object.getPads());
		model.setNpth(object.getNpth());
		model.setKicad(object.getKicad());
        model.setType(object.getType());

        if (object.getSeeAlso() != null) {
            model.setSeeAlso(new THashSet<FootprintApiModel>());
            for (Footprint t: object.getSeeAlso()) {
                model.getSeeAlso().add(convert(path, "see-also", t, cache, include));
            }
			for (Footprint t: object.getSeeAlsoIncoming()) {
				model.getSeeAlso().add(convert(path, "see-also", t, cache, include));
			}
        }

		return model;
	}
}
