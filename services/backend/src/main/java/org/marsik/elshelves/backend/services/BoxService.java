package org.marsik.elshelves.backend.services;

import gnu.trove.map.hash.THashMap;
import org.marsik.elshelves.api.entities.BoxApiModel;
import org.marsik.elshelves.backend.controllers.exceptions.EntityNotFound;
import org.marsik.elshelves.backend.controllers.exceptions.PermissionDenied;
import org.marsik.elshelves.backend.entities.Box;
import org.marsik.elshelves.backend.entities.User;
import org.marsik.elshelves.backend.entities.converters.BoxToEmber;
import org.marsik.elshelves.backend.entities.converters.EmberToBox;
import org.marsik.elshelves.backend.repositories.BoxRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class BoxService extends AbstractRestService<BoxRepository, Box, BoxApiModel> {
    @Autowired
    public BoxService(BoxRepository boxRepository,
                      BoxToEmber boxToEmber,
                      EmberToBox emberToBox,
                      UuidGenerator uuidGenerator) {
        super(boxRepository, boxToEmber, emberToBox, uuidGenerator);
    }

	@Override
	protected int conversionDepth() {
		return 2;
	}

	@Override
    protected Iterable<Box> getAllEntities(User currentUser) {
        return currentUser.getBoxes();
    }

    @Override
    protected Box getSingleEntity(UUID uuid) {
        return getRepository().getBoxByUuid(uuid);
    }

	public Iterable<BoxApiModel> getNestedBoxes(UUID parent, User currentUser) throws EntityNotFound, PermissionDenied {
		Box box = getSingleEntity(parent);

		if (box == null) {
			throw new EntityNotFound();
		}

		if (!box.getOwner().equals(currentUser)) {
			throw new PermissionDenied();
		}

		Map<UUID, Object> cache = new THashMap<>();
		List<BoxApiModel> boxes = new ArrayList<>();

		for (Box b: box.getContains()) {
			boxes.add(getDbToRest().convert(b, 2, cache));
		}

		return boxes;
	}

	@Override
	protected Box relink(Box entity) {
		if (entity.getParent() != null) {
			Box b = getRepository().getBoxByUuid(entity.getParent().getUuid());
			entity.setParent(b);
		}
		return super.relink(entity);
	}
}
