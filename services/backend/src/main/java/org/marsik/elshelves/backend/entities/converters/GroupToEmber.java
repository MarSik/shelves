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
public class GroupToEmber implements CachingConverter<Group, PartGroupApiModel, UUID> {
	@Autowired
	NamedObjectToEmber namedObjectToEmber;

	@Autowired
	TypeToEmber typeToEmber;

    @Autowired
    NumericPropertyToEmber numericPropertyToEmber;

	@Override
	public PartGroupApiModel convert(Group object, int nested, Map<UUID, Object> cache) {
		if (object == null) {
			return null;
		}

		if (cache.containsKey(object.getUuid())) {
			return (PartGroupApiModel)cache.get(object.getUuid());
		}

		PartGroupApiModel model = new PartGroupApiModel();
		if (nested > 0
				&& object.getUuid() != null) {
			cache.put(object.getUuid(), model);
		}

		return convert(object, model, nested, cache);
	}

	@Override
	public PartGroupApiModel convert(Group object, PartGroupApiModel model, int nested, Map<UUID, Object> cache) {
		namedObjectToEmber.convert(object, model, nested, cache);

		if (nested == 0) {
			return model;
		}

		model.setDirectCount(object.getCount());
		model.setNestedCount(object.getNestedCount());

		model.setParent(convert(object.getParent(), nested - 1, cache));

		if (object.getGroups() != null) {
			model.setGroups(new THashSet<PartGroupApiModel>());
			for (Group g : object.getGroups()) {
				model.getGroups().add(convert(g, nested - 1, cache));
			}
		}

		if (object.getTypes() != null) {
			model.setTypes(new THashSet<PartTypeApiModel>());
			for (Type t : object.getTypes()) {
				model.getTypes().add(typeToEmber.convert(t, nested - 1, cache));
			}
		}

        if (object.getShowProperties() != null) {
            model.setShowProperties(new THashSet<NumericPropertyApiModel>());
            for (NumericProperty p: object.getShowProperties()) {
                model.getShowProperties().add(numericPropertyToEmber.convert(p, nested - 1, cache));
            }
        }

		return model;
	}
}
