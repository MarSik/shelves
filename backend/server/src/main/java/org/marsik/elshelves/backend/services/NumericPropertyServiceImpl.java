package org.marsik.elshelves.backend.services;

import org.marsik.elshelves.backend.entities.NumericProperty;
import org.marsik.elshelves.backend.entities.User;
import org.marsik.elshelves.backend.repositories.NumericPropertyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NumericPropertyServiceImpl extends AbstractRestService<NumericPropertyRepository, NumericProperty>
        implements NumericPropertyService {
    @Autowired
    public NumericPropertyServiceImpl(NumericPropertyRepository repository,
            UuidGenerator uuidGenerator) {
        super(repository, uuidGenerator);
    }

    @Override
    protected Iterable<NumericProperty> getAllEntities(User currentUser) {
        return getRepository().findByOwner(currentUser);
    }
}
