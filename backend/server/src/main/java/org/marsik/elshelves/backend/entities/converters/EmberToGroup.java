package org.marsik.elshelves.backend.entities.converters;

import gnu.trove.set.hash.THashSet;
import org.marsik.elshelves.api.entities.NumericPropertyApiModel;
import org.marsik.elshelves.api.entities.PartGroupApiModel;
import org.marsik.elshelves.api.entities.PartTypeApiModel;
import org.marsik.elshelves.backend.entities.Group;
import org.marsik.elshelves.backend.entities.IdentifiedEntity;
import org.marsik.elshelves.backend.entities.NumericProperty;
import org.marsik.elshelves.backend.entities.Type;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Service
public class EmberToGroup extends AbstractEmberToEntity<PartGroupApiModel, Group> {
	@Autowired
	EmberToNamedObject emberToNamedObject;

    @Autowired
    EmberToNumericProperty emberToNumericProperty;

    @Autowired
    EmberToType emberToType;

	public EmberToGroup() {
		super(Group.class);
	}

	@Override
	public Group convert(String path, PartGroupApiModel object, Group model, Map<UUID, Object> cache, Set<String> include) {
		emberToNamedObject.convert(path, object, model, cache, include);
		model.setParent(convert(path, "parent", object.getParent(), cache, include));

        if (object.getShowProperties() != null) {
            model.setShowProperties(new THashSet<NumericProperty>());
            for (NumericPropertyApiModel p: object.getShowProperties()) {
                model.addShowProperty(emberToNumericProperty.convert(path, "property", p, cache, include));
            }
        } else {
            model.setShowProperties(new IdentifiedEntity.UnprovidedSet<>());
        }

        if (object.getTypes() != null) {
            model.setTypes(new THashSet<Type>());
            for (PartTypeApiModel t: object.getTypes()) {
                model.addType(emberToType.convert(path, "type", t, cache, include));
            }
        } else {
            model.setTypes(new IdentifiedEntity.UnprovidedSet<>());
        }

        if (object.getGroups() == null) {
            model.setGroups(new IdentifiedEntity.UnprovidedSet<>());
        }

		return model;
	}
}
