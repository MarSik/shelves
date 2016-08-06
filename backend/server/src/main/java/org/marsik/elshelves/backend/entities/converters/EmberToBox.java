package org.marsik.elshelves.backend.entities.converters;

import org.marsik.elshelves.api.entities.BoxApiModel;
import org.marsik.elshelves.backend.entities.Box;
import org.marsik.elshelves.backend.entities.IdentifiedEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Service
@DependsOn("EntityToEmberConversionService")
public class EmberToBox extends AbstractEmberToEntity<BoxApiModel, Box> {
    @Autowired
    EmberToUser emberToUser;

	@Autowired
	EmberToNamedObject emberToNamedObject;

	@Autowired
	EmberToEntityConversionService conversionService;

	public EmberToBox() {
		super(Box.class);
	}

	@PostConstruct
	void postConstruct() {
		conversionService.register(BoxApiModel.class, this);
	}

	@Override
	public Box convert(String path, BoxApiModel object, Box box, Map<UUID, Object> cache, Set<String> include) {
		emberToNamedObject.convert(path, object, box, cache, include);
		box.setParent(convert(path, "owner", object.getParent(), cache, include));

		if (object.getLots() == null) {
			box.setLots(new IdentifiedEntity.UnprovidedSet<>());
		}

		if (object.getBoxes() == null) {
			box.setContains(new IdentifiedEntity.UnprovidedSet<>());
		}

		return box;
	}
}
