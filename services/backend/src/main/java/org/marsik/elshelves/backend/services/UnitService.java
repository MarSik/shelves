package org.marsik.elshelves.backend.services;

import org.marsik.elshelves.api.entities.UnitApiModel;
import org.marsik.elshelves.backend.entities.Unit;
import org.marsik.elshelves.backend.entities.User;
import org.marsik.elshelves.backend.entities.converters.EmberToUnit;
import org.marsik.elshelves.backend.entities.converters.UnitToEmber;
import org.marsik.elshelves.backend.repositories.UnitRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UnitService extends AbstractRestService<UnitRepository, Unit, UnitApiModel> {
	@Autowired
	public UnitService(UnitRepository repository,
					   UnitToEmber dbToRest,
					   EmberToUnit restToDb,
					   UuidGenerator uuidGenerator) {
		super(repository, dbToRest, restToDb, uuidGenerator);
	}

	@Override
	protected Iterable<Unit> getAllEntities(User currentUser) {
		return getRepository().findByOwner(currentUser);
	}
}
