package org.marsik.elshelves.backend.services;

import gnu.trove.map.hash.THashMap;
import org.marsik.elshelves.backend.entities.IdentifiedEntityInterface;
import org.marsik.elshelves.backend.entities.OwnedEntity;
import org.marsik.elshelves.backend.entities.OwnedEntityInterface;
import org.marsik.elshelves.backend.entities.User;
import org.marsik.elshelves.backend.repositories.IdentifiedEntityRepository;
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
    private static final Logger log = LoggerFactory.getLogger(RelinkService.class);

    @Autowired
    UuidGenerator uuidGenerator;

    @Autowired
    OwnedEntityRepository ownedEntityRepository;

    @Autowired
    IdentifiedEntityRepository identifiedEntityRepository;

	protected <E extends IdentifiedEntityInterface> E getByUuid(E value, Map<UUID, IdentifiedEntityInterface> cache)  {
		// Consult the relink cache first
        if (cache.containsKey(value.getId())) {
			return (E)cache.get(value.getId());
		}

        // Try getting the instance from database
		E entity = (E)identifiedEntityRepository.findById(value.getId());
        if (entity != null) {
            updateCache(cache, entity);
            return entity;
        }

        // Return the entity itself if it does not exist in the DB yet
		return null;
	}

    /**
     * Make sure the object won't collide with an already existing entity, but
     * make sure the cache still knows the old id for relinking purposes.
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
    public <E extends IdentifiedEntityInterface> E fixUuidAndOwner(E value, User user, Map<UUID, IdentifiedEntityInterface> cache)  {
        if (value.getId() == null) {
            value.setId(uuidGenerator.generate());
            log.warn("Entity {} without UUID -> {}", value.getClass().getName(), value.getId().toString());
        }

        if (cache.containsKey(value.getId())) {
            return (E)cache.get(value.getId());
        }

        updateCache(cache, value);

        OwnedEntity e = ownedEntityRepository.findById(value.getId());
        if (e != null) {
            value.setId(uuidGenerator.generate());
            updateCache(cache, value);
        }

        if (value instanceof OwnedEntityInterface) {
            ((OwnedEntityInterface)value).setOwner(user);
        }
        return value;
    }

	/**
	 * This method is used to realign entities coming from external sources
	 * like REST based web application with the entities stored in the database.
	 *
	 * The linking is done using the id property values.
	 *
	 * Modus operandi:
	 *
	 * Find all relationship properties and make sure they reference existing
	 * database entities by querying the id index.
	 * If the property does not contain id (happens with
	 * nested rest fields), call relink on the the property contents to relink
	 * the nested properties.
	 *
	 * @param entity Entity to relink with the daatabase
	 * @return
	 */

	protected <T extends OwnedEntityInterface> T relink(T entity) {
		return relink(entity, null, new THashMap<UUID, IdentifiedEntityInterface>(), false);
	}

    protected <E extends OwnedEntityInterface> E relink(E entity, User user) {
        return relink(entity, user, new THashMap<UUID, IdentifiedEntityInterface>(), false);
    }

    protected <E extends OwnedEntityInterface> E relink(E entity, User user, E updatedEntity) {
        Map<UUID, IdentifiedEntityInterface> cache = new THashMap<>();
        updateCache(cache, updatedEntity);
        return relink(entity, user, cache, true);
    }

    protected void relinkImpl(Object entity, User user, Map<UUID, IdentifiedEntityInterface> known) {
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
            if (IdentifiedEntityInterface.class.isAssignableFrom(f.getPropertyType())) {
                try {
                    OwnedEntityInterface value = (OwnedEntityInterface) getter.invoke(entity);
                    Method setter = f.getWriteMethod();

                    if (value == null || setter == null) {
                        continue;
                    }

                    // Potentially existing UUID but unconnected entity
                    if (value.getId() != null && value.getDbId() == null) {
                        OwnedEntityInterface v = getByUuid(value, known);

                        // Entity does exist in DB or cache, replace the reference with
                        // the connected entity
                        if (v != null) {
                            setter.invoke(entity, v);
                        } else {
                            // New entity, perform deep relinking
                            log.info("Deep relinking of {}.{} ({})",
                                    value.getClass().getName(), f.getName(),
                                    value.getId().toString());
                            updateCache(known, value);
                            relink(value, user, known, false);
                        }

                    } else if (value.getId() == null) {
                        // Missing UUID meaning new entity
                        // create an UUID for it and perform deep relinking
                        value.setId(uuidGenerator.generate());
                        updateCache(known, value);
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
                        if (item0 instanceof IdentifiedEntityInterface) {
                            // OwnedEntity, relink
                            OwnedEntityInterface item = (OwnedEntityInterface) item0;

                            if (item.getId() == null) {
                                // New entity, create UUID and perform deep relinking
                                newItems.add(item);
                                item.setId(uuidGenerator.generate());
                                updateCache(known, item);
                                relink(item, user, known, false);

                            } else if (item.getDbId() != null) {
                                // Connected existing entity
                                newItems.add(item);

                            } else {
                                // Disconnected potentially existing entity
                                IdentifiedEntityInterface v = getByUuid(item, known);

                                if (v != null) {
                                    // Entity known in the database or cache
                                    // add the connected version to the list
                                    newItems.add(v);

                                } else {
                                    // New entity with id, perform deep relinking
                                    // New entity, perform deep relinking
                                    log.info("Deep relinking of {}.{} ({})",
                                            item.getClass().getName(), f.getName(),
                                            item.getId().toString());

                                    newItems.add(item);
                                    updateCache(known, item);
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

    private static void updateCache(Map<UUID, IdentifiedEntityInterface> known, IdentifiedEntityInterface value) {
        if (known.containsKey(value.getId())) {
            log.warn("Replacing cached {} with {}", value.getId(), value.toString());
        }

        known.put(value.getId(), value);
    }

    protected <E extends IdentifiedEntityInterface> E relink(E entity, User user, Map<UUID, IdentifiedEntityInterface> known, boolean forceRelink) {
        if (!forceRelink
                && entity.getId() != null
                && known.containsKey(entity.getId())) {
            return entity;
        }

        if (!forceRelink
                && entity.getId() != null) {
            updateCache(known, entity);
        }

        relinkImpl(entity, user, known);

        return entity;
	}

}
