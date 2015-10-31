package org.marsik.elshelves.backend.entities.converters;

import gnu.trove.set.hash.THashSet;
import org.marsik.elshelves.api.entities.ListApiModel;
import org.marsik.elshelves.api.entities.PolymorphicRecord;
import org.marsik.elshelves.backend.entities.IdentifiedEntity;
import org.marsik.elshelves.backend.entities.List;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
public class EmberToList extends AbstractEmberToEntity<ListApiModel, List> {
    public EmberToList() {
        super(List.class);
    }

    @Override
    public List convert(ListApiModel object, List model, int nested, Map<UUID, Object> cache) {
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
