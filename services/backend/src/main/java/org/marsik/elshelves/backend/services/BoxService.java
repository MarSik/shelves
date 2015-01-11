package org.marsik.elshelves.backend.services;

import org.marsik.elshelves.api.entities.BoxApiModel;
import org.marsik.elshelves.backend.entities.Box;
import org.marsik.elshelves.backend.entities.User;
import org.marsik.elshelves.backend.entities.converters.BoxToEmber;
import org.marsik.elshelves.backend.entities.converters.EmberToBox;
import org.marsik.elshelves.backend.repositories.BoxRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BoxService extends AbstractRestService<BoxRepository, Box, BoxApiModel> {
    @Autowired
    public BoxService(BoxRepository boxRepository,
                      BoxToEmber boxToEmber,
                      EmberToBox emberToBox,
                      UuidGenerator uuidGenerator) {
        super(boxRepository, boxToEmber, emberToBox, uuidGenerator);
    }

    @Override
    protected Iterable<Box> getAllEntities(User currentUser) {
        return getRepository().findByOwner(currentUser);
    }
}
