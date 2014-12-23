package org.marsik.elshelves.backend.services;

import org.marsik.elshelves.api.entities.FootprintApiModel;
import org.marsik.elshelves.backend.entities.Footprint;
import org.marsik.elshelves.backend.entities.converters.EmberToFootprint;
import org.marsik.elshelves.backend.entities.converters.FootprintToEmber;
import org.marsik.elshelves.backend.repositories.FootprintRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FootprintService extends AbstractRestService<FootprintRepository, Footprint, FootprintApiModel> {
	@Autowired
	public FootprintService(FootprintRepository repository,
							FootprintToEmber dbToRest,
							EmberToFootprint restToDb,
							UuidGenerator uuidGenerator) {
		super(repository, dbToRest, restToDb, uuidGenerator);
	}
}
