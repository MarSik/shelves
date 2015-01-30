package org.marsik.elshelves.api.entities.idresolvers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.google.common.base.Joiner;
import org.marsik.elshelves.api.entities.fields.SiPrefix;

import java.io.IOException;

public class SiSizePrefixDeserializer extends JsonDeserializer<SiPrefix> {
    @Override
    public SiPrefix deserialize(JsonParser jp, DeserializationContext dc) throws IOException, JsonProcessingException {

        SiPrefix prefix = SiPrefix.valueOf(jp.getValueAsString());
        if (prefix != null) {
            return prefix;
        }
        throw new JsonMappingException("Invalid value for IsoSizePrefix, must be one of: " + Joiner.on(", ").join(SiPrefix.values()));
    }
}