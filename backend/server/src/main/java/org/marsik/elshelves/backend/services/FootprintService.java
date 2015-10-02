package org.marsik.elshelves.backend.services;

import org.marsik.elshelves.backend.entities.Footprint;
import org.marsik.elshelves.backend.entities.User;
import org.marsik.elshelves.backend.repositories.FootprintRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FootprintService extends AbstractRestService<FootprintRepository, Footprint> {
	@Autowired
	public FootprintService(FootprintRepository repository,

							UuidGenerator uuidGenerator) {
		super(repository, uuidGenerator);
	}

    @Override
    protected Iterable<Footprint> getAllEntities(User currentUser) {
        return getRepository().findByOwner(currentUser);
    }
}
