package org.marsik.elshelves.backend.controllers;

import org.marsik.elshelves.api.entities.NumericPropertyApiModel;
import org.marsik.elshelves.backend.entities.NumericProperty;
import org.marsik.elshelves.backend.entities.converters.EmberToNumericProperty;
import org.marsik.elshelves.backend.entities.converters.NumericPropertyToEmber;
import org.marsik.elshelves.backend.services.NumericPropertyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/properties")
public class NumericPropertyController extends AbstractRestController<NumericProperty, NumericPropertyApiModel, NumericPropertyService> {
    @Autowired
    public NumericPropertyController(NumericPropertyService service,
                                     NumericPropertyToEmber dbToRest,
                                     EmberToNumericProperty restToDb) {
        super(NumericPropertyApiModel.class, service, dbToRest, restToDb);
    }
}
