package org.marsik.elshelves.backend.controllers;

import gnu.trove.map.hash.THashMap;
import gnu.trove.set.hash.THashSet;
import org.joda.time.DateTime;
import org.marsik.elshelves.api.entities.LotApiModel;
import org.marsik.elshelves.api.entities.SourceApiModel;
import org.marsik.elshelves.backend.controllers.exceptions.InvalidRequest;
import org.marsik.elshelves.backend.entities.Source;
import org.marsik.elshelves.backend.entities.Type;
import org.marsik.elshelves.backend.entities.converters.EmberToSource;
import org.marsik.elshelves.backend.entities.converters.EmberToType;
import org.marsik.elshelves.ember.EmberModel;
import org.marsik.elshelves.api.entities.ItemApiModel;
import org.marsik.elshelves.api.entities.LotHistoryApiModel;
import org.marsik.elshelves.api.entities.PartTypeApiModel;
import org.marsik.elshelves.api.entities.PurchaseApiModel;
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
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/v1/items")
public class ItemController extends AbstractReadOnlyRestController<Item, ItemApiModel, ItemService> {

    EmberToItem restToDb;

	@Autowired
	public ItemController(ItemService service,
                          ItemToEmber dbToRest,
                          EmberToItem restToDb) {
		super(ItemApiModel.class, dbToRest, service);
        this.restToDb = restToDb;
	}

    @Autowired
    UuidGenerator uuidGenerator;

    @Autowired
    EmberToSource emberToSource;

    @Autowired
    EmberToType emberToType;

    @Autowired
    LotController lotController;

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

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    @ResponseBody
    @Transactional
    public EmberModel updateLot(@CurrentUser User currentUser,
                                @PathVariable("id") UUID id,
                                @RequestBody @Validated ItemApiModel lot0) throws InvalidRequest, PermissionDenied, EntityNotFound, OperationNotPermitted {
        EmberModel.Builder<? super ItemApiModel> modelBuilder = lotController.performLotAction(currentUser, id, lot0);

        if (modelBuilder == null
                && lot0.getFinished() != null) {
            Item item = getService().get(id, currentUser);
            item.finishOrReopen(lot0.getFinished(), currentUser, uuidGenerator);
            modelBuilder = new EmberModel.Builder<>(dbToRest.convert(item, 1, new THashMap<>()));
        }

        if (modelBuilder == null) {
            throw new InvalidRequest();
        }

        return modelBuilder.build();
    }

    @RequestMapping(method = RequestMethod.POST)
    @Transactional
    public EmberModel create(@CurrentUser User currentUser, @Valid @RequestBody ItemApiModel item0) throws OperationNotPermitted {
        SourceApiModel source0 = item0.getSource();
        PartTypeApiModel type0 = item0.getType();

        Map<UUID, Object> cache = new THashMap<>();

        Item item = getRestToDb().convert(item0, Integer.MAX_VALUE, cache);
        Source source = emberToSource.convert(source0, Integer.MAX_VALUE, cache);
        Type type = emberToType.convert(type0, Integer.MAX_VALUE, cache);

        item = getService().startProject(item, type, source, currentUser);

        ItemApiModel itemApiModel = getDbToRest().convert(item, 1, new THashMap<>());

        EmberModel.Builder<ItemApiModel> builder = new EmberModel.Builder<ItemApiModel>(itemApiModel);
        sideLoad(itemApiModel, builder);
        return builder.build();
    }

    protected EmberToItem getRestToDb() {
        return restToDb;
    }
}
