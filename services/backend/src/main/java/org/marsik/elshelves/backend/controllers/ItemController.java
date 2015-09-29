package org.marsik.elshelves.backend.controllers;

import gnu.trove.map.hash.THashMap;
import gnu.trove.set.hash.THashSet;
import org.joda.time.DateTime;
import org.marsik.elshelves.api.ember.EmberModel;
import org.marsik.elshelves.api.entities.ItemApiModel;
import org.marsik.elshelves.api.entities.LotHistoryApiModel;
import org.marsik.elshelves.api.entities.PartTypeApiModel;
import org.marsik.elshelves.api.entities.PurchaseApiModel;
import org.marsik.elshelves.api.entities.RequirementApiModel;
import org.marsik.elshelves.api.entities.TransactionApiModel;
import org.marsik.elshelves.api.entities.fields.LotAction;
import org.marsik.elshelves.backend.controllers.exceptions.EntityNotFound;
import org.marsik.elshelves.backend.controllers.exceptions.OperationNotPermitted;
import org.marsik.elshelves.backend.controllers.exceptions.PermissionDenied;
import org.marsik.elshelves.backend.entities.Item;
import org.marsik.elshelves.backend.entities.Requirement;
import org.marsik.elshelves.backend.entities.User;
import org.marsik.elshelves.backend.entities.converters.EmberToItem;
import org.marsik.elshelves.backend.entities.converters.ItemToEmber;
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
	public ItemController(ItemService service,
                          ItemToEmber dbToRest,
                          EmberToItem restToDb) {
		super(ItemApiModel.class, service, dbToRest, restToDb);
	}

    @Autowired
    UuidGenerator uuidGenerator;

    @RequestMapping("/{uuid}/import")
    @Transactional
    public EmberModel importFromSchematics(@CurrentUser User currentUser,
                                           @PathVariable("uuid") UUID itemId,
                                           @RequestParam("document") UUID documentId) throws OperationNotPermitted, EntityNotFound, PermissionDenied, IOException {
        List<Requirement> newRequirements = new ArrayList<>();
        Item project = service.importRequirements(itemId, documentId, currentUser, newRequirements);

        ItemApiModel itemApiModel = getDbToRest().convert(project, 1, new THashMap<>());

        EmberModel.Builder<ItemApiModel> builder = new EmberModel.Builder<ItemApiModel>(itemApiModel);
        sideLoad(itemApiModel, builder);

        //This would be nice, but Ember has an issue in beta 14.1/15 that breaks the model
        //builder.sideLoad(RequirementApiModel.class, newRequirements);
        return builder.build();
    }

    @Override
    @Transactional
    public EmberModel create(@CurrentUser User currentUser, @Valid @RequestBody ItemApiModel item) throws OperationNotPermitted {
        if (item.getType() == null) {
            item.setType(new PartTypeApiModel());
            item.getType().setName(item.getSerial());
            item.getType().setLots(new THashSet<>());
            item.getType().getLots().add(item);
            item.getType().setManufacturable(true);
            item.getType().setSerials(true);
            item.getType().setId(uuidGenerator.generate());
        }

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

        item.setHistory(new LotHistoryApiModel());
        item.getHistory().setCreated(new DateTime());
        item.getHistory().setAction(LotAction.DELIVERY);
        item.getHistory().setPerformedById(currentUser.getId());

        item.setCount(1L);
        item.setAction(LotAction.DELIVERY);
        item.setId(uuidGenerator.generate());

        return super.create(currentUser, item);
    }
}
