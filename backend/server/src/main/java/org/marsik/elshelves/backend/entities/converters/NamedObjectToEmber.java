package org.marsik.elshelves.backend.entities.converters;

import gnu.trove.map.hash.THashMap;
import gnu.trove.set.hash.THashSet;
import org.marsik.elshelves.api.entities.AbstractNamedEntityApiModel;
import org.marsik.elshelves.api.entities.CodeApiModel;
import org.marsik.elshelves.api.entities.DocumentApiModel;
import org.marsik.elshelves.api.entities.NumericPropertyApiModel;
import org.marsik.elshelves.backend.entities.Code;
import org.marsik.elshelves.backend.entities.Document;
import org.marsik.elshelves.backend.entities.NamedEntity;
import org.marsik.elshelves.backend.entities.NumericPropertyValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Service
public class NamedObjectToEmber {
	@Autowired
	UserToEmber userToEmber;

	@Autowired
	DocumentToEmber documentToEmber;

    @Autowired
    NumericPropertyToEmber numericPropertyToEmber;

    @Autowired
    CodeToEmber codeToEmber;

	public AbstractNamedEntityApiModel convert(String path, NamedEntity object, AbstractNamedEntityApiModel model, Map<UUID, Object> cache, Set<String> include) {
		model.setName(object.getName());
		model.setSummary(object.getSummary());
		model.setDescription(object.getDescription());
        model.setFlagged(object.getFlagged());
        model.setCanBeDeleted(object.canBeDeleted());
		model.setCreated(object.getCreated());

        if (object.getCodes() != null) {
            model.setCodes(new THashSet<CodeApiModel>());
            for (Code c: object.getCodes()) {
                model.getCodes().add(codeToEmber.convert(path, "code", c, cache, include));
            }
        }

		model.setBelongsTo(userToEmber.convert(path, "owner", object.getOwner(), cache, include));

		if (object.getDescribedBy() != null) {
			model.setDescribedBy(new THashSet<DocumentApiModel>());

			for (Document d: object.getDescribedBy()) {
				model.getDescribedBy().add(documentToEmber.convert(path, "document", d, cache, include));
			}
		}

        if (object.getProperties() != null) {
            model.setValues(new THashMap<UUID, Long>());
            model.setProperties(new THashSet<NumericPropertyApiModel>());

            for (NumericPropertyValue v: object.getProperties()) {
                model.getValues().put(v.getProperty().getId(), v.getValue());
                model.getProperties().add(numericPropertyToEmber.convert(path, "property", v.getProperty(), cache, include));
            }
        }

		return model;
	}
}
