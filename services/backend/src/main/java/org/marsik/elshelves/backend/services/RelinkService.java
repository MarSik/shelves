package org.marsik.elshelves.backend.services;

import gnu.trove.map.hash.THashMap;
import org.marsik.elshelves.backend.entities.OwnedEntity;
import org.marsik.elshelves.backend.entities.User;
import org.marsik.elshelves.backend.repositories.OwnedEntityRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    Logger log = LoggerFactory.getLogger(RelinkService.class);

    @Autowired
    UuidGenerator uuidGenerator;

    @Autowired
    OwnedEntityRepository ownedEntityRepository;

	protected <E extends OwnedEntity> E getByUuid(E value, Map<UUID, OwnedEntity> cache)  {
		// Consult the relink cache first
        if (cache.containsKey(value.getUuid())) {
			return (E)cache.get(value.getUuid());
		}

        // Try getting the instance from database
		E entity = (E)ownedEntityRepository.findByUuid(value.getUuid());
        if (entity != null) {
            cache.put(entity.getUuid(), entity);
        }

		return entity;
	}

    /**
     * Make sure the object won't collide with an already existing entity, but
     * make sure the cache still knows the old uuid for relinking purposes.
     *
     * This method also sets the owner for the entity.
     *
     * @param value Entity about to be imported
     * @param user The user who will own the entity
     * @param cache Id cache for relinking purposes
     * @param <E> Type of the entity - automatically derived from the value's type
     * @return The value entity prepared for import
     */
    @SuppressWarnings("unchecked")
    public <E extends OwnedEntity> E fixUuidAndOwner(E value, User user, Map<UUID, OwnedEntity> cache)  {
        if (value.getUuid() == null) {
            value.setUuid(uuidGenerator.generate());
            log.warn("Entity {} without UUID -> {}", value.getClass().getName(), value.getUuid().toString());
        }

        if (cache.containsKey(value.getUuid())) {
            return (E)cache.get(value.getUuid());
        }

        cache.put(value.getUuid(), value);

        OwnedEntity e = ownedEntityRepository.findByUuid(value.getUuid());
        if (e != null) {
            value.setUuid(uuidGenerator.generate());
            cache.put(value.getUuid(), value);
        }

        value.setOwner(user);
        return value;
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
		return relink(entity, null, new THashMap<UUID, OwnedEntity>(), false);
	}

    protected <E extends OwnedEntity> E relink(E entity, User user) {
        return relink(entity, user, new THashMap<UUID, OwnedEntity>(), false);
    }

    protected <E extends OwnedEntity> E relink(E entity, User user, OwnedEntity updatedEntity) {
        Map<UUID, OwnedEntity> cache = new THashMap<>();
        cache.put(updatedEntity.getUuid(), updatedEntity);
        return relink(entity, user, cache, true);
    }

    protected <E> void relinkImpl(E entity, User user, Map<UUID, OwnedEntity> known) {
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

                    if (value == null || setter == null) {
                        continue;
                    }

                    // Potentially existing UUID but unconnected entity
                    if (value.getUuid() != null && value.getId() == null) {
                        OwnedEntity v = getByUuid(value, known);

                        // Entity does exist in DB or cache, replace the reference with
                        // the connected entity
                        if (v != null) {
                            setter.invoke(entity, v);
                        } else {
                            // New entity, perform deep relinking
                            known.put(value.getUuid(), value);
                            relink(value, user, known, false);
                        }

                    } else if (value.getUuid() == null) {
                        // Missing UUID meaning new entity
                        // create an UUID for it and perform deep relinking
                        value.setUuid(uuidGenerator.generate());
                        known.put(value.getUuid(), value);
                        relink(value, user, known, false);
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

                            if (item.getUuid() == null) {
                                // New entity, create UUID and perform deep relinking
                                newItems.add(item);
                                item.setUuid(uuidGenerator.generate());
                                known.put(item.getUuid(), item);
                                relink(item, user, known, false);

                            } else if (item.getId() != null) {
                                // Connected existing entity
                                newItems.add(item);

                            } else {
                                // Disconnected potentially existing entity
                                OwnedEntity v = getByUuid(item, known);

                                if (v != null) {
                                    // Entity known in the database or cache
                                    // add the connected version to the list
                                    newItems.add(v);

                                } else {
                                    // New entity with uuid, perform deep relinking
                                    newItems.add(item);
                                    known.put(item.getUuid(), item);
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
                    // add calls so the AspectJ or live mapping on linked entities update
                    // the database properly
                    items.clear();
                    items.addAll(newItems);
                } catch (InvocationTargetException | IllegalAccessException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    protected <E extends OwnedEntity> E relink(E entity, User user, Map<UUID, OwnedEntity> known, boolean ignoreCache) {
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
