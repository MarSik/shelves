package org.marsik.elshelves.backend.entities.converters;

import gnu.trove.set.hash.THashSet;
import org.marsik.elshelves.api.entities.NumericPropertyApiModel;
import org.marsik.elshelves.api.entities.PartGroupApiModel;
import org.marsik.elshelves.api.entities.PartTypeApiModel;
import org.marsik.elshelves.backend.entities.Group;
import org.marsik.elshelves.backend.entities.NumericProperty;
import org.marsik.elshelves.backend.entities.Type;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
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
	public Group convert(PartGroupApiModel object, Group model, int nested, Map<UUID, Object> cache) {
		emberToNamedObject.convert(object, model, nested, cache);
		model.setParent(convert(object.getParent(), 1, cache));

        if (object.getShowProperties() != null) {
            model.setShowProperties(new THashSet<NumericProperty>());
            for (NumericPropertyApiModel p: object.getShowProperties()) {
                model.addShowProperty(emberToNumericProperty.convert(p, 1, cache));
            }
        } else {
            model.setShowProperties(null);
        }

        if (object.getTypes() != null) {
            model.setTypes(new THashSet<Type>());
            for (PartTypeApiModel t: object.getTypes()) {
                model.addType(emberToType.convert(t, nested, cache));
            }
        } else {
            model.setTypes(null);
        }

        model.setGroups(null);

		return model;
	}
}
