package org.marsik.elshelves.backend.controllers;

import org.marsik.elshelves.api.entities.ListApiModel;
import org.marsik.elshelves.backend.entities.List;
import org.marsik.elshelves.backend.entities.converters.CachingConverter;
import org.marsik.elshelves.backend.entities.converters.EmberToList;
import org.marsik.elshelves.backend.entities.converters.ListToEmber;
import org.marsik.elshelves.backend.services.ListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController("/v1/lists")
public class ListController extends AbstractRestController<List, ListApiModel, ListService> {

    @Autowired
    public ListController(ListService service,
                          ListToEmber dbToRest,
                          EmberToList restToDb) {
        super(ListApiModel.class, service, dbToRest, restToDb);
    }
}
