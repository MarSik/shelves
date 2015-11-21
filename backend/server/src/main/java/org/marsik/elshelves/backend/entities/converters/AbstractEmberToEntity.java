package org.marsik.elshelves.backend.entities.converters;

import gnu.trove.set.hash.THashSet;
import org.marsik.elshelves.api.entities.AbstractEntityApiModel;
import org.marsik.elshelves.backend.entities.IdentifiedEntity;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

public abstract class AbstractEmberToEntity<F extends AbstractEntityApiModel, T extends IdentifiedEntity> implements CachingConverter<F, T, UUID> {
    final Class<T> type;

    protected AbstractEmberToEntity(Class<T> type) {
        this.type = type;
    }

    @Override
    public T convert(F object, Map<UUID, Object> cache) {
        return convert(null, null, object, cache, new THashSet<String>());
    }

    @Override
    public T convert(String path, String element, F object, Map<UUID, Object> cache, Set<String> include) {
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

        if (!object.isStub()
                && object.getId() != null) {
            cache.put(object.getId(), model);
        }

        return convert(null, object, model, cache, include);
    }
}
