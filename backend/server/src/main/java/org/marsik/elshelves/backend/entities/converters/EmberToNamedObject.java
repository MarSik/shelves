package org.marsik.elshelves.backend.entities.converters;

import gnu.trove.set.hash.THashSet;
import org.marsik.elshelves.api.entities.AbstractNamedEntityApiModel;
import org.marsik.elshelves.api.entities.CodeApiModel;
import org.marsik.elshelves.api.entities.DocumentApiModel;
import org.marsik.elshelves.api.entities.NumericPropertyApiModel;
import org.marsik.elshelves.backend.entities.Code;
import org.marsik.elshelves.backend.entities.Document;
import org.marsik.elshelves.backend.entities.IdentifiedEntity;
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
    EmberToCode emberToCode;

    @Autowired
    EmberToNumericProperty emberToNumericProperty;

	public NamedEntity convert(AbstractNamedEntityApiModel object, NamedEntity model, int nested, Map<UUID, Object> cache) {
		model.setId(object.getId());
        model.setVersion(object.getVersion());
		model.setName(object.getName());
		model.setSummary(object.getSummary());
		model.setDescription(object.getDescription());
		model.setOwner(emberToUser.convert(object.getBelongsTo(), nested, cache));
        model.setFlagged(object.isFlagged());

        if (object.getCodes() != null) {
            model.setCodes(new THashSet<Code>());
            for (CodeApiModel c: object.getCodes()) {
                model.addCode(emberToCode.convert(c, nested, cache));
            }
        } else {
            model.setCodes(new IdentifiedEntity.UnprovidedSet<>());
        }

		if (object.getDescribedBy() != null) {
			model.setDescribedBy(new THashSet<Document>());
			for (DocumentApiModel d: object.getDescribedBy()) {
				model.addDescribedBy(emberToDocument.convert(d, nested, cache));
			}
		} else {
            model.setDescribedBy(new IdentifiedEntity.UnprovidedSet<>());
        }

        if (object.getProperties() != null) {
            model.setProperties(new THashSet<NumericPropertyValue>());
            for (NumericPropertyApiModel pModel: object.getProperties()) {
                NumericProperty p = emberToNumericProperty.convert(pModel, nested, cache);
                NumericPropertyValue v = new NumericPropertyValue();
                v.setEntity(model);
                v.setProperty(p);

                if (object.getValues() != null && object.getValues().get(p.getId()) != null) {
                    v.setValue(object.getValues().get(p.getId()));
                } else {
                    v.setValue(0L);
                }
                model.addProperty(v);
            }
        } else {
            model.setProperties(new IdentifiedEntity.UnprovidedSet<>());
        }

		return model;
	}
}
