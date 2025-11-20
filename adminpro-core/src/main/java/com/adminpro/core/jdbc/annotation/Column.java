package com.adminpro.core.jdbc.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target(FIELD)
@Retention(RUNTIME)
public @interface Column {
    enum Type {INT, LONG, DOUBLE, FLOAT, STRING, DATE, TIME, DATETIME, BLOB, BOOLEAN, BIGDECIMAL}

    String name();

    String field() default "";

    Type type() default Type.STRING;
}
