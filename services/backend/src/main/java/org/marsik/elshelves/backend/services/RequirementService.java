package org.marsik.elshelves.backend.services;

import org.marsik.elshelves.api.entities.RequirementApiModel;
import org.marsik.elshelves.backend.entities.Project;
import org.marsik.elshelves.backend.entities.Requirement;
import org.marsik.elshelves.backend.entities.Type;
import org.marsik.elshelves.backend.entities.converters.CachingConverter;
import org.marsik.elshelves.backend.entities.converters.EmberToRequirement;
import org.marsik.elshelves.backend.entities.converters.RequirementToEmber;
import org.marsik.elshelves.backend.repositories.ProjectRepository;
import org.marsik.elshelves.backend.repositories.RequirementRepository;
import org.marsik.elshelves.backend.repositories.TypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class RequirementService extends AbstractRestService<RequirementRepository, Requirement, RequirementApiModel> {
	@Autowired
	TypeRepository typeRepository;

	@Autowired
	ProjectRepository projectRepository;

	@Autowired
	public RequirementService(RequirementRepository repository,
							  RequirementToEmber dbToRest,
							  EmberToRequirement restToDb,
							  UuidGenerator uuidGenerator) {

		super(repository, dbToRest, restToDb, uuidGenerator);
	}

	@Override
	protected int conversionDepth() {
		return 2;
	}
}
