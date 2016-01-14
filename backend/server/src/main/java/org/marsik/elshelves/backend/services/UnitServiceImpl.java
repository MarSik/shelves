package org.marsik.elshelves.backend.services;

import org.marsik.elshelves.backend.entities.Unit;
import org.marsik.elshelves.backend.entities.User;
import org.marsik.elshelves.backend.repositories.UnitRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UnitServiceImpl extends AbstractRestService<UnitRepository, Unit> implements UnitService {
	@Autowired
	public UnitServiceImpl(UnitRepository repository,
			UuidGenerator uuidGenerator) {
		super(repository, uuidGenerator);
	}

	@Override
	protected Iterable<Unit> getAllEntities(User currentUser) {
		return getRepository().findByOwner(currentUser);
	}
}
