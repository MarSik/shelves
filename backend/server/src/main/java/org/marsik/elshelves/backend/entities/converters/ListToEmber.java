package org.marsik.elshelves.backend.entities.converters;

import gnu.trove.set.hash.THashSet;
import org.marsik.elshelves.api.entities.AbstractEntityApiModel;
import org.marsik.elshelves.api.entities.ListApiModel;
import org.marsik.elshelves.backend.entities.IdentifiedEntity;
import org.marsik.elshelves.backend.entities.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Service
@DependsOn("EntityToEmberConversionService")
public class ListToEmber extends AbstractEntityToEmber<List, ListApiModel> {
    @Autowired
    NamedObjectToEmber namedObjectToEmber;

    @Autowired
    EntityToEmberConversionService conversionService;

    public ListToEmber() {
        super(ListApiModel.class);
    }

    @PostConstruct
    void postConstruct() {
        conversionService.register(List.class, this);

    }

    @Override
    public ListApiModel convert(String path, List object, ListApiModel model, Map<UUID, Object> cache, Set<String> include) {
        namedObjectToEmber.convert(path, object, model, cache, include);

        model.setItems(new THashSet<>());

        for (IdentifiedEntity entity: object.getItems()) {
            final AbstractEntityApiModel record = conversionService.converter(entity, AbstractEntityApiModel.class)
                    .convert(path, "item", entity, cache, include);
            model.getItems().add(record);
        }

        return model;
    }
}
