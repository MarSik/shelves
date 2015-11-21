package org.marsik.elshelves.backend.entities.converters;

import gnu.trove.set.hash.THashSet;
import org.marsik.elshelves.api.entities.DocumentApiModel;
import org.marsik.elshelves.api.entities.PolymorphicRecord;
import org.marsik.elshelves.backend.entities.Document;
import org.marsik.elshelves.backend.entities.IdentifiedEntity;
import org.marsik.elshelves.backend.entities.NamedEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Service
public class EmberToDocument extends AbstractEmberToEntity<DocumentApiModel, Document> {
	@Autowired
	EmberToUser emberToUser;

	public EmberToDocument() {
		super(Document.class);
	}

	@Override
	public Document convert(String path, DocumentApiModel object, Document model, Map<UUID, Object> cache, Set<String> include) {
		model.setId(object.getId());
		model.setName(object.getName());
		model.setContentType(object.getContentType());
		model.setCreated(object.getCreated());
		model.setSize(object.getSize());
        model.setUrl(object.getUrl());

		model.setOwner(emberToUser.convert(path, "owner", object.getBelongsTo(), cache, include));
		if (object.getDescribes() != null) {
			model.setDescribes(new THashSet<NamedEntity>());
			for (final PolymorphicRecord r: object.getDescribes()) {
				NamedEntity entity = new NamedEntity();
				entity.setId(r.getId());
				model.addDescribes(entity);
			}
		} else {
			model.setDescribes(new IdentifiedEntity.UnprovidedSet<>());
		}

		return model;
	}
}
