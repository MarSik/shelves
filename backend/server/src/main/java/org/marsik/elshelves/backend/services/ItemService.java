package org.marsik.elshelves.backend.services;

import org.marsik.elshelves.backend.controllers.exceptions.EntityNotFound;
import org.marsik.elshelves.backend.controllers.exceptions.OperationNotPermitted;
import org.marsik.elshelves.backend.controllers.exceptions.PermissionDenied;
import org.marsik.elshelves.backend.entities.Item;
import org.marsik.elshelves.backend.entities.Requirement;
import org.marsik.elshelves.backend.entities.Type;
import org.marsik.elshelves.backend.entities.User;
import org.marsik.elshelves.backend.repositories.ItemRepository;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public interface ItemService extends AbstractRestServiceIntf<ItemRepository, Item> {
    Item importRequirements(UUID projectId, UUID document, User currentUser, List<Requirement> newRequirements) throws
            OperationNotPermitted, EntityNotFound, PermissionDenied, IOException;

    @Transactional
    Item startProject(Item item,
                      Type type,
                      User currentUser);
}
