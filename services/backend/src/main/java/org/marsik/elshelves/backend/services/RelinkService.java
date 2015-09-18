package org.marsik.elshelves.backend.services;

import gnu.trove.map.hash.THashMap;
import org.marsik.elshelves.backend.entities.OwnedEntity;
import org.marsik.elshelves.backend.entities.User;
import org.marsik.elshelves.backend.repositories.OwnedEntityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;

@Service
public class RelinkService {
    @Autowired
    UuidGenerator uuidGenerator;

    @Autowired
    OwnedEntityRepository ownedEntityRepository;

	protected <E extends OwnedEntity> E getRelinked(E value, Map<UUID, Object> cache)  {
		if (cache.containsKey(value.getUuid())) {
			return (E)cache.get(value.getUuid());
		}
		E entity = (E)ownedEntityRepository.findByUuid(value.getUuid());
		return entity;
	}

	/**
	 * This method is used to realign entities coming from external sources
	 * like REST based web application with the entities stored in the database.
	 *
	 * The linking is done using the uuid property values.
	 *
	 * Modus operandi:
	 *
	 * Find all relationship properties and make sure they reference existing
	 * database entities by querying the uuid index.
	 * If the property does not contain uuid (happens with
	 * nested rest fields), call relink on the the property contents to relink
	 * the nested properties.
	 *
	 * @param entity Entity to relink with the daatabase
	 * @return
	 */

	protected <E extends OwnedEntity> E relink(E entity) {
		return relink(entity, null, new THashMap<UUID, Object>(), false);
	}

    protected <E extends OwnedEntity> E relink(E entity, User user) {
        return relink(entity, user, new THashMap<UUID, Object>(), false);
    }

    protected <E extends OwnedEntity> E relink(E entity, User user, OwnedEntity updatedEntity) {
        Map<UUID, Object> cache = new THashMap<>();
        cache.put(updatedEntity.getUuid(), updatedEntity);
        return relink(entity, user, cache, true);
    }

    protected <E extends Object> void relinkImpl(E entity, User user, Map<UUID, Object> known) {
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

            // One-to-* relationship
            if (OwnedEntity.class.isAssignableFrom(f.getPropertyType())) {
                try {
                    OwnedEntity value = (OwnedEntity) getter.invoke(entity);
                    Method setter = f.getWriteMethod();

                    if (value != null && setter != null) {
                        // Potentially existing UUID but unconnected entity
                        if (value.getUuid() != null && value.getId() == null) {
                            OwnedEntity v = getRelinked(value, known);
                            // Entity does exist in DB, replace the reference with
                            // the connected entity
                            if (v != null) {
                                setter.invoke(entity, v);
                                known.put(v.getUuid(), v);
                                // New entity
                            } else {
                                relink(value, user, known, false);
                            }
                            // Missing UUID meaning new entity
                        } else if (value.getUuid() == null) {
                            value.setUuid(uuidGenerator.generate());
                            relink(value, user, known, false);
                        }
                    }
                } catch (InvocationTargetException | IllegalAccessException ex) {
                    ex.printStackTrace();
                }

                // Many-to-* relationship
            } else if (Collection.class.isAssignableFrom(f.getPropertyType())) {
                try {
                    Collection<Object> newItems = new ArrayList<Object>();
                    Collection<Object> items = (Collection<Object>)getter.invoke(entity);
                    if (items == null) {
                        continue;
                    }

                    for (Object item0: items) {
                        if (item0 instanceof OwnedEntity) {
                            // OwnedEntity, relink
                            OwnedEntity item = (OwnedEntity)item0;

                            // New entity
                            if (item.getUuid() == null) {
                                newItems.add(item);
                                item.setUuid(uuidGenerator.generate());
                                relink(item, user, known, false);
                                // Connected existing entity
                            } else if (item.getId() != null) {
                                newItems.add(item);
                                // Disconnected potentially existing entity
                            } else {
                                OwnedEntity v = getRelinked(item, known);
                                // Entity known in the database, add the connected version to the list
                                if (v != null) {
                                    newItems.add(v);
                                    known.put(v.getUuid(), v);
                                    // New entity
                                } else {
                                    newItems.add(item);
                                    relink(item, user, known, false);
                                }
                            }
                        } else {
                            // Not owned entity, probably relationship entity
                            // keep it as it is and relink the internal properties
                            newItems.add(item0);
                            relinkImpl(item0, user, known);
                        }
                    }

                    // Replace the references with the relinked ones using proper
                    // add calls so the AspectJ mapping on linked entities updates
                    // the database propery
                    items.clear();
                    items.addAll(newItems);
                } catch (InvocationTargetException | IllegalAccessException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    protected <E extends OwnedEntity> E relink(E entity, User user, Map<UUID, Object> known, boolean ignoreCache) {
        if (!ignoreCache
                && entity.getUuid() != null
                && known.containsKey(entity.getUuid())) {
            return entity;
        }

        if (!ignoreCache
                && entity.getUuid() != null) {
            known.put(entity.getUuid(), entity);
        }

        relinkImpl(entity, user, known);

        // Ensure the entity is properly owned
        if (entity.getOwner() == null && user != null) {
            entity.setOwner(user);
        }

        return entity;
	}

}
