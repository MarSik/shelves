package org.marsik.elshelves.backend.entities.converters;

import gnu.trove.set.hash.THashSet;
import org.marsik.elshelves.api.entities.AbstractNamedEntityApiModel;
import org.marsik.elshelves.api.entities.DocumentApiModel;
import org.marsik.elshelves.backend.entities.Document;
import org.marsik.elshelves.backend.entities.NamedObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
public class NamedObjectToEmber {
	@Autowired
	UserToEmber userToEmber;

	@Autowired
	DocumentToEmber documentToEmber;

	public AbstractNamedEntityApiModel convert(NamedObject object, AbstractNamedEntityApiModel model, int nested, Map<UUID, Object> cache) {
		model.setId(object.getUuid());

		if (nested == 0) {
			return model;
		}

		model.setName(object.getName());
		model.setSummary(object.getSummary());
		model.setDescription(object.getDescription());

		model.setBelongsTo(userToEmber.convert(object.getOwner(), nested - 1, cache));

		if (object.getDescribedBy() != null) {
			model.setDescribedBy(new THashSet<DocumentApiModel>());

			for (Document d: object.getDescribedBy()) {
				model.getDescribedBy().add(documentToEmber.convert(d, nested - 1, cache));
			}
		}

		return model;
	}
}
