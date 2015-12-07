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

    public Class<T> getTarget() {
        return type;
    }

    @Override
    public T convert(F object, Map<UUID, Object> cache) {
        return convert(null, null, object, cache, new THashSet<String>());
    }

    private T createNew() {
        try {
            return type.newInstance();
        } catch (InstantiationException|IllegalAccessException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    @Override
    public T convert(String path, String element, F object, Map<UUID, Object> cache, Set<String> include) {
        if (object == null) {
            return null;
        }

        path = getMergedPath(path, element);

        // First check whether we need the full object
        // we need it when it is:
        // - top level object AND it is not in the cache
        // - OR it was mentioned in the include list
        // - OR it has no ID
        if (object.getId() != null
                && element != null
                && (include == null || !include.contains(path))) {
            T model = createNew();
            if (model == null) {
                return null;
            }
            model.setId(object.getId());
            model.setEntityType(model.getEmberType());
            model.setStub(true);
            return model;
        }

        // New conversion
        T model = createNew();
        if (model == null) {
            return null;
        }

        model.setId(object.getId());
        model.setEntityType(model.getEmberType());
        model.setVersion(object.getVersion());
        model.setStub(false);

        // Save to the cache
        cache.put(object.getId(), model);

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
