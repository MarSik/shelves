package org.marsik.elshelves.api.ember;

import com.google.common.base.CaseFormat;
import gnu.trove.map.hash.THashMap;
import gnu.trove.set.hash.THashSet;
import org.atteo.evo.inflector.English;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Taken from http://springember.blogspot.cz/2014/08/using-ember-data-restadapter-with.html
 */
public final class EmberModel extends HashMap<String, Object> {

    private EmberModel() {
        //Must use the builder
    }

    public static class Builder<T> implements org.marsik.elshelves.api.ember.Builder<EmberModel> {
        private final Map<String, Set<Object>> sideLoadedItems = new THashMap<String, Set<Object>>();
        private final Map<String, Object> metaData = new HashMap<String, Object>();
        private final String payloadName;
        private final Object payload;

        public Builder(final Object entity) {
            payload = entity;
            payloadName = getSingularName(entity.getClass());
        }

        public Builder(final Class<T> clazz, final Collection<T> entities) {
            payload = entities;
            payloadName = getPluralName(clazz);
        }

		public Builder(final Class<T> clazz, final Iterable<T> entities) {
			payload = entities;
			payloadName = getPluralName(clazz);
		}

		public Builder(final String rootName, final Collection<?> entities) {
            payload = entities;
            payloadName = English.plural(rootName);
        }

        public Builder<T> addMeta(final String key, final Object value) {
            metaData.put(key, value);
            return this;
        }

        private Collection<Object> getSideLoadingBucket(Class<?> type) {
            String bucket = getPluralName(type);

            if (!sideLoadedItems.containsKey(bucket)) {
                sideLoadedItems.put(bucket, new THashSet<Object>());
            }
            return sideLoadedItems.get(bucket);
        }

        private Collection<Object> getSideLoadingBucket(String type) {
            String bucket = English.plural(type);

            if (!sideLoadedItems.containsKey(bucket)) {
                sideLoadedItems.put(bucket, new THashSet<Object>());
            }
            return sideLoadedItems.get(bucket);
        }

        public Builder<T> sideLoad(final Object entity) {
            if (entity != null) {
                Collection<Object> bucket = getSideLoadingBucket(entity.getClass());
                bucket.add(entity);
            }
            return this;
        }

        public <K> Builder<T> sideLoad(final Class<K> clazz, final Collection<K> entities) {
            if (entities != null) {
                Collection<Object> bucket = getSideLoadingBucket(clazz);
                bucket.addAll(entities);
            }
            return this;
        }

        public Builder<T> sideLoad(final String rootName, final Collection<?> entities) {
            if (entities != null) {
                Collection<Object> bucket = getSideLoadingBucket(rootName);
                bucket.addAll(entities);
            }
            return this;
        }

        private String getSingularName(final Class<?> clazz) {
            if (clazz.isAnnotationPresent(EmberModelName.class)) {
                return clazz.getAnnotation(EmberModelName.class).value();
            }
            else {
                return CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, clazz.getSimpleName());
            }
        }

        private String getPluralName(final Class<?> clazz) {
            return English.plural(getSingularName(clazz));
        }

        @Override
        public EmberModel build() {
            EmberModel sideLoader = new EmberModel();
            sideLoader.putAll(sideLoadedItems);

            if (metaData.size() > 0) {
                sideLoader.put("meta", metaData);
            }

            sideLoader.put(payloadName, payload);
            return sideLoader;
        }
    }
}