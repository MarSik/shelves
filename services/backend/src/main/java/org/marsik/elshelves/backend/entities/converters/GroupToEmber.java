package org.marsik.elshelves.backend.entities.converters;

import org.marsik.elshelves.api.entities.PartGroupApiModel;
import org.marsik.elshelves.api.entities.PartTypeApiModel;
import org.marsik.elshelves.backend.entities.Group;
import org.marsik.elshelves.backend.entities.Type;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;

@Service
public class GroupToEmber implements CachingConverter<Group, PartGroupApiModel, UUID> {
	@Autowired
	UserToEmber userToEmber;

	@Autowired
	TypeToEmber typeToEmber;

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
		model.setName(object.getName());
		model.setId(object.getUuid());

		if (nested == 0) {
			return model;
		}

		model.setBelongsTo(userToEmber.convert(object.getOwner(), nested - 1, cache));
		model.setParent(convert(object.getParent(), nested - 1, cache));

		if (object.getGroups() != null) {
			model.setGroups(new ArrayList<PartGroupApiModel>());
			for (Group g : object.getGroups()) {
				model.getGroups().add(convert(g, nested - 1, cache));
			}
		}

		if (object.getTypes() != null) {
			model.setTypes(new ArrayList<PartTypeApiModel>());
			for (Type t : object.getTypes()) {
				model.getTypes().add(typeToEmber.convert(t, nested - 1, cache));
			}
		}

		return model;
	}
}
