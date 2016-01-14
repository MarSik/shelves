package org.marsik.elshelves.backend.services;

import org.marsik.elshelves.backend.entities.Box;
import org.marsik.elshelves.backend.entities.User;
import org.marsik.elshelves.backend.repositories.BoxRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BoxServiceImpl extends AbstractRestService<BoxRepository, Box> implements BoxService {
    @Autowired
    public BoxServiceImpl(BoxRepository boxRepository,
            UuidGenerator uuidGenerator) {
        super(boxRepository, uuidGenerator);
    }

    @Override
    protected Iterable<Box> getAllEntities(User currentUser) {
        return getRepository().findByOwner(currentUser);
    }
}
