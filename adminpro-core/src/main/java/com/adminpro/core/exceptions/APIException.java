package com.adminpro.core.exceptions;

import java.util.function.Supplier;

/**
 * @author simon
 */
public class APIException extends Exception implements Supplier<APIException> {

    public APIException(String msg) {
        super(msg);
    }

    public APIException(Exception e) {
        super(e);
    }

    @Override
    public APIException get() {
        return this;
    }
}
