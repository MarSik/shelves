package org.marsik.elshelves.backend.entities.converters;

import org.marsik.elshelves.api.entities.AbstractEntityApiModel;
import org.marsik.elshelves.backend.entities.OwnedEntity;

import java.util.Map;
import java.util.UUID;

public abstract class AbstractEntityToEmber<F extends OwnedEntity, T extends AbstractEntityApiModel> implements CachingConverter<F, T, UUID> {
    final Class<T> type;

    protected AbstractEntityToEmber(Class<T> type) {
        this.type = type;
    }

    @Override
    public T convert(F object, int nested, Map<UUID, Object> cache) {
        if (object == null) {
            return null;
        }

        if (cache.containsKey(object.getUuid())) {
            return (T)cache.get(object.getUuid());
        }

        T model;

        try {
            model = type.newInstance();
        } catch (InstantiationException|IllegalAccessException ex) {
            ex.printStackTrace();
            return null;
        }

        if (nested > 0
                && object.getUuid() != null) {
            cache.put(object.getUuid(), model);
        }
        return convert(object, model, nested, cache);
    }
}