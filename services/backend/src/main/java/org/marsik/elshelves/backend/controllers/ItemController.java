package org.marsik.elshelves.backend.controllers;

import org.marsik.elshelves.api.ember.EmberModel;
import org.marsik.elshelves.api.entities.ItemApiModel;
import org.marsik.elshelves.api.entities.RequirementApiModel;
import org.marsik.elshelves.backend.controllers.exceptions.EntityNotFound;
import org.marsik.elshelves.backend.controllers.exceptions.OperationNotPermitted;
import org.marsik.elshelves.backend.controllers.exceptions.PermissionDenied;
import org.marsik.elshelves.backend.entities.Item;
import org.marsik.elshelves.backend.entities.User;
import org.marsik.elshelves.backend.security.CurrentUser;
import org.marsik.elshelves.backend.services.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/items")
public class ItemController extends AbstractRestController<Item, ItemApiModel, ItemService> {
	@Autowired
	public ItemController(ItemService service) {
		super(ItemApiModel.class, service);
	}

    @RequestMapping("/{uuid}/import")
    @Transactional
    public EmberModel importFromSchematics(@CurrentUser User currentUser,
                                           @PathVariable("uuid") UUID itemId,
                                           @RequestParam("document") UUID documentId) throws OperationNotPermitted, EntityNotFound, PermissionDenied, IOException {
        List<RequirementApiModel> newRequirements = new ArrayList<>();
        ItemApiModel project = service.importRequirements(itemId, documentId, currentUser, newRequirements);

        EmberModel.Builder<ItemApiModel> builder = new EmberModel.Builder<ItemApiModel>(project);
        sideLoad(project, builder);

        //This would be nice, but Ember has an issue in beta 14.1/15 that breaks the model
        //builder.sideLoad(RequirementApiModel.class, newRequirements);
        return builder.build();
    }
}
