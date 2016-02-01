package org.marsik.elshelves.backend.controllers;

import org.marsik.elshelves.api.entities.AbstractEntityApiModel;
import org.marsik.elshelves.backend.controllers.exceptions.BaseRestException;
import org.marsik.elshelves.backend.entities.OwnedEntity;
import org.marsik.elshelves.backend.entities.User;
import org.marsik.elshelves.backend.entities.converters.EntityToEmberConversionService;
import org.marsik.elshelves.backend.security.CurrentUser;
import org.marsik.elshelves.backend.services.IdentifiedEntityService;
import org.marsik.elshelves.backend.services.IdentifiedEntityServiceInterface;
import org.marsik.elshelves.ember.EmberModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/v1/id")
public class IdController extends AbstractReadOnlyRestController<OwnedEntity, AbstractEntityApiModel, IdentifiedEntityServiceInterface> {
    @Autowired
    public IdController(EntityToEmberConversionService dbToRest, IdentifiedEntityServiceInterface service) {
        super(AbstractEntityApiModel.class, dbToRest, service);
    }

    @Override
    @RequestMapping
    public ResponseEntity<EmberModel> getAll(@CurrentUser User currentUser, @RequestParam(value = "ids[]", required = false) UUID[] ids, @RequestParam(value = "include", required = false) String include) throws BaseRestException {
        if (ids == null) {
            throw new IllegalArgumentException("Listing all entities is not supported. Provide a list of ids to retrieve.");
        }
        return super.getAll(currentUser, ids, include);
    }
}
