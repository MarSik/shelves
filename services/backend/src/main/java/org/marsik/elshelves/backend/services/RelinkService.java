package org.marsik.elshelves.backend.services;

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

@Service
public class RelinkService {
	@Autowired
	Neo4jTemplate neo4jTemplate;

	protected <E extends OwnedEntity> E getRelinked(E value) {
		return (E)neo4jTemplate.findByIndexedValue(value.getClass(), "uuid", value.getUuid()).single();
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
		return relink(entity, null);
	}

	protected <E extends OwnedEntity> E relink(E entity, User user) {
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

			if (OwnedEntity.class.isAssignableFrom(f.getPropertyType())) {
				try {
					OwnedEntity value = (OwnedEntity) getter.invoke(entity);
					Method setter = f.getWriteMethod();

					if (value != null && setter != null) {
						if (value.getUuid() != null && value.getNodeId() == null) {
							OwnedEntity v = getRelinked(value);
							assert v != null && v.getClass().equals(value.getClass());
							setter.invoke(entity, v);
						} else {
							relink(value, user);
						}
					}
				} catch (InvocationTargetException | IllegalAccessException ex) {
					ex.printStackTrace();
				}
			} else if (Collection.class.isAssignableFrom(f.getPropertyType())) {
				try {
					Collection<OwnedEntity> newItems = new ArrayList<OwnedEntity>();
					Collection<OwnedEntity> items = (Collection<OwnedEntity>)getter.invoke(entity);
					for (OwnedEntity item: items) {
						if (item.getUuid() == null) {
							newItems.add(item);
							relink(item, user);
						} else if(item.getNodeId() != null) {
							newItems.add(item);
						} else {
							OwnedEntity v = getRelinked(item);
							assert v != null && v.getClass().equals(item.getClass());
							newItems.add(v);
						}
					}
					items.clear();
					items.addAll(newItems);
				} catch (InvocationTargetException | IllegalAccessException ex) {
					ex.printStackTrace();
				}
			}
		}

		if (entity.getOwner() == null && user != null) {
			entity.setOwner(user);
		}

		return entity;
	}

}
