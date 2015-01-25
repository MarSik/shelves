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
public class EmberToGroup implements CachingConverter<PartGroupApiModel, Group, UUID> {
	@Autowired
	EmberToNamedObject emberToNamedObject;

    @Autowired
    EmberToNumericProperty emberToNumericProperty;

    @Autowired
    EmberToType emberToType;

	@Override
	public Group convert(PartGroupApiModel object, int nested, Map<UUID, Object> cache) {
		if (object == null) {
			return null;
		}

		if (cache.containsKey(object.getId())) {
			return (Group)cache.get(object.getId());
		}

		Group model = new Group();
		if (nested > 0
				&& object.getId() != null) {
			cache.put(object.getId(), model);
		}

		return convert(object, model, nested, cache);
	}

	@Override
	public Group convert(PartGroupApiModel object, Group model, int nested, Map<UUID, Object> cache) {
		emberToNamedObject.convert(object, model, nested, cache);
		model.setParent(convert(object.getParent(), 1, cache));

        if (object.getShowProperties() != null) {
            model.setShowProperties(new THashSet<NumericProperty>());
            for (NumericPropertyApiModel p: object.getShowProperties()) {
                model.getShowProperties().add(emberToNumericProperty.convert(p, 1, cache));
            }
        }

        if (object.getTypes() != null) {
            model.setTypes(new THashSet<Type>());
            for (PartTypeApiModel t: object.getTypes()) {
                model.getTypes().add(emberToType.convert(t, nested, cache));
            }
        }

		return model;
	}
}
