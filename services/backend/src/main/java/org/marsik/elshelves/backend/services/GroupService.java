package org.marsik.elshelves.backend.services;

import org.marsik.elshelves.api.entities.PartGroupApiModel;
import org.marsik.elshelves.backend.entities.Box;
import org.marsik.elshelves.backend.entities.Group;
import org.marsik.elshelves.backend.entities.Type;
import org.marsik.elshelves.backend.entities.User;
import org.marsik.elshelves.backend.entities.converters.CachingConverter;
import org.marsik.elshelves.backend.entities.converters.EmberToGroup;
import org.marsik.elshelves.backend.entities.converters.GroupToEmber;
import org.marsik.elshelves.backend.repositories.GroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

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
	protected int conversionDepth() {
		return 2;
	}

	@Override
	protected Group relink(Group entity) {
		if (entity.getParent() != null) {
			Group b = getRepository().getGroupByUuid(entity.getParent().getUuid());
			entity.setParent(b);
		}
		return super.relink(entity);
	}
}
