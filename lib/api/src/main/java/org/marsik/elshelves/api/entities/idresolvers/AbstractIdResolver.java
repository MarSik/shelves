package org.marsik.elshelves.api.entities.idresolvers;

import com.fasterxml.jackson.annotation.ObjectIdGenerator;
import com.fasterxml.jackson.annotation.ObjectIdResolver;
import org.marsik.elshelves.api.entities.AbstractEntityApiModel;
import org.marsik.elshelves.jackson.CreateObjectIdResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractIdResolver implements CreateObjectIdResolver {
	private final static Logger log = LoggerFactory.getLogger(AbstractIdResolver.class);

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
			AbstractEntityApiModel stub =  getType().getConstructor(id.key.getClass()).newInstance(id.key);
			stub.setStub(true);
			return stub;
		} catch (NoSuchMethodException|IllegalAccessException|InvocationTargetException|InstantiationException ex) {
			log.error("Error creating empty shell", ex);
			return null;
		}
	}

	@Override
	public boolean canUseFor(ObjectIdResolver resolverType) {
		return getClass().isAssignableFrom(resolverType.getClass());
	}

	protected abstract Class<? extends AbstractEntityApiModel> getType();

	@Override
	public ObjectIdResolver newForDeserialization(Object c) {
		//DeserializationContext context = (DeserializationContext)c;
        try {
            return this.getClass().newInstance();
        } catch (InstantiationException|IllegalAccessException ex) {
			log.error("Error creating AbstractIdResolver", ex);
			return null;
        }
    }
}
