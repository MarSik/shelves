package org.marsik.elshelves.backend.entities.converters;

import gnu.trove.set.hash.THashSet;
import org.marsik.elshelves.api.entities.AbstractEntityApiModel;
import org.marsik.elshelves.api.entities.ListApiModel;
import org.marsik.elshelves.api.entities.PolymorphicRecord;
import org.marsik.elshelves.backend.entities.IdentifiedEntity;
import org.marsik.elshelves.backend.entities.List;
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
public class EmberToList extends AbstractEmberToEntity<ListApiModel, List> {
    @Autowired
    EmberToNamedObject emberToNamedObject;

    @Autowired
    EmberToEntityConversionService conversionService;

    public EmberToList() {
        super(List.class);
    }

    @PostConstruct
    void postConstruct() {
        conversionService.register(ListApiModel.class, getTarget(), this);
    }

    @Override
    public List convert(String path, ListApiModel object, List model, Map<UUID, Object> cache, Set<String> include) {
        emberToNamedObject.convert(path, object, model, cache, include);

        if (object.getItems() != null) {
            model.setItems(new THashSet<>());
            for (AbstractEntityApiModel r: object.getItems()) {
                IdentifiedEntity e = conversionService.converter(r, IdentifiedEntity.class)
                        .convert(path, "item", r, cache, include);
                model.getItems().add(e);
            }
        } else {
            model.setItems(new IdentifiedEntity.UnprovidedSet<IdentifiedEntity>());
        }

        return model;
    }
}
