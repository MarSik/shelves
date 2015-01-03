package org.marsik.elshelves.backend.entities.converters;

import gnu.trove.set.hash.THashSet;
import org.marsik.elshelves.api.entities.LotApiModel;
import org.marsik.elshelves.api.entities.LotBaseApiModel;
import org.marsik.elshelves.backend.entities.Lot;
import org.marsik.elshelves.backend.entities.LotBase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
public class EmberToLotBase  {
	@Autowired
	EmberToUser emberToUser;

	@Autowired
	EmberToLot emberTolot;

	public LotBase convert(LotBaseApiModel object, LotBase model, int nested, Map<UUID, Object> cache) {
		model.setOwner(emberToUser.convert(object.getBelongsTo(), 1, cache));
		model.setUuid(object.getId());
		model.setCount(object.getCount());
		model.setCreated(object.getCreated());

		return model;
	}
}
