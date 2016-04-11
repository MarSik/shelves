package org.marsik.elshelves.backend.entities.converters;

import gnu.trove.set.hash.THashSet;
import org.marsik.elshelves.api.entities.CodeApiModel;
import org.marsik.elshelves.api.entities.LotApiModel;
import org.marsik.elshelves.api.entities.fields.LotAction;
import org.marsik.elshelves.backend.entities.Code;
import org.marsik.elshelves.backend.entities.IdentifiedEntity;
import org.marsik.elshelves.backend.entities.Lot;
import org.marsik.elshelves.backend.entities.MixedLot;
import org.marsik.elshelves.backend.entities.PurchasedLot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@DependsOn("EntityToEmberConversionService")
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

	@Autowired
	EmberToCode emberToCode;

	@Autowired
	EmberToEntityConversionService conversionService;

	public EmberToLot() {
		super(Lot.class);
	}

	@PostConstruct
	void postConstruct() {
		conversionService.register(LotApiModel.class, this);
	}

	@Override
	public Lot convert(String path, LotApiModel object, Lot model, Map<UUID, Object> cache, Set<String> include) {
		model.setId(object.getId());
		model.setCount(object.getCount());

		if (object.getStatus() == LotAction.SOLDERED) {
			object.setUsed(true);
		} else if (object.getStatus() == LotAction.UNSOLDERED) {
			object.setUsed(false);
			object.setUsedInPast(true);
		} else if (object.getStatus() == LotAction.DESTROYED) {
			object.setValid(false);
		} else if (object.getStatus() == LotAction.FIXED) {
			object.setValid(true);
		} else if (object.getStatus() == LotAction.UNASSIGNED) {
			object.setUsedBy(null);
		}

		model.setUsed(object.getUsed());
		model.setUsedInPast(object.getUsedInPast());

		// Only for old data restore purposes
		model.setCreated(object.getCreated());

		model.setExpiration(object.getExpiration());
		model.setLocation(emberToBox.convert(path, "location", object.getLocation(), cache, include));
		if (model instanceof PurchasedLot) {
			((PurchasedLot) model).setPurchase(emberToPurchase.convert(path, "purchase", object.getPurchase(), cache, include));
		}
		if (model instanceof MixedLot) {
			((MixedLot) model).setParents(object.getParents().stream()
					.map((l) -> convert(path, "parent", l, cache, include))
					.collect(Collectors.toSet()));
		}
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

		if (object.getCodes() != null) {
			model.setCodes(new THashSet<Code>());
			for (CodeApiModel c: object.getCodes()) {
				model.addCode(emberToCode.convert(path, "code", c, cache, include));
			}
		} else {
			model.setCodes(new IdentifiedEntity.UnprovidedSet<>());
		}

		return model;
	}

	@Override
	public Class<? extends Lot> getTarget(LotApiModel object) {
		if (object.getPurchase() != null) {
			return PurchasedLot.class;
		} else if (object.getParents() != null && !object.getParents().isEmpty()) {
			return MixedLot.class;
		}

		return super.getTarget(object);
	}
}
