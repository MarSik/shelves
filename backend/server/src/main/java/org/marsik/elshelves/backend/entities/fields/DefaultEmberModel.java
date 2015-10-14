package org.marsik.elshelves.backend.entities.fields;

import org.marsik.elshelves.api.entities.AbstractEntityApiModel;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface DefaultEmberModel {
    Class<? extends AbstractEntityApiModel> value();
}
