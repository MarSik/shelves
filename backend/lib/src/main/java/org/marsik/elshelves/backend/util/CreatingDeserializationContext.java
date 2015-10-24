package org.marsik.elshelves.backend.util;

import com.fasterxml.jackson.annotation.ObjectIdGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.InjectableValues;
import com.fasterxml.jackson.databind.deser.DefaultDeserializationContext;
import com.fasterxml.jackson.databind.deser.DeserializerCache;
import com.fasterxml.jackson.databind.deser.DeserializerFactory;
import com.fasterxml.jackson.databind.deser.impl.ReadableObjectId;
import org.marsik.elshelves.jackson.ResolvableReadableObjectId;

public final class CreatingDeserializationContext extends DefaultDeserializationContext {
    private static final long serialVersionUID = 1L;

    public CreatingDeserializationContext(DeserializerFactory df,
            DeserializerCache cache) {
        super(df, cache);
    }

    public CreatingDeserializationContext(DefaultDeserializationContext src,
            DeserializationConfig config, JsonParser jp, InjectableValues values) {
        super(src, config, jp, values);
    }

    public CreatingDeserializationContext(DefaultDeserializationContext src,
            DeserializerFactory factory) {
        super(src, factory);
    }

    public CreatingDeserializationContext(DefaultDeserializationContext src) {
        super(src);
    }

    public DefaultDeserializationContext copy() {
        return (DefaultDeserializationContext)(this.getClass() != CreatingDeserializationContext.class?super.copy():new CreatingDeserializationContext(this));
    }

    public DefaultDeserializationContext createInstance(DeserializationConfig config, JsonParser jp, InjectableValues values) {
        return new CreatingDeserializationContext(this, config, jp, values);
    }

    public DefaultDeserializationContext with(DeserializerFactory factory) {
        return new CreatingDeserializationContext(this, factory);
    }

    @Override
    protected ReadableObjectId createReadableObjectId(ObjectIdGenerator.IdKey key) {
        return new ResolvableReadableObjectId(key);
    }
}
