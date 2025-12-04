package com.adminpro.core.exceptions;

public class BaseRuntimeException extends RuntimeException {

    /**
     *
     */
    private static final long serialVersionUID = -8910595672166978962L;

    public BaseRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public BaseRuntimeException(String message) {
        super(message);
    }

    public BaseRuntimeException(Throwable cause) {
        super(cause);
    }

}
