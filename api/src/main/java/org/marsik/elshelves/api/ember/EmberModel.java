package org.marsik.elshelves.api.ember;

import com.google.common.base.CaseFormat;
import gnu.trove.map.hash.THashMap;
import gnu.trove.set.hash.THashSet;
import org.atteo.evo.inflector.English;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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

		// used to avoid recursion during sideloading
		private final Set<Object> knownObjects = new THashSet<>();

        public Builder(final Object entity) {
            payload = entity;
            payloadName = getSingularName(entity.getClass());

			knownObjects.add(entity);
			implicitSideloader(entity);
        }

		public Builder(final Class<T> clazz, final Iterable<T> entities) {
			payload = entities;
			payloadName = getPluralName(clazz);

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
				if (knownObjects.contains(entity)) {
					return this;
				}

				Collection<Object> bucket = getSideLoadingBucket(entity.getClass());
                bucket.add(entity);
				knownObjects.add(entity);
				implicitSideloader(entity);
            }
            return this;
        }

		public <K> Builder<T> sideLoad(final Class<K> clazz, final Iterable<K> entities) {
			return sideLoad(clazz, entities, false);
		}

		public <K> Builder<T> sideLoad(final Class<K> clazz, final Iterable<? extends K> entities, boolean polymorphic) {
			if (entities != null) {
				for (K item: entities) {
					if (knownObjects.contains(item)) {
						continue;
					}

					Collection<Object> bucket = getSideLoadingBucket(polymorphic ? item.getClass() : clazz);
					bucket.add(item);
					knownObjects.add(item);
					implicitSideloader(item);
				}
			}
			return this;
		}

        public <K> Builder<T> sideLoad(final String rootName, final Iterable<K> entities) {
            if (entities != null) {
                Collection<Object> bucket = getSideLoadingBucket(rootName);
				for (K item: entities) {
					if (knownObjects.contains(item)) {
						continue;
					}

					bucket.add(item);
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
						sideLoad((Class<Object>)sideload.asType(), (Iterable<Object>)getter.invoke(entity), sideload.polymorphic());
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