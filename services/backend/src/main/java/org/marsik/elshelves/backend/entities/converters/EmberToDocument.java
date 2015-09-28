package org.marsik.elshelves.backend.entities.converters;

import gnu.trove.set.hash.THashSet;
import org.marsik.elshelves.api.entities.DocumentApiModel;
import org.marsik.elshelves.api.entities.PolymorphicRecord;
import org.marsik.elshelves.backend.entities.Document;
import org.marsik.elshelves.backend.entities.NamedEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
public class EmberToDocument implements CachingConverter<DocumentApiModel, Document, UUID> {
	@Autowired
	EmberToUser emberToUser;

	@Override
	public Document convert(DocumentApiModel object, int nested, Map<UUID, Object> cache) {
		if (object == null) {
			return null;
		}

		if (cache.containsKey(object.getId())) {
			return (Document)cache.get(object.getId());
		}

		Document model = new Document();
		if (nested > 0
				&& object.getId() != null) {
			cache.put(object.getId(), model);
		}
		return convert(object, model, nested, cache);
	}

	@Override
	public Document convert(DocumentApiModel object, Document model, int nested, Map<UUID, Object> cache) {
		model.setId(object.getId());
		model.setName(object.getName());
		model.setContentType(object.getContentType());
		model.setCreated(object.getCreated());
		model.setSize(object.getSize());
        model.setUrl(object.getUrl());

		if (nested == 0) {
			return model;
		}

		model.setOwner(emberToUser.convert(object.getBelongsTo(), nested, cache));
		if (object.getDescribes() != null) {
			model.setDescribes(new THashSet<NamedEntity>());
			for (final PolymorphicRecord r: object.getDescribes()) {
				NamedEntity entity = new NamedEntity();
				entity.setId(r.getId());
				model.getDescribes().add(entity);
			}
		}

		return model;
	}
}
