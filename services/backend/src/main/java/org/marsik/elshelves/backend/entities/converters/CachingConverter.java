package org.marsik.elshelves.backend.entities.converters;

import java.util.Map;

public interface CachingConverter<F, T, U> {
    T convert(F object, Map<U, Object> cache);
}
