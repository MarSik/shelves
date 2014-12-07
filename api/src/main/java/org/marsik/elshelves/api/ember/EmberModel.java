package org.marsik.elshelves.api.ember;

import com.google.common.base.CaseFormat;
import org.atteo.evo.inflector.English;
import org.springframework.util.Assert;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Taken from http://springember.blogspot.cz/2014/08/using-ember-data-restadapter-with.html
 */
public final class EmberModel extends HashMap<String, Object> {

    private EmberModel() {
        //Must use the builder
    }

    public static class Builder<T> implements org.marsik.elshelves.api.ember.Builder<EmberModel> {
        private final Map<String, Object> sideLoadedItems = new HashMap<String, Object>();
        private final Map<String, Object> metaData = new HashMap<String, Object>();

        public Builder(final Object entity) {
            Assert.notNull(entity);
            sideLoad(entity);
        }

        public Builder(final Class<T> clazz, final Collection<T> entities) {
            Assert.notNull(entities);
            sideLoad(clazz, entities);
        }

        public Builder(final String rootName, final Collection<?> entities) {
            Assert.notNull(entities);
            sideLoad(rootName, entities);
        }

        public Builder<T> addMeta(final String key, final Object value) {
            metaData.put(key, value);
            return this;
        }

        public Builder<T> sideLoad(final Object entity) {
            if (entity != null) {
                sideLoadedItems.put(getSingularName(entity.getClass()), entity);
            }
            return this;
        }

        public <K> Builder<T> sideLoad(final Class<K> clazz, final Collection<K> entities) {
            if (entities != null) {
                sideLoadedItems.put(getPluralName(clazz), entities);
            }
            return this;
        }

        public Builder<T> sideLoad(final String rootName, final Collection<?> entities) {
            if (entities != null) {
                sideLoadedItems.put(English.plural(rootName), entities);
            }
            return this;
        }

        private String getSingularName(final Class<?> clazz) {
            Assert.notNull(clazz);
            return CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, clazz.getSimpleName());
        }

        private String getPluralName(final Class<?> clazz) {
            return English.plural(getSingularName(clazz));
        }

        @Override
        public EmberModel build() {
            if (metaData.size() > 0) {
                sideLoadedItems.put("meta", metaData);
            }
            EmberModel sideLoader = new EmberModel();
            sideLoader.putAll(sideLoadedItems);
            return sideLoader;
        }
    }
}