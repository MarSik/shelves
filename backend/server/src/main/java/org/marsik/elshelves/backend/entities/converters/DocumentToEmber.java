package org.marsik.elshelves.backend.entities.converters;

import gnu.trove.set.hash.THashSet;
import org.marsik.elshelves.api.entities.AbstractEntityApiModel;
import org.marsik.elshelves.api.entities.AbstractNamedEntityApiModel;
import org.marsik.elshelves.api.entities.DocumentApiModel;
import org.marsik.elshelves.api.entities.PolymorphicRecord;
import org.marsik.elshelves.backend.entities.Document;
import org.marsik.elshelves.backend.entities.NamedEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Service
@DependsOn("EntityToEmberConversionService")
public class DocumentToEmber extends AbstractEntityToEmber<Document, DocumentApiModel> {
	@Autowired
	UserToEmber userToEmber;

	@Autowired
	NamedObjectToEmber namedObjectToEmber;

	@Autowired
	EntityToEmberConversionService conversionService;

	public DocumentToEmber() {
		super(DocumentApiModel.class);
	}

	@PostConstruct
	void postConstruct() {
		conversionService.register(Document.class, getTarget(), this);
	}

	@Override
	public DocumentApiModel convert(String path, Document object, DocumentApiModel model, Map<UUID, Object> cache, Set<String> include) {
		model.setName(object.getName());
		model.setContentType(object.getContentType());
		model.setCreated(object.getCreated());
		model.setSize(object.getSize());
        model.setUrl(object.getUrl());

		model.setBelongsTo(userToEmber.convert(path, "owner", object.getOwner(), cache, include));

		if (object.getDescribes() != null) {
			model.setDescribes(new THashSet<>());
			for (final NamedEntity n: object.getDescribes()) {
				AbstractNamedEntityApiModel r = conversionService.converter(n, AbstractNamedEntityApiModel.class)
						.convert(path, "describes", n, cache, include);
				model.getDescribes().add(r);
			}
		}

		return model;
	}
}
