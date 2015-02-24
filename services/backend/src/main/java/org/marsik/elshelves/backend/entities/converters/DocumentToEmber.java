package org.marsik.elshelves.backend.entities.converters;

import gnu.trove.set.hash.THashSet;
import org.marsik.elshelves.api.ember.EmberModelName;
import org.marsik.elshelves.api.entities.AbstractEntityApiModel;
import org.marsik.elshelves.api.entities.DocumentApiModel;
import org.marsik.elshelves.api.entities.PolymorphicRecord;
import org.marsik.elshelves.backend.entities.Document;
import org.marsik.elshelves.backend.entities.NamedEntity;
import org.marsik.elshelves.backend.entities.fields.DefaultEmberModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
public class DocumentToEmber implements CachingConverter<Document, DocumentApiModel, UUID> {
	@Autowired
	UserToEmber userToEmber;

	@Autowired
	NamedObjectToEmber namedObjectToEmber;

	@Override
	public DocumentApiModel convert(Document object, int nested, Map<UUID, Object> cache) {
		if (object == null) {
			return null;
		}

		if (cache.containsKey(object.getUuid())) {
			return (DocumentApiModel)cache.get(object.getUuid());
		}

		DocumentApiModel model = new DocumentApiModel();
		if (nested > 0
				&& object.getUuid() != null) {
			cache.put(object.getUuid(), model);
		}
		return convert(object, model, nested, cache);
	}

	@Override
	public DocumentApiModel convert(Document object, DocumentApiModel model, int nested, Map<UUID, Object> cache) {
		model.setId(object.getUuid());

		if (nested == 0) {
			return model;
		}

		model.setName(object.getName());
		model.setContentType(object.getContentType());
		model.setCreated(object.getCreated());
		model.setSize(object.getSize());
        model.setUrl(object.getUrl());

		model.setBelongsTo(userToEmber.convert(object.getOwner(), nested - 1, cache));

		if (object.getDescribes() != null) {
			model.setDescribes(new THashSet<PolymorphicRecord>());
			for (final NamedEntity n: object.getDescribes()) {
				PolymorphicRecord r = new PolymorphicRecord();
				r.setId(n.getUuid());
				r.setType(n.getEmberType());
				model.getDescribes().add(r);
			}
		}

		return model;
	}
}
