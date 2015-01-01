package org.marsik.elshelves.backend.entities.converters;

import org.marsik.elshelves.api.entities.DocumentApiModel;
import org.marsik.elshelves.backend.entities.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
public class DocumentToEmber implements CachingConverter<Document, DocumentApiModel, UUID> {
	@Autowired
	UserToEmber userToEmber;

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
		model.setName(object.getName());
		model.setContentType(object.getContentType());
		model.setCreated(object.getCreated());
		model.setSize(object.getSize());

		if (nested == 0) {
			return model;
		}

		model.setBelongsTo(userToEmber.convert(object.getOwner(), nested, cache));

		return model;
	}
}
