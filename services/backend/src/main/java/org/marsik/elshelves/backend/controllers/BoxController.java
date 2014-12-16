package org.marsik.elshelves.backend.controllers;

import org.marsik.elshelves.api.ember.EmberModel;
import org.marsik.elshelves.api.entities.Lot;
import org.marsik.elshelves.backend.entities.Box;
import org.marsik.elshelves.backend.entities.User;
import org.marsik.elshelves.backend.entities.converters.BoxToEmber;
import org.marsik.elshelves.backend.entities.converters.CachingConverter;
import org.marsik.elshelves.backend.entities.converters.EmberToBox;
import org.marsik.elshelves.backend.repositories.BoxRepository;
import org.marsik.elshelves.backend.services.BoxService;
import org.marsik.elshelves.backend.services.UuidGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/boxes")
public class BoxController extends AbstractRestController<Box, org.marsik.elshelves.api.entities.Box> {
    @Autowired
    public BoxController(BoxService boxService) {
        super(org.marsik.elshelves.api.entities.Box.class,
              boxService);
    }

    @Override
    protected void sideLoad(org.marsik.elshelves.api.entities.Box dto, EmberModel.Builder<org.marsik.elshelves.api.entities.Box> builder) {
        if (dto.getBoxes() != null) {
            builder.sideLoad(org.marsik.elshelves.api.entities.Box.class, dto.getBoxes());
        }

        if (dto.getLots() != null) {
            builder.sideLoad(Lot.class, dto.getLots());
        }

        if (dto.getParent() != null) {
            builder.sideLoad(dto.getParent());
        }

        if (dto.getBelongsTo() != null) {
            builder.sideLoad(dto.getBelongsTo());
        }
    }
}
