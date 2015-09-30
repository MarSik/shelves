package org.marsik.elshelves.backend.services;

import org.marsik.elshelves.backend.controllers.exceptions.EntityNotFound;
import org.marsik.elshelves.backend.controllers.exceptions.OperationNotPermitted;
import org.marsik.elshelves.backend.controllers.exceptions.PermissionDenied;
import org.marsik.elshelves.backend.entities.Item;
import org.marsik.elshelves.backend.entities.Requirement;
import org.marsik.elshelves.backend.entities.User;
import org.marsik.elshelves.backend.repositories.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
public class ItemService extends AbstractRestService<ItemRepository, Item> {
	@Autowired
	RequirementService requirementService;

    @Autowired
    DocumentService documentService;

	@Autowired
	public ItemService(ItemRepository repository,
                       UuidGenerator uuidGenerator) {
		super(repository, uuidGenerator);
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

    public Item importRequirements(UUID projectId, UUID document, User currentUser, List<Requirement> newRequirements) throws OperationNotPermitted, EntityNotFound, PermissionDenied, IOException {
        Item item = getSingleEntity(projectId);

        if (item == null) {
            throw new EntityNotFound();
        }

        if (!item.getOwner().equals(currentUser)) {
            throw new PermissionDenied();
        }

        Item dummyProject = new Item();
        dummyProject.setId(projectId);

        List<Requirement> requirements = documentService.analyzeSchematics(document, currentUser);
        for (Requirement r: requirements) {
            r.setItem(dummyProject);
            Requirement newR = requirementService.create(r, currentUser);
            newRequirements.add(newR);
        }

        return item;
    }
}
