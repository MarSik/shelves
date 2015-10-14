package org.marsik.elshelves.backend.services;

import org.marsik.elshelves.backend.entities.Requirement;
import org.marsik.elshelves.backend.entities.User;
import org.marsik.elshelves.backend.repositories.ItemRepository;
import org.marsik.elshelves.backend.repositories.RequirementRepository;
import org.marsik.elshelves.backend.repositories.TypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RequirementService extends AbstractRestService<RequirementRepository, Requirement> {
	@Autowired
	TypeRepository typeRepository;

	@Autowired
	ItemRepository itemRepository;

	@Autowired
	public RequirementService(RequirementRepository repository,
							  UuidGenerator uuidGenerator) {

		super(repository, uuidGenerator);
	}

    @Override
    protected Iterable<Requirement> getAllEntities(User currentUser) {
        return getRepository().findByItemOwner(currentUser);
    }
}
