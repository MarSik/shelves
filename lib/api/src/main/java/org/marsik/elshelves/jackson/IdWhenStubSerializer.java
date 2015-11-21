package org.marsik.elshelves.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier;
import com.fasterxml.jackson.databind.ser.PropertyWriter;
import com.fasterxml.jackson.databind.ser.std.BeanSerializerBase;
import com.fasterxml.jackson.databind.util.NameTransformer;
import org.marsik.elshelves.api.entities.AbstractEntityApiModel;

import java.io.IOException;
import java.util.Iterator;

public class IdWhenStubSerializer extends BeanSerializerModifier {
    @Override
    public JsonSerializer<?> modifySerializer(SerializationConfig config, BeanDescription beanDesc, JsonSerializer<?> serializer) {
        if (StubSupport.class.isAssignableFrom(beanDesc.getBeanClass())
                && serializer instanceof BeanSerializerBase) {
            return new StubSerializer((JsonSerializer<StubSupport>) serializer);
        }

        return super.modifySerializer(config, beanDesc, serializer);
    }

    private static class StubSerializer extends JsonSerializer<StubSupport> {
        final JsonSerializer<StubSupport> parent;

        public StubSerializer(JsonSerializer<StubSupport> parent) {
            this.parent = parent;
        }

        @Override
        public void serialize(StubSupport entity, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonProcessingException {
            if (entity != null
                    && entity.isStub()
                    && entity.getId() != null) {
                jgen.writeString(entity.getId().toString());
            } else {
                parent.serialize(entity, jgen, provider);
            }
        }
    }
}
