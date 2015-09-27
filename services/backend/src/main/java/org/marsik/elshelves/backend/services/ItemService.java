package org.marsik.elshelves.backend.services;

import gnu.trove.map.hash.THashMap;
import org.marsik.elshelves.api.entities.ItemApiModel;
import org.marsik.elshelves.api.entities.ProjectApiModel;
import org.marsik.elshelves.api.entities.RequirementApiModel;
import org.marsik.elshelves.backend.controllers.exceptions.EntityNotFound;
import org.marsik.elshelves.backend.controllers.exceptions.OperationNotPermitted;
import org.marsik.elshelves.backend.controllers.exceptions.PermissionDenied;
import org.marsik.elshelves.backend.entities.Item;
import org.marsik.elshelves.backend.entities.Requirement;
import org.marsik.elshelves.backend.entities.User;
import org.marsik.elshelves.backend.entities.converters.EmberToItem;
import org.marsik.elshelves.backend.entities.converters.ItemToEmber;
import org.marsik.elshelves.backend.repositories.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class ItemService extends AbstractRestService<ItemRepository, Item, ItemApiModel> {
	@Autowired
	RequirementService requirementService;

    @Autowired
    DocumentService documentService;

	@Autowired
	public ItemService(ItemRepository repository,
                       ItemToEmber dbToRest,
                       EmberToItem restToDb,
                       UuidGenerator uuidGenerator) {
		super(repository, dbToRest, restToDb, uuidGenerator);
	}

    @Override
    protected Iterable<Item> getAllEntities(User currentUser) {
        return getRepository().findByOwner(currentUser);
    }

	@Override
	protected void deleteEntity(Item entity) throws OperationNotPermitted {
		// Delete requirements
		for (Requirement r: entity.getRequires()) {
			requirementService.deleteEntity(r);
		}

		super.deleteEntity(entity);
	}

    public ItemApiModel importRequirements(UUID projectId, UUID document, User currentUser, List<RequirementApiModel> newRequirements) throws OperationNotPermitted, EntityNotFound, PermissionDenied, IOException {
        Item item = getSingleEntity(projectId);

        if (item == null) {
            throw new EntityNotFound();
        }

        if (!item.getOwner().equals(currentUser)) {
            throw new PermissionDenied();
        }

        ProjectApiModel dummyProject = new ProjectApiModel();
        dummyProject.setId(projectId);

        Map<UUID, Object> cache = new THashMap<>();

        List<RequirementApiModel> requirements = documentService.analyzeSchematics(document, currentUser);
        for (RequirementApiModel r: requirements) {
            r.setProject(dummyProject);
            RequirementApiModel newR = requirementService.create(r, currentUser);
            newRequirements.add(newR);
            cache.put(newR.getId(), newR);
        }

        return getDbToRest().convert(item, 1, cache);
    }
}
