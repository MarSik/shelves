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
public class ListToEmber extends AbstractEntityToEmber<List, ListApiModel> {
    public ListToEmber() {
        super(ListApiModel.class);
    }

    @Override
    public ListApiModel convert(List object, ListApiModel model, int nested, Map<UUID, Object> cache) {
        model.setItems(new THashSet<>());

        for (IdentifiedEntity entity: object.getItems()) {
            model.getItems().add(new PolymorphicRecord(entity.getId()));
        }

        return model;
    }
}
