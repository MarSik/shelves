package org.marsik.elshelves.backend.services;

import org.marsik.elshelves.api.entities.NumericPropertyApiModel;
import org.marsik.elshelves.backend.entities.NumericProperty;
import org.marsik.elshelves.backend.entities.User;
import org.marsik.elshelves.backend.entities.converters.CachingConverter;
import org.marsik.elshelves.backend.entities.converters.EmberToNumericProperty;
import org.marsik.elshelves.backend.entities.converters.NumericPropertyToEmber;
import org.marsik.elshelves.backend.repositories.NumericPropertyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class NumericPropertyService extends AbstractRestService<NumericPropertyRepository, NumericProperty, NumericPropertyApiModel> {
    @Autowired
    public NumericPropertyService(NumericPropertyRepository repository,
                                  NumericPropertyToEmber dbToRest,
                                  EmberToNumericProperty restToDb,
                                  UuidGenerator uuidGenerator) {
        super(repository, dbToRest, restToDb, uuidGenerator);
    }

    @Override
    protected Iterable<NumericProperty> getAllEntities(User currentUser) {
        return getRepository().findByOwner(currentUser);
    }
}
