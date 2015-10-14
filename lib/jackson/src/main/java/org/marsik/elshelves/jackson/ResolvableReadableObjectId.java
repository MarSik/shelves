package org.marsik.elshelves.jackson;

import com.fasterxml.jackson.annotation.ObjectIdGenerator;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.impl.ReadableObjectId;

import java.io.IOException;

public class ResolvableReadableObjectId extends ReadableObjectId {
    public ResolvableReadableObjectId(Object id) {
        super(id);
    }

    public ResolvableReadableObjectId(ObjectIdGenerator.IdKey key) {
        super(key);
    }

    @Override
    public boolean tryToResolveUnresolved(DeserializationContext ctxt) {
        if (this._resolver instanceof CreateObjectIdResolver) {
            Object newItem = ((CreateObjectIdResolver)_resolver).createItem(_key);
            if (newItem == null) {
                return false;
            } else {
                try {
                    bindItem(newItem);
                    return true;
                } catch (IOException e) {
                    e.printStackTrace();
                    return false;
                }
            }
        } else {
            return super.tryToResolveUnresolved(ctxt);
        }
    }
}
