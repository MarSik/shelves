package org.marsik.elshelves.backend.entities.converters;

import gnu.trove.set.hash.THashSet;
import org.marsik.elshelves.api.entities.AbstractNamedEntityApiModel;
import org.marsik.elshelves.api.entities.DocumentApiModel;
import org.marsik.elshelves.api.entities.NumericPropertyApiModel;
import org.marsik.elshelves.backend.entities.Document;
import org.marsik.elshelves.backend.entities.NamedEntity;
import org.marsik.elshelves.backend.entities.NumericProperty;
import org.marsik.elshelves.backend.entities.NumericPropertyValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
public class EmberToNamedObject {

	@Autowired
	EmberToUser emberToUser;

	@Autowired
	EmberToDocument emberToDocument;

    @Autowired
    EmberToNumericProperty emberToNumericProperty;

	public NamedEntity convert(AbstractNamedEntityApiModel object, NamedEntity model, int nested, Map<UUID, Object> cache) {
		model.setUuid(object.getId());
		model.setName(object.getName());
		model.setSummary(object.getSummary());
		model.setDescription(object.getDescription());
		model.setOwner(emberToUser.convert(object.getBelongsTo(), nested, cache));
        model.setFlagged(object.isFlagged());

		if (object.getDescribedBy() != null) {
			model.setDescribedBy(new THashSet<Document>());
			for (DocumentApiModel d: object.getDescribedBy()) {
				model.getDescribedBy().add(emberToDocument.convert(d, nested, cache));
			}
		}

        if (object.getProperties() != null) {
            model.setProperties(new THashSet<NumericPropertyValue>());
            for (NumericPropertyApiModel pModel: object.getProperties()) {
                NumericProperty p = emberToNumericProperty.convert(pModel, nested, cache);
                NumericPropertyValue v = new NumericPropertyValue();
                v.setEntity(model);
                v.setProperty(p);

                if (object.getValues() != null && object.getValues().get(p.getUuid()) != null) {
                    v.setValue(object.getValues().get(p.getUuid()));
                } else {
                    v.setValue(0L);
                }
                model.getProperties().add(v);
            }
        }

		return model;
	}
}
