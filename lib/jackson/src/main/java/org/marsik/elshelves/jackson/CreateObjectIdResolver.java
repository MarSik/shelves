package org.marsik.elshelves.jackson;

import com.fasterxml.jackson.annotation.ObjectIdGenerator;
import com.fasterxml.jackson.annotation.ObjectIdResolver;

public interface CreateObjectIdResolver extends ObjectIdResolver {
    Object createItem(ObjectIdGenerator.IdKey id);
}
