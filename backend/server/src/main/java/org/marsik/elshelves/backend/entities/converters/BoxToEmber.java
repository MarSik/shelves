package org.marsik.elshelves.backend.entities.converters;

import gnu.trove.set.hash.THashSet;
import org.marsik.elshelves.api.entities.BoxApiModel;
import org.marsik.elshelves.api.entities.LotApiModel;
import org.marsik.elshelves.backend.entities.Box;
import org.marsik.elshelves.backend.entities.Lot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Service
@DependsOn("EntityToEmberConversionService")
public class BoxToEmber extends AbstractEntityToEmber<Box,BoxApiModel> {
    @Autowired
    UserToEmber userToEmber;

	@Autowired
	LotToEmber lotToEmber;

	@Autowired
	NamedObjectToEmber namedObjectToEmber;

	@Autowired
	EntityToEmberConversionService conversionService;

	protected BoxToEmber() {
		super(BoxApiModel.class);
	}

	@PostConstruct
	void postConstruct() {
		conversionService.register(Box.class, this);

	}

	@Override
	public BoxApiModel convert(String path, Box box, BoxApiModel model, Map<UUID, Object> cache, Set<String> include) {
		namedObjectToEmber.convert(path, box, model, cache, include);

		model.setParent(convert(path, "parent", box.getParent(), cache, include));

		if (box.getContains() != null) {
			Set<BoxApiModel> boxes = new THashSet<>();
			for (Box b : box.getContains()) {
				boxes.add(convert(path, "box", b, cache, include));
			}
			model.setBoxes(boxes);
		}

		if (box.getLots() != null) {
			Set<LotApiModel> lots = new THashSet<>();
			for (Lot l: box.getLots()) {
				if (!l.isValid()) {
					continue;
				}

				lots.add(lotToEmber.convert(path, "lot", l, cache, include));
			}
			model.setLots(lots);
		}

		return model;
	}
}
