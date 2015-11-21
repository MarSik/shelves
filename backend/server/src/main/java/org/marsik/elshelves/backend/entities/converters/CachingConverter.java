package org.marsik.elshelves.backend.entities.converters;

import java.util.Map;
import java.util.Set;

public interface CachingConverter<F, T, U> {
	/**
	 * Convert object to T type. Check for null and cache presence.
	 * @param object The object to convert
	 * @param cache Object cache to avoid multiple conversions of the same object
	 * @param include Set of field paths to include during conversion
	 * @param path The field path of the parent object
	 * @param element The element name of the current field
	 * @return The converted object
	 */
    T convert(String path, String element, F object, Map<U, Object> cache, Set<String> include);

	/**
	 * Convert object to T type. Check for null and cache presence.
	 * @param object The object to convert
	 * @param cache Object cache to avoid multiple conversions of the same object
	 * @return The converted object
	 */
	T convert(F object, Map<U, Object> cache);

	/**
	 * Convert data from object to model. Expect fields to be not null.
	 * @param path
	 * @param object
	 * @param model
	 * @param cache  @return
	 * @param include
	 */
	T convert(String path, F object, T model, Map<U, Object> cache, Set<String> include);
}
