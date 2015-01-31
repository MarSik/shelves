package org.marsik.elshelves.api.entities.idresolvers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.google.common.base.Joiner;
import org.marsik.elshelves.api.entities.fields.FootprintType;
import org.marsik.elshelves.api.entities.fields.SiPrefix;

import java.io.IOException;

public class FootprintTypeDeserializer extends JsonDeserializer<FootprintType> {
    @Override
    public FootprintType deserialize(JsonParser jp, DeserializationContext dc) throws IOException, JsonProcessingException {

        FootprintType type = FootprintType.valueOf(jp.getValueAsString());
        if (type != null) {
            return type;
        }
        throw new JsonMappingException("Invalid value for FootprintType, must be one of: " + Joiner.on(", ").join(FootprintType.values()));
    }
}