package org.marsik.elshelves.backend.services;

import org.marsik.elshelves.backend.entities.Group;
import org.marsik.elshelves.backend.entities.User;
import org.marsik.elshelves.backend.repositories.GroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GroupServiceImpl extends AbstractRestService<GroupRepository, Group> implements GroupService {
	@Autowired
	public GroupServiceImpl(GroupRepository repository,
			UuidGenerator uuidGenerator) {
		super(repository, uuidGenerator);
	}

    @Override
    protected Iterable<Group> getAllEntities(User currentUser) {
        return getRepository().findByOwner(currentUser);
    }
}
