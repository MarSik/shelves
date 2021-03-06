package org.marsik.elshelves.backend.controllers;

import gnu.trove.map.hash.THashMap;
import org.marsik.elshelves.api.entities.ItemApiModel;
import org.marsik.elshelves.api.entities.PartTypeApiModel;
import org.marsik.elshelves.api.entities.SourceApiModel;
import org.marsik.elshelves.backend.controllers.exceptions.BaseRestException;
import org.marsik.elshelves.backend.controllers.exceptions.EntityNotFound;
import org.marsik.elshelves.backend.controllers.exceptions.InvalidRequest;
import org.marsik.elshelves.backend.controllers.exceptions.OperationNotPermitted;
import org.marsik.elshelves.backend.controllers.exceptions.PermissionDenied;
import org.marsik.elshelves.backend.dtos.LotSplitResult;
import org.marsik.elshelves.backend.entities.Item;
import org.marsik.elshelves.backend.entities.Lot;
import org.marsik.elshelves.backend.entities.Requirement;
import org.marsik.elshelves.backend.entities.Source;
import org.marsik.elshelves.backend.entities.Type;
import org.marsik.elshelves.backend.entities.User;
import org.marsik.elshelves.backend.entities.converters.EmberToItem;
import org.marsik.elshelves.backend.entities.converters.EmberToSource;
import org.marsik.elshelves.backend.entities.converters.EmberToType;
import org.marsik.elshelves.backend.entities.converters.EntityToEmberConversionService;
import org.marsik.elshelves.backend.entities.converters.ItemToEmber;
import org.marsik.elshelves.backend.repositories.ItemRepository;
import org.marsik.elshelves.backend.security.CurrentUser;
import org.marsik.elshelves.backend.services.ItemService;
import org.marsik.elshelves.backend.services.LotService;
import org.marsik.elshelves.backend.services.UuidGenerator;
import org.marsik.elshelves.ember.EmberModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.IOException;
import java.net.URI;
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
    LotService lotService;

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    EntityToEmberConversionService conversionService;

    @RequestMapping("/{uuid}/import")
    @Transactional
    public EmberModel importFromSchematics(@CurrentUser User currentUser,
                                           @PathVariable("uuid") UUID itemId,
                                           @RequestParam("document") UUID documentId) throws OperationNotPermitted, EntityNotFound, PermissionDenied, IOException {
        List<Requirement> newRequirements = new ArrayList<>();
        Item project = service.importRequirements(itemId, documentId, currentUser, newRequirements);

        ItemApiModel itemApiModel = getDbToRest().convert(project, new THashMap<>());

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
                                @RequestBody @Validated ItemApiModel item0) throws InvalidRequest, PermissionDenied, EntityNotFound, OperationNotPermitted {
        Item item = getRestToDb().convert(item0, new THashMap<>());
        LotSplitResult result = lotService.update(itemRepository.findById(id), item, currentUser);

        Map<UUID, Object> cache = new THashMap<>();

        EmberModel.Builder<ItemApiModel> modelBuilder = new EmberModel.Builder<>(
                conversionService.convert(result.getRequested(), cache));
        for (Lot l: result.getOthers()) {
            modelBuilder.sideLoad(
                    conversionService.convert(l, cache));
        }

        return modelBuilder.build();
    }

    @RequestMapping(method = RequestMethod.POST)
    @Transactional
    public ResponseEntity<EmberModel> create(HttpServletRequest request, @CurrentUser User currentUser, @Valid @RequestBody ItemApiModel item0) throws BaseRestException {
        if (item0.getPrevious() != null) {
            return ResponseEntity.ok(updateLot(currentUser, item0.getPrevious().getId(), item0));
        }

        PartTypeApiModel type0 = item0.getType();

        Map<UUID, Object> cache = new THashMap<>();

        Item item = getRestToDb().convert(item0, cache);
        Type type = emberToType.convert(type0, cache);

        item = getService().startProject(item, type, currentUser);
        getService().flush();

        ItemApiModel itemApiModel = getDbToRest().convert(item, new THashMap<>());

        EmberModel.Builder<ItemApiModel> builder = new EmberModel.Builder<ItemApiModel>(itemApiModel);
        sideLoad(itemApiModel, builder);

        return ResponseEntity
                .created(URI.create(request.getRequestURL() + "/" + item.getId().toString()))
                .eTag(item.getVersion().toString())
                .lastModified(item.getLastModified().getMillis())
                .body(builder.build());
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    @Transactional
    public Map<Object, Object> deleteOne(@CurrentUser User currentUser,
            @PathVariable("id") UUID uuid) throws BaseRestException {
        service.delete(uuid, currentUser);
        return new THashMap<>();
    }

    protected EmberToItem getRestToDb() {
        return restToDb;
    }
}
