package com.adminpro.core.exceptions;

public class NotAcceptableException extends BaseRuntimeException {

    /**
     *
     */
    private static final long serialVersionUID = -2405216343328739736L;

    public NotAcceptableException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotAcceptableException(String message) {
        super(message);
    }

    public NotAcceptableException(Throwable cause) {
        super(cause);
    }

}
