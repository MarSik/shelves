package org.marsik.elshelves.backend.services;

import org.marsik.elshelves.api.entities.PartGroupApiModel;
import org.marsik.elshelves.backend.entities.Group;
import org.marsik.elshelves.backend.entities.User;
import org.marsik.elshelves.backend.entities.converters.EmberToGroup;
import org.marsik.elshelves.backend.entities.converters.GroupToEmber;
import org.marsik.elshelves.backend.repositories.GroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GroupService extends AbstractRestService<GroupRepository, Group, PartGroupApiModel> {
	@Autowired
	public GroupService(GroupRepository repository,
						GroupToEmber dbToRest,
						EmberToGroup restToDb,
						UuidGenerator uuidGenerator) {
		super(repository, dbToRest, restToDb, uuidGenerator);
	}

    @Override
    protected Iterable<Group> getAllEntities(User currentUser) {
        return getRepository().findByOwner(currentUser);
    }
}
