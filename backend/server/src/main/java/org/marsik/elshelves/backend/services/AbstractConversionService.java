package org.marsik.elshelves.backend.services;

import com.fasterxml.jackson.annotation.JsonTypeName;
import gnu.trove.map.hash.THashMap;
import org.hibernate.proxy.HibernateProxy;
import org.marsik.elshelves.backend.entities.converters.CachingConverter;
import org.marsik.elshelves.ember.EmberModelName;

import java.util.Map;
import java.util.Set;

public class AbstractConversionService<S, D, U> {
    Map<Class<? extends S>,
            CachingConverter<? extends S, ? extends D, U>> sourceConversionMap = new THashMap<>();

    Map<String,
            CachingConverter<? extends S, ? extends D, U>> typeNameConversionMap = new THashMap<>();

    public <F extends S, T extends D> void register(Class<F> source, Class<T> target, CachingConverter<F, T, U> converter) {
        sourceConversionMap.put(source, converter);

        if (source.getAnnotation(JsonTypeName.class) != null) {
            typeNameConversionMap.put(source.getAnnotation(JsonTypeName.class).value(), converter);
        }
    }

    public static <G> G deproxy(Object entity) {
        if (entity instanceof HibernateProxy) {
            entity = ((HibernateProxy) entity).getHibernateLazyInitializer()
                    .getImplementation();
        }

        return (G) entity;
    }

    public <F extends S, T extends D> CachingConverter<F, T, U> converter(F entity, Class<T> dest) {
        entity = deproxy(entity);
        final CachingConverter<F, T, U> ftuCachingConverter = (CachingConverter<F, T, U>) sourceConversionMap.get(entity.getClass());
        return ftuCachingConverter;
    }

    public <F extends S> CachingConverter<F, ? extends D, U> converter(F entity, String typeName) {
        return (CachingConverter<F, ? extends D, U>) typeNameConversionMap.get(typeName);
    }
}
