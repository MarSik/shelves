package org.marsik.elshelves.backend.entities.converters;

import org.marsik.elshelves.api.entities.BoxApiModel;
import org.marsik.elshelves.backend.entities.Box;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
public class EmberToBox extends AbstractEmberToEntity<BoxApiModel, Box> {
    @Autowired
    EmberToUser emberToUser;

	@Autowired
	EmberToNamedObject emberToNamedObject;

	public EmberToBox() {
		super(Box.class);
	}

	@Override
	public Box convert(BoxApiModel object, Box box, int nested, Map<UUID, Object> cache) {
		emberToNamedObject.convert(object, box, nested, cache);
		box.setParent(convert(object.getParent(), 1, cache));

		return box;
	}
}
