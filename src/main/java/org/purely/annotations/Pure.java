package org.purely.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation can be placed on the class level or on the method level.
 * <p>
 * On the class level, this annotation signifies that the class is immutable and all methods operating on the class
 * are referentially transparent.
 * <p>
 * On the method level, this annotation signifies that the method itself is referentially transparent, however the
 * class that contains it may not follow the above rules.
 */
@Retention(RetentionPolicy.CLASS)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface Pure {
}
