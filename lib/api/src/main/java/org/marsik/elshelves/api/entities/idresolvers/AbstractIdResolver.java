package org.marsik.elshelves.api.entities.idresolvers;

import com.fasterxml.jackson.annotation.ObjectIdGenerator;
import com.fasterxml.jackson.annotation.ObjectIdResolver;
import com.fasterxml.jackson.databind.DeserializationContext;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractIdResolver implements ObjectIdResolver {
	private Map<ObjectIdGenerator.IdKey,Object> _items = new HashMap<ObjectIdGenerator.IdKey,Object>();

	@Override
	public void bindItem(ObjectIdGenerator.IdKey id, Object ob)
	{
		if (_items.containsKey(id)) {
			throw new IllegalStateException("Already had POJO for id (" + id.key.getClass().getName() + ") [" + id
					+ "]");
		}
		_items.put(id, ob);
	}

	@Override
	public Object resolveId(ObjectIdGenerator.IdKey id) {
		return _items.get(id);
	}

    @Override
	public Object createItem(ObjectIdGenerator.IdKey id) {
		try {
			System.out.println("Creating empty " +getType().getName()+ " with id "+id.key.toString());
			return getType().getConstructor(id.key.getClass()).newInstance(id.key);
		} catch (NoSuchMethodException|IllegalAccessException|InvocationTargetException|InstantiationException ex) {
			ex.printStackTrace();
			return null;
		}
	}

	@Override
	public boolean canUseFor(ObjectIdResolver resolverType) {
		return getClass().isAssignableFrom(resolverType.getClass());
	}

	protected abstract Class<?> getType();

	@Override
	public ObjectIdResolver newForDeserialization(Object c) {
		DeserializationContext context = (DeserializationContext)c;
        try {
            return this.getClass().newInstance();
        } catch (InstantiationException|IllegalAccessException ex) {
            ex.printStackTrace();
            return null;
        }
    }
}
