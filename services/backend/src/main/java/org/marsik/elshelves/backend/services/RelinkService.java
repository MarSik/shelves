package org.marsik.elshelves.backend.services;

import gnu.trove.set.hash.THashSet;
import org.marsik.elshelves.backend.entities.OwnedEntity;
import org.marsik.elshelves.backend.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.neo4j.support.Neo4jTemplate;
import org.springframework.stereotype.Service;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;
import java.util.UUID;

@Service
public class RelinkService {
	@Autowired
	Neo4jTemplate neo4jTemplate;

    @Autowired
    UuidGenerator uuidGenerator;

	protected <E extends OwnedEntity> E getRelinked(E value) {
		return (E)neo4jTemplate.findByIndexedValue(value.getClass(), "uuid", value.getUuid().toString()).singleOrNull();
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
		return relink(entity, null, new THashSet<UUID>());
	}

    protected <E extends OwnedEntity> E relink(E entity, User user) {
        return relink(entity, user, new THashSet<UUID>());
    }

    protected <E extends OwnedEntity> E relink(E entity, User user, Set<UUID> known) {
        if (entity.getUuid() != null
                && known.contains(entity.getUuid())) {
            return entity;
        }

        if (entity.getUuid() != null) {
            known.add(entity.getUuid());
        }

		PropertyDescriptor[] properties;
		try {
			properties = Introspector.getBeanInfo(entity.getClass()).getPropertyDescriptors();
		} catch (IntrospectionException ex) {
			ex.printStackTrace();
			return entity;
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
						if (value.getUuid() != null && value.getNodeId() == null) {
							OwnedEntity v = getRelinked(value);
                            // Entity does exist in DB, replace the reference with
                            // the connected entity
							if (v != null) {
                                setter.invoke(entity, v);
                            // New entity
                            } else {
                                relink(value, user, known);
                            }
                        // Missing UUID meaning new entity
						} else if (value.getUuid() == null) {
                            value.setUuid(uuidGenerator.generate());
                            relink(value, user, known);
                        }
					}
				} catch (InvocationTargetException | IllegalAccessException ex) {
					ex.printStackTrace();
				}

            // Many-to-* relationship
			} else if (Collection.class.isAssignableFrom(f.getPropertyType())) {
				try {
					Collection<OwnedEntity> newItems = new ArrayList<OwnedEntity>();
					Collection<OwnedEntity> items = (Collection<OwnedEntity>)getter.invoke(entity);
					for (OwnedEntity item: items) {
                        // New entity
						if (item.getUuid() == null) {
							newItems.add(item);
                            item.setUuid(uuidGenerator.generate());
							relink(item, user, known);
                        // Connected existing entity
						} else if(item.getNodeId() != null) {
							newItems.add(item);
                        // Disconnected potentially existing entity
						} else {
							OwnedEntity v = getRelinked(item);
                            // Entity known in the database, add the connected version to the list
                            if (v != null) {
                                newItems.add(v);
                            // New entity
                            } else {
                                newItems.add(item);
                                relink(item, user, known);
                            }
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

        // Ensure the entity is properly owned
		if (entity.getOwner() == null && user != null) {
			entity.setOwner(user);
		}

		return entity;
	}

}
