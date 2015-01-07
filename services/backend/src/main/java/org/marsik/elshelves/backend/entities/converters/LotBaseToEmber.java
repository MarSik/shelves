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
public class LotBaseToEmber  {
	@Autowired
	UserToEmber userToEmber;

	@Autowired
	LotToEmber lotToEmber;

	public LotBaseApiModel convert(LotBase object, LotBaseApiModel model, int nested, Map<UUID, Object> cache) {
		model.setId(object.getUuid());

		if (nested == 0) {
			return model;
		}

		model.setCount(object.getCount());
		model.setCreated(object.getCreated());

		model.setBelongsTo(userToEmber.convert(object.getOwner(), nested - 1, cache));

		if (object.getNext() != null) {
			model.setNext(new THashSet<LotApiModel>());
			for (Lot l : object.getNext()) {
				model.getNext().add(lotToEmber.convert(l, nested - 1, cache));
			}
		}

		return model;
	}
}
