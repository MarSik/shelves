package org.marsik.elshelves.backend.services;

import org.marsik.elshelves.api.entities.PartTypeApiModel;
import org.marsik.elshelves.backend.entities.Type;
import org.marsik.elshelves.backend.entities.User;
import org.marsik.elshelves.backend.entities.converters.EmberToType;
import org.marsik.elshelves.backend.entities.converters.TypeToEmber;
import org.marsik.elshelves.backend.repositories.FootprintRepository;
import org.marsik.elshelves.backend.repositories.GroupRepository;
import org.marsik.elshelves.backend.repositories.TypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TypeService extends AbstractRestService<TypeRepository, Type, PartTypeApiModel> {
	@Autowired
	FootprintRepository footprintRepository;

	@Autowired
	GroupRepository groupRepository;

	@Autowired
	public TypeService(TypeRepository repository,
					   TypeToEmber dbToRest,
					   EmberToType restToDb,
					   UuidGenerator uuidGenerator) {
		super(repository, dbToRest, restToDb, uuidGenerator);
	}

    @Override
    protected Iterable<Type> getAllEntities(User currentUser) {
        return getRepository().findByOwner(currentUser);
    }
}
