package org.marsik.elshelves.backend.controllers;

import org.marsik.elshelves.backend.entities.Box;
import org.marsik.elshelves.backend.entities.converters.BoxToEmber;
import org.marsik.elshelves.backend.entities.converters.CachingConverter;
import org.marsik.elshelves.backend.entities.converters.EmberToBox;
import org.marsik.elshelves.backend.repositories.BoxRepository;
import org.marsik.elshelves.backend.services.UuidGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/boxes")
public class BoxController extends AbstractRestController<Box, org.marsik.elshelves.api.entities.Box> {
    @Autowired
    public BoxController(BoxRepository repository,
                         BoxToEmber dbToRest,
                         EmberToBox restToDb,
                         UuidGenerator uuidGenerator) {
        super(repository, dbToRest, restToDb, uuidGenerator);
    }
}
