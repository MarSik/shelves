package org.marsik.elshelves.backend.services;

import org.marsik.elshelves.backend.entities.Footprint;
import org.marsik.elshelves.backend.entities.User;
import org.marsik.elshelves.backend.repositories.FootprintRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FootprintServiceImpl extends AbstractRestService<FootprintRepository, Footprint>
		implements FootprintService {
	@Autowired
	public FootprintServiceImpl(FootprintRepository repository,

			UuidGenerator uuidGenerator) {
		super(repository, uuidGenerator);
	}

    @Override
    protected Iterable<Footprint> getAllEntities(User currentUser) {
        return getRepository().findByOwner(currentUser);
    }
}
