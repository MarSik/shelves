package org.marsik.elshelves.backend.entities.converters;

import gnu.trove.set.hash.THashSet;
import org.marsik.elshelves.api.entities.AbstractEntityApiModel;
import org.marsik.elshelves.backend.entities.IdentifiedEntity;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

public abstract class AbstractEntityToEmber<F extends IdentifiedEntity, T extends AbstractEntityApiModel> implements CachingConverter<F, T, UUID> {
    final Class<T> type;

    protected AbstractEntityToEmber(Class<T> type) {
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
        model.setEntityType(object.getEmberType());
        model.setVersion(object.getVersion());

        path = getMergedPath(path, element);

        if ((element == null
                || (include != null && include.contains(path)))
                && object.getId() != null) {
            cache.put(object.getId(), model);
        } else {
            model.setStub(true);
            return model;
        }

        return convert(path, object, model, cache, include);
    }

    private String getMergedPath(String base, String element) {
        if (base == null) {
            return element;
        } else if (element != null) {
            return base+"."+element;
        } else {
            return base;
        }
    }
}
