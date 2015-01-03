package org.marsik.elshelves.api.ember;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Sideload {
	Class<?> asType() default None.class;
	boolean polymorphic() default false;
}
