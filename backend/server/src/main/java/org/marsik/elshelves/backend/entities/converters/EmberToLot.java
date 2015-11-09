package org.marsik.elshelves.backend.entities.converters;

import gnu.trove.set.hash.THashSet;
import org.marsik.elshelves.api.entities.LotApiModel;
import org.marsik.elshelves.backend.entities.IdentifiedEntity;
import org.marsik.elshelves.backend.entities.Lot;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
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
	public Lot convert(LotApiModel object, Lot model, int nested, Map<UUID, Object> cache) {
		model.setId(object.getId());
		model.setCount(object.getCount());
		model.setStatus(object.getStatus());

		// Only for old data restore purposes
		model.setCreated(object.getCreated());

		model.setExpiration(object.getExpiration());
		model.setLocation(emberToBox.convert(object.getLocation(), nested, cache));
		model.setPurchase(emberToPurchase.convert(object.getPurchase(), nested, cache));
		model.setUsedBy(emberToRequirement.convert(object.getUsedBy(), nested, cache));
		model.setSerials(object.getSerials());
		model.setHistory(emberToLotHistory.convert(object.getHistory(), nested, cache));

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
