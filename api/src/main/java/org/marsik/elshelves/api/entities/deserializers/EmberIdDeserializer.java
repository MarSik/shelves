package org.marsik.elshelves.api.entities.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.marsik.elshelves.api.entities.AbstractEntityApiModel;
import org.marsik.elshelves.api.entities.PartGroupApiModel;

import java.io.IOException;
import java.util.UUID;

public abstract class EmberIdDeserializer<T extends AbstractEntityApiModel> extends JsonDeserializer<T> {
	protected abstract Class<? extends T> getEntityClass();

	protected T createEntity(UUID uuid) {
		try {
			T entity = getEntityClass().newInstance();
			entity.setId(uuid);
			return entity;
		} catch (InstantiationException|IllegalAccessException ex) {
			ex.printStackTrace();
			return null;
		}
	}

	@Override
	public T deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
		if (jsonParser.getCurrentToken().equals(JsonToken.VALUE_NULL)) {
			return null;
		} else if (jsonParser.getCurrentToken().equals(JsonToken.VALUE_STRING)) {
			UUID uuid = jsonParser.readValueAs(UUID.class);
			return createEntity(uuid);
		} else {
			return jsonParser.readValueAs(getEntityClass());
		}
	}
}
