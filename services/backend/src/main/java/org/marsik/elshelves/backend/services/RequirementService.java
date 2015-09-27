package org.marsik.elshelves.backend.services;

import org.marsik.elshelves.api.entities.RequirementApiModel;
import org.marsik.elshelves.backend.entities.Requirement;
import org.marsik.elshelves.backend.entities.User;
import org.marsik.elshelves.backend.entities.converters.EmberToRequirement;
import org.marsik.elshelves.backend.entities.converters.RequirementToEmber;
import org.marsik.elshelves.backend.repositories.ItemRepository;
import org.marsik.elshelves.backend.repositories.RequirementRepository;
import org.marsik.elshelves.backend.repositories.TypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RequirementService extends AbstractRestService<RequirementRepository, Requirement, RequirementApiModel> {
	@Autowired
	TypeRepository typeRepository;

	@Autowired
	ItemRepository itemRepository;

	@Autowired
	public RequirementService(RequirementRepository repository,
							  RequirementToEmber dbToRest,
							  EmberToRequirement restToDb,
							  UuidGenerator uuidGenerator) {

		super(repository, dbToRest, restToDb, uuidGenerator);
	}

    @Override
    protected Iterable<Requirement> getAllEntities(User currentUser) {
        return getRepository().findByProjectOwner(currentUser);
    }
}
