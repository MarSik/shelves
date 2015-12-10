package org.marsik.elshelves.backend.entities.converters;

import gnu.trove.set.hash.THashSet;
import org.marsik.elshelves.api.entities.AbstractEntityApiModel;
import org.marsik.elshelves.backend.entities.IdentifiedEntity;
import org.marsik.elshelves.backend.services.AbstractConversionService;

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

        // First check whether we need the full object at all
        // we do not need it when it is:
        // - nested level object
        // - AND it was not mentioned in the include list
        // - AND it has ID
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

        // We have to do the conversion all the time, because the
        // include path might be different
        T model = createNew();
        if (model == null) {
            return null;
        }

        model.setId(object.getId());
        model.setEntityType(model.getEmberType());
        model.setVersion(object.getVersion());
        model.setStub(false);

        convert(path, AbstractConversionService.deproxy(object), model, cache, include);

        // Check whether we need to return a full object or just
        // the eference
        // The full object is needed when
        // - there is no ID
        // - it is a top level object
        // The cache will be used to populate the included list of
        // the response
        if (model.getId() == null
                || element == null) {
            return model;
        } else {
            cache.put(object.getId(), model);
            T reference = createNew();
            if (reference == null) {
                return null;
            }
            reference.setId(object.getId());
            reference.setEntityType(reference.getEmberType());
            return reference;
        }
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
