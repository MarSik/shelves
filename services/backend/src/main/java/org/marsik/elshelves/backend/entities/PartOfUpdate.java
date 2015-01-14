package org.marsik.elshelves.backend.entities;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * This annotation is supposed to be used on getter methods.
 *
 * When an entity is updated all annotated getters are called
 * and the received value is transfered from the update to the
 * entity.
 *
 * IMPORTANT: Ember.js does not send Many-to-One relationship
 * data by default. So collection setters for has many
 * relationships must not be annotated or you risk losing
 * the relationship.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface PartOfUpdate {
}
