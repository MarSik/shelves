package org.marsik.elshelves.backend.services;

import org.marsik.elshelves.api.entities.NumericPropertyApiModel;
import org.marsik.elshelves.backend.entities.NumericProperty;
import org.marsik.elshelves.backend.entities.User;
import org.marsik.elshelves.backend.entities.converters.EmberToNumericProperty;
import org.marsik.elshelves.backend.entities.converters.NumericPropertyToEmber;
import org.marsik.elshelves.backend.repositories.NumericPropertyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NumericPropertyService extends AbstractRestService<NumericPropertyRepository, NumericProperty> {
    @Autowired
    public NumericPropertyService(NumericPropertyRepository repository,
                                  UuidGenerator uuidGenerator) {
        super(repository, uuidGenerator);
    }

    @Override
    protected Iterable<NumericProperty> getAllEntities(User currentUser) {
        return getRepository().findByOwner(currentUser);
    }
}
