package org.marsik.elshelves.backend.controllers;

import org.marsik.elshelves.api.entities.AuthorizationApiModel;
import org.marsik.elshelves.backend.entities.Authorization;
import org.marsik.elshelves.backend.services.AuthorizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/authorizations")
public class AuthorizationController extends AbstractRestController<Authorization, AuthorizationApiModel, AuthorizationService> {
    @Autowired
    public AuthorizationController(AuthorizationService service) {
        super(AuthorizationApiModel.class, service);
    }
}
