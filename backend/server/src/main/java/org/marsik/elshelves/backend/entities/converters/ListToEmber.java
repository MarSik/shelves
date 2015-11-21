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
public class ListToEmber extends AbstractEntityToEmber<List, ListApiModel> {
    @Autowired
    NamedObjectToEmber namedObjectToEmber;

    public ListToEmber() {
        super(ListApiModel.class);
    }

    @Override
    public ListApiModel convert(String path, List object, ListApiModel model, Map<UUID, Object> cache, Set<String> include) {
        namedObjectToEmber.convert(path, object, model, cache, include);

        model.setItems(new THashSet<>());

        for (IdentifiedEntity entity: object.getItems()) {
            model.getItems().add(PolymorphicRecord.build(entity.getEmberType(), entity.getId()));
        }

        return model;
    }
}
