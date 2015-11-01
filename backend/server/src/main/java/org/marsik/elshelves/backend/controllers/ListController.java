package org.marsik.elshelves.backend.controllers;

import gnu.trove.map.hash.THashMap;
import org.marsik.elshelves.api.entities.AbstractEntityApiModel;
import org.marsik.elshelves.api.entities.ListApiModel;
import org.marsik.elshelves.backend.controllers.exceptions.BaseRestException;
import org.marsik.elshelves.backend.controllers.exceptions.EntityNotFound;
import org.marsik.elshelves.backend.controllers.exceptions.PermissionDenied;
import org.marsik.elshelves.backend.entities.IdentifiedEntity;
import org.marsik.elshelves.backend.entities.List;
import org.marsik.elshelves.backend.entities.OwnedEntity;
import org.marsik.elshelves.backend.entities.User;
import org.marsik.elshelves.backend.entities.converters.CachingConverter;
import org.marsik.elshelves.backend.entities.converters.EmberToList;
import org.marsik.elshelves.backend.entities.converters.ListToEmber;
import org.marsik.elshelves.backend.repositories.IdentifiedEntityRepository;
import org.marsik.elshelves.backend.repositories.OwnedEntityRepository;
import org.marsik.elshelves.backend.security.CurrentUser;
import org.marsik.elshelves.backend.services.ListService;
import org.marsik.elshelves.ember.EmberModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.transaction.Transactional;
import java.util.UUID;

@RequestMapping("/v1/lists")
@RestController
public class ListController extends AbstractRestController<List, ListApiModel, ListService> {

    @Autowired
    OwnedEntityRepository ownedEntityRepository;

    @Autowired
    public ListController(ListService service,
                          ListToEmber dbToRest,
                          EmberToList restToDb) {
        super(ListApiModel.class, service, dbToRest, restToDb);
    }

    @RequestMapping(value = "/{list}/items", method = RequestMethod.POST)
    @Transactional
    public EmberModel addItemToList(@CurrentUser User currentUser,
                                    @PathVariable("list") UUID uuid,
                                    @RequestBody AbstractEntityApiModel item0) throws BaseRestException {
        List list = getService().get(uuid, currentUser);

        OwnedEntity item = ownedEntityRepository.findById(item0.getId());
        if (item == null) {
            throw new EntityNotFound();
        }

        if (!item.getOwner().equals(currentUser)) {
            throw new PermissionDenied();
        }

        list.addItem(item);

        EmberModel.Builder<ListApiModel> model = new EmberModel.Builder<ListApiModel>(getDbToRest().convert(list, 1, new THashMap<>()));
        return model.build();
    }

    @RequestMapping(value = "/{list}/items/{item}", method = RequestMethod.DELETE)
    @Transactional
    public EmberModel deleteItemFromList(@CurrentUser User currentUser,
                                         @PathVariable("list") UUID uuid,
                                         @PathVariable("item") UUID item0) throws BaseRestException {
        List list = getService().get(uuid, currentUser);

        IdentifiedEntity item = new IdentifiedEntity();
        item.setId(item0);
        list.removeItem(item);

        EmberModel.Builder<ListApiModel> model = new EmberModel.Builder<ListApiModel>(getDbToRest().convert(list, 1, new THashMap<>()));
        return model.build();
    }
}
