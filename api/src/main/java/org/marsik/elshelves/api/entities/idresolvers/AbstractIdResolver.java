package org.marsik.elshelves.api.entities.idresolvers;

import com.fasterxml.jackson.annotation.ObjectIdGenerator;
import com.fasterxml.jackson.annotation.ObjectIdResolver;
import com.fasterxml.jackson.annotation.SimpleObjectIdResolver;

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
		Object obj = _items.get(id);
		if (obj == null) {
			obj = create(id);
		}

		return obj;
	}

	public Object create(ObjectIdGenerator.IdKey id) {
		try {
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
	public ObjectIdResolver newForDeserialization(Object context) {
        this._items.clear();
		return this;
	}
}
