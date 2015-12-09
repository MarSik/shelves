package org.marsik.elshelves.backend.services;

import gnu.trove.map.hash.THashMap;
import org.hibernate.proxy.HibernateProxy;
import org.marsik.elshelves.backend.entities.converters.CachingConverter;
import org.marsik.elshelves.ember.EmberModelName;

import java.util.Map;

public class AbstractConversionService<S, D, U> {
    Map<Class<? extends S>,
            CachingConverter<? extends S, ? extends D, U>> sourceConversionMap = new THashMap<>();

    Map<String,
            CachingConverter<? extends S, ? extends D, U>> typeNameConversionMap = new THashMap<>();

    public <F extends S, T extends D> void register(Class<F> source, Class<T> target, CachingConverter<F, T, U> converter) {
        sourceConversionMap.put(source, converter);

        if (source.getAnnotation(EmberModelName.class) != null) {
            typeNameConversionMap.put(source.getAnnotation(EmberModelName.class).value(), converter);
        }
    }

    public <F extends S, T extends D> CachingConverter<F, T, U> converter(F entity, Class<T> dest) {
        if (entity instanceof HibernateProxy) {
            entity = (F) ((HibernateProxy) entity).getHibernateLazyInitializer()
                    .getImplementation();
        }

        return (CachingConverter<F, T, U>) sourceConversionMap.get(entity.getClass());
    }

    public <F extends S> CachingConverter<F, ? extends D, U> converter(F entity, String typeName) {
        return (CachingConverter<F, ? extends D, U>) typeNameConversionMap.get(typeName);
    }
}
