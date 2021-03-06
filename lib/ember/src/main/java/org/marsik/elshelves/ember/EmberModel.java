package org.marsik.elshelves.ember;

import gnu.trove.map.hash.THashMap;
import gnu.trove.set.hash.THashSet;
import org.atteo.evo.inflector.English;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Taken from http://springember.blogspot.cz/2014/08/using-ember-data-restadapter-with.html
 */
public final class EmberModel extends HashMap<String, Object> {

    private EmberModel() {
        //Must use the builder
    }

	public static class Builder<T> implements org.marsik.elshelves.ember.Builder<EmberModel> {
        private final Set<Object> sideLoadedItems = new THashSet<Object>();
        private final Map<String, Object> metaData = new HashMap<String, Object>();
        private final String payloadName;
        private final Object payload;

        // Set of objects to purge from client's cache
        private final Set<EmberEntity> purge = new THashSet<>();

		// used to avoid recursion during sideloading
		private final Set<Object> knownObjects = new THashSet<>();

        public Builder(final Object entity) {
            payload = entity;
            payloadName = "data";

			knownObjects.add(entity);
			implicitSideloader(entity);
        }

		public Builder(final Iterable<T> entities) {
			payload = entities;
			payloadName = "data";

			for (T entity: entities) {
				knownObjects.add(entity);
			}

			for (T entity: entities) {
				implicitSideloader(entity);
			}
		}

        public Builder<T> addMeta(final String key, final Object value) {
            metaData.put(key, value);
            return this;
        }

        public Builder<T> sideLoad(final Object entity) {
            if (entity != null) {
				if (knownObjects.contains(entity)) {
					return this;
				}

                sideLoadedItems.add(entity);
				knownObjects.add(entity);
				implicitSideloader(entity);
            }
            return this;
        }

        public <K> Builder<T> sideLoad(final Iterable<K> entities) {
            if (entities != null) {
				for (K item: entities) {
					if (knownObjects.contains(item)) {
						continue;
					}

					sideLoadedItems.add(item);
					knownObjects.add(item);
					implicitSideloader(item);
				}
            }
            return this;
        }

		public void implicitSideloader(Object entity) {
			PropertyDescriptor[] properties;
			try {
				properties = Introspector.getBeanInfo(entity.getClass()).getPropertyDescriptors();
			} catch (IntrospectionException ex) {
				ex.printStackTrace();
				return;
			}

			for (PropertyDescriptor f: properties) {
				Method getter = f.getReadMethod();
				if (getter == null) {
					continue;
				}

				// check the getter annotation
				Sideload sideload = getter.getAnnotation(Sideload.class);

				// and the field annotation as a fallback
				if (sideload == null) {
					try {
						sideload = getter.getDeclaringClass().getDeclaredField(f.getName()).getAnnotation(Sideload.class);
					} catch (NoSuchFieldException ex) {
						// Ignore, can happen for getter only elements
					}
				}

				// Ignore fields without Sideload annotation
				if (sideload == null) {
					continue;
				}

				if (!sideload.asType().equals(None.class)
						&& Iterable.class.isAssignableFrom(f.getPropertyType())) {
					try {
						sideLoad((Iterable<Object>)getter.invoke(entity));
					} catch (IllegalAccessException|InvocationTargetException ex) {
						ex.printStackTrace();
					}
				} else {
					try {
						sideLoad(getter.invoke(entity));
					} catch (IllegalAccessException|InvocationTargetException ex) {
						ex.printStackTrace();
					}
				}
			}
		}

        public Builder<T> purge(EmberEntity entity) {
            purge.add(entity);
            return this;
        }

        @Override
        public EmberModel build() {
            EmberModel sideLoader = new EmberModel();
            sideLoader.put("included", sideLoadedItems);

            List<EmberPurge> purges = new ArrayList<>();

            for (EmberEntity entity: purge) {
                purges.add(new EmberPurge(EmberModelHelper.getSingularName(entity.getClass()), entity.getId()));
            }

            if (!purges.isEmpty()) {
                metaData.put("purge", purges);
            }

            if (metaData.size() > 0) {
                sideLoader.put("meta", metaData);
            }

            sideLoader.put(payloadName, payload);
            return sideLoader;
        }
    }
}
