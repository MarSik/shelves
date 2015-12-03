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
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Service
@DependsOn("EntityToEmberConversionService")
public class EmberToNamedObject {

	@Autowired
	EmberToUser emberToUser;

	@Autowired
	EmberToDocument emberToDocument;

    @Autowired
    EmberToCode emberToCode;

    @Autowired
    EmberToNumericProperty emberToNumericProperty;

    @Autowired
    EmberToEntityConversionService conversionService;

	public NamedEntity convert(String path, AbstractNamedEntityApiModel object, NamedEntity model, Map<UUID, Object> cache, Set<String> include) {
		model.setId(object.getId());
        model.setVersion(object.getVersion());
		model.setName(object.getName());
		model.setSummary(object.getSummary());
		model.setDescription(object.getDescription());
		model.setOwner(emberToUser.convert(path, "owner", object.getBelongsTo(), cache, include));
        model.setFlagged(object.getFlagged());
        model.setCreated(object.getCreated());

        if (object.getCodes() != null) {
            model.setCodes(new THashSet<Code>());
            for (CodeApiModel c: object.getCodes()) {
                model.addCode(emberToCode.convert(path, "code", c, cache, include));
            }
        } else {
            model.setCodes(new IdentifiedEntity.UnprovidedSet<>());
        }

		if (object.getDescribedBy() != null) {
			model.setDescribedBy(new THashSet<Document>());
			for (DocumentApiModel d: object.getDescribedBy()) {
				model.addDescribedBy(emberToDocument.convert(path, "document", d, cache, include));
			}
		} else {
            model.setDescribedBy(new IdentifiedEntity.UnprovidedSet<>());
        }

        if (object.getProperties() != null) {
            model.setProperties(new THashSet<NumericPropertyValue>());
            for (NumericPropertyApiModel pModel: object.getProperties()) {
                NumericProperty p = emberToNumericProperty.convert(path, "property", pModel, cache, include);
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
