package org.marsik.elshelves.backend.entities.converters;

import gnu.trove.set.hash.THashSet;
import org.marsik.elshelves.api.entities.LotApiModel;
import org.marsik.elshelves.backend.entities.IdentifiedEntity;
import org.marsik.elshelves.backend.entities.Lot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Service
public class EmberToLot extends AbstractEmberToEntity<LotApiModel, Lot> {
	@Autowired
	EmberToBox emberToBox;

	@Autowired
	EmberToPurchase emberToPurchase;

	@Autowired
	EmberToUser emberToUser;

	@Autowired
	EmberToRequirement emberToRequirement;

	@Autowired
	EmberToLotHistory emberToLotHistory;

	public EmberToLot() {
		super(Lot.class);
	}

	@Override
	public Lot convert(String path, LotApiModel object, Lot model, Map<UUID, Object> cache, Set<String> include) {
		model.setId(object.getId());
		model.setCount(object.getCount());
		model.setStatus(object.getStatus());

		// Only for old data restore purposes
		model.setCreated(object.getCreated());

		model.setExpiration(object.getExpiration());
		model.setLocation(emberToBox.convert(path, "location", object.getLocation(), cache, include));
		model.setPurchase(emberToPurchase.convert(path, "purchase", object.getPurchase(), cache, include));
		model.setUsedBy(emberToRequirement.convert(path, "used-by", object.getUsedBy(), cache, include));
		model.setSerials(object.getSerials());
		model.setHistory(emberToLotHistory.convert(path, "history", object.getHistory(), cache, include));

		if (object.getSerial() != null
				&& !object.getSerial().isEmpty()) {
			if (model.getSerials() == null) {
				model.setSerials(new THashSet<>());
			}
			model.getSerials().add(object.getSerial());
		}

		if (model.getSerials() == null) {
			model.setSerials(new IdentifiedEntity.UnprovidedSet<>());
		}

		return model;
	}
}
