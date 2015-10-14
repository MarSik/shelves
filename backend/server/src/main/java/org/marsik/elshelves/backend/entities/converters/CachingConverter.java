package org.marsik.elshelves.backend.entities.converters;

import java.util.Map;

public interface CachingConverter<F, T, U> {
	/**
	 * Convert object to T type. Check for null and cache presence.
	 * @param object The object to convert
	 * @param nested The number of nested layers to convert fully. The relationships that go deeper will contain only IDs.
	 *               0 means only the converted object, 1 the children, 2 the grandchildren, ...
	 * @param cache Object cache to avoid multiple conversions of the same object
	 * @return The converted object
	 */
    T convert(F object, int nested, Map<U, Object> cache);

	/**
	 * Convert data from object to model. Expect fields to be not null.
	 *
	 * @param object
	 * @param model
	 * @param nested
	 *@param cache  @return
	 */
	T convert(F object, T model, int nested, Map<U, Object> cache);
}
