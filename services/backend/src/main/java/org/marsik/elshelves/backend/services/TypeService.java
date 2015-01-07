package org.marsik.elshelves.backend.services;

import gnu.trove.set.hash.THashSet;
import org.marsik.elshelves.api.entities.PartTypeApiModel;
import org.marsik.elshelves.backend.entities.Footprint;
import org.marsik.elshelves.backend.entities.Group;
import org.marsik.elshelves.backend.entities.Type;
import org.marsik.elshelves.backend.entities.converters.CachingConverter;
import org.marsik.elshelves.backend.entities.converters.EmberToType;
import org.marsik.elshelves.backend.entities.converters.TypeToEmber;
import org.marsik.elshelves.backend.repositories.FootprintRepository;
import org.marsik.elshelves.backend.repositories.GroupRepository;
import org.marsik.elshelves.backend.repositories.TypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.UUID;

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
}
