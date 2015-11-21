package org.marsik.elshelves.backend.entities.converters;

import gnu.trove.set.hash.THashSet;
import org.marsik.elshelves.api.entities.ListApiModel;
import org.marsik.elshelves.api.entities.PolymorphicRecord;
import org.marsik.elshelves.backend.entities.IdentifiedEntity;
import org.marsik.elshelves.backend.entities.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Service
public class EmberToList extends AbstractEmberToEntity<ListApiModel, List> {
    @Autowired
    EmberToNamedObject emberToNamedObject;

    public EmberToList() {
        super(List.class);
    }

    @Override
    public List convert(String path, ListApiModel object, List model, Map<UUID, Object> cache, Set<String> include) {
        emberToNamedObject.convert(path, object, model, cache, include);

        if (object.getItems() != null) {
            model.setItems(new THashSet<>());
            for (PolymorphicRecord r: object.getItems()) {
                model.getItems().add(IdentifiedEntity.fromPolymorphicRecord(r));
            }
        } else {
            model.setItems(new IdentifiedEntity.UnprovidedSet<IdentifiedEntity>());
        }

        return model;
    }
}
