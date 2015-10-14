package org.marsik.elshelves.backend.app.jackson;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.BeanDeserializerFactory;
import org.marsik.elshelves.backend.util.CreatingDeserializationContext;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

public class Jackson2CustomContextMapperBuilder extends Jackson2ObjectMapperBuilder {
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ObjectMapper> T build() {
        ObjectMapper mapper = new ObjectMapper(null, null,
                new CreatingDeserializationContext(BeanDeserializerFactory.instance, null));
        configure(mapper);
        return (T)mapper;
    }
}
