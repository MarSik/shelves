package org.marsik.elshelves.backend.entities.converters;

import org.marsik.elshelves.api.entities.AbstractEntityApiModel;
import org.marsik.elshelves.backend.entities.IdentifiedEntity;
import org.marsik.elshelves.backend.entities.IdentifiedEntityInterface;

import java.util.Map;
import java.util.UUID;

public abstract class AbstractEmberToEntity<F extends AbstractEntityApiModel, T extends IdentifiedEntity> implements CachingConverter<F, T, UUID> {
    final Class<T> type;

    protected AbstractEmberToEntity(Class<T> type) {
        this.type = type;
    }

    @Override
    public T convert(F object, int nested, Map<UUID, Object> cache) {
        if (object == null) {
            return null;
        }

        if (cache.containsKey(object.getId())) {
            return (T)cache.get(object.getId());
        }

        T model;

        try {
            model = type.newInstance();
        } catch (InstantiationException|IllegalAccessException ex) {
            ex.printStackTrace();
            return null;
        }

        model.setId(object.getId());
        model.setVersion(object.getVersion());

        if (nested > 0
                && object.getId() != null) {
            cache.put(object.getId(), model);
        }
        return convert(object, model, nested, cache);
    }
}
