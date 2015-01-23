package org.marsik.elshelves.api.entities.idresolvers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.google.common.base.Joiner;
import org.marsik.elshelves.api.entities.fields.IsoSizePrefix;

import java.io.IOException;

public class IsoSizePrefixDeserializer extends JsonDeserializer<IsoSizePrefix> {
    @Override
    public IsoSizePrefix deserialize(JsonParser jp, DeserializationContext dc) throws IOException, JsonProcessingException {

        IsoSizePrefix prefix = IsoSizePrefix.valueOf(jp.getValueAsString());
        if (prefix != null) {
            return prefix;
        }
        throw new JsonMappingException("Invalid value for IsoSizePrefix, must be one of: " + Joiner.on(", ").join(IsoSizePrefix.values()));
    }
}