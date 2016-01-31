package org.marsik.elshelves.backend.entities.converters;

import gnu.trove.set.hash.THashSet;
import org.marsik.elshelves.api.entities.FootprintApiModel;
import org.marsik.elshelves.api.entities.PartGroupApiModel;
import org.marsik.elshelves.api.entities.PartTypeApiModel;
import org.marsik.elshelves.backend.entities.Footprint;
import org.marsik.elshelves.backend.entities.Group;
import org.marsik.elshelves.backend.entities.IdentifiedEntity;
import org.marsik.elshelves.backend.entities.Type;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Service
@DependsOn("EntityToEmberConversionService")
public class EmberToType extends AbstractEmberToEntity<PartTypeApiModel, Type> {
	@Autowired
	EmberToFootprint emberToFootprint;

	@Autowired
	EmberToGroup emberToGroup;

	@Autowired
	EmberToLot emberToLot;

	@Autowired
	EmberToNamedObject emberToNamedObject;

	@Autowired
	EmberToEntityConversionService conversionService;

	public EmberToType() {
		super(Type.class);
	}

	@PostConstruct
	void postConstruct() {
		conversionService.register(PartTypeApiModel.class, this);

	}

	@Override
	public Type convert(String path, PartTypeApiModel object, Type model, Map<UUID, Object> cache, Set<String> include) {
		emberToNamedObject.convert(path, object, model, cache, include);
		model.setDescription(object.getDescription());
		model.setVendor(object.getVendor());
		model.setCustomId(object.getCustomId());
        model.setSerials(object.getSerials());
		model.setManufacturable(object.getManufacturable());

        model.setBuyMultiple(object.getBuyMultiple());
        model.setMinimumCount(object.getMinimumCount());

        if (object.getFootprints() != null) {
            model.setFootprints(new THashSet<Footprint>());
            for (FootprintApiModel g : object.getFootprints()) {
                model.addFootprint(emberToFootprint.convert(path, "footprint", g, cache, include));
            }
        } else {
			model.setFootprints(new IdentifiedEntity.UnprovidedSet<>());
		}
        
		if (object.getGroups() != null) {
			model.setGroups(new THashSet<Group>());
			for (PartGroupApiModel g : object.getGroups()) {
				model.addGroup(emberToGroup.convert(path, "group", g, cache, include));
			}
		} else {
			model.setGroups(new IdentifiedEntity.UnprovidedSet<>());
		}

        if (object.getSeeAlso() != null) {
            model.setSeeAlso(new THashSet<Type>());
            for (PartTypeApiModel t: object.getSeeAlso()) {
                model.addSeeAlso(convert(path, "see-also", t, cache, include));
            }
        } else {
			model.setSeeAlso(new IdentifiedEntity.UnprovidedSet<>());
			model.setSeeAlsoIncoming(new IdentifiedEntity.UnprovidedSet<>());
		}

		return model;
	}
}
