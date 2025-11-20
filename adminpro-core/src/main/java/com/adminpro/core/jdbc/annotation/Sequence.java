package com.adminpro.core.jdbc.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @author simon
 */
@Target(TYPE)
@Retention(RUNTIME)
public @interface Sequence {
    String name();
}
