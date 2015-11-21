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
import java.util.Set;
import java.util.UUID;

@Service
public class GroupToEmber extends AbstractEntityToEmber<Group, PartGroupApiModel> {
	@Autowired
	NamedObjectToEmber namedObjectToEmber;

	@Autowired
	TypeToEmber typeToEmber;

    @Autowired
    NumericPropertyToEmber numericPropertyToEmber;

	public GroupToEmber() {
		super(PartGroupApiModel.class);
	}

	@Override
	public PartGroupApiModel convert(String path, Group object, PartGroupApiModel model, Map<UUID, Object> cache, Set<String> include) {
		namedObjectToEmber.convert(path, object, model, cache, include);

		model.setDirectCount(object.getCount());
		model.setNestedCount(object.getNestedCount());

		model.setParent(convert(path, "parent", object.getParent(), cache, include));

		if (object.getGroups() != null) {
			model.setGroups(new THashSet<PartGroupApiModel>());
			for (Group g : object.getGroups()) {
				model.getGroups().add(convert(path, "group", g, cache, include));
			}
		}

		if (object.getTypes() != null) {
			model.setTypes(new THashSet<PartTypeApiModel>());
			for (Type t : object.getTypes()) {
				model.getTypes().add(typeToEmber.convert(path, "type", t, cache, include));
			}
		}

        if (object.getShowProperties() != null) {
            model.setShowProperties(new THashSet<NumericPropertyApiModel>());
            for (NumericProperty p: object.getShowProperties()) {
                model.getShowProperties().add(numericPropertyToEmber.convert(path, "property", p, cache, include));
            }
        }

		return model;
	}
}
