package org.marsik.elshelves.backend.controllers;

import gnu.trove.set.hash.THashSet;
import org.joda.time.DateTime;
import org.marsik.elshelves.api.ember.EmberModel;
import org.marsik.elshelves.api.entities.ItemApiModel;
import org.marsik.elshelves.api.entities.PurchaseApiModel;
import org.marsik.elshelves.api.entities.RequirementApiModel;
import org.marsik.elshelves.api.entities.TransactionApiModel;
import org.marsik.elshelves.backend.controllers.exceptions.EntityNotFound;
import org.marsik.elshelves.backend.controllers.exceptions.OperationNotPermitted;
import org.marsik.elshelves.backend.controllers.exceptions.PermissionDenied;
import org.marsik.elshelves.backend.entities.Item;
import org.marsik.elshelves.backend.entities.User;
import org.marsik.elshelves.backend.security.CurrentUser;
import org.marsik.elshelves.backend.services.ItemService;
import org.marsik.elshelves.backend.services.UuidGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
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

    @Autowired
    UuidGenerator uuidGenerator;

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

    @Override
    @Transactional
    public EmberModel create(@CurrentUser User currentUser, @Valid @RequestBody ItemApiModel item) throws OperationNotPermitted {
        if (item.getPurchase() == null) {
            item.setPurchase(new PurchaseApiModel());
            item.getPurchase().setCount(1L);
            item.getPurchase().setType(item.getType());
            item.getPurchase().setId(uuidGenerator.generate());
            item.getPurchase().setLots(new THashSet<>());
            item.getPurchase().getLots().add(item);
        }

        if (item.getPurchase().getTransaction() == null) {
            item.getPurchase().setTransaction(new TransactionApiModel());
            item.getPurchase().getTransaction().setName(item.getSerial());
            item.getPurchase().getTransaction().setDate(new DateTime());
            item.getPurchase().getTransaction().setId(uuidGenerator.generate());
            item.getPurchase().getTransaction().setItems(new THashSet<>());
            item.getPurchase().getTransaction().getItems().add(item.getPurchase());
        }
        return super.create(currentUser, item);
    }
}
