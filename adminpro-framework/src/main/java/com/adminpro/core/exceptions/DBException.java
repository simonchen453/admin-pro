package com.adminpro.core.exceptions;

public class DBException extends RuntimeException {

    /**
     *
     */
    private static final long serialVersionUID = -8910595672166978962L;

    public DBException(String message, Throwable cause) {
        super(message, cause);
    }

    public DBException(String message) {
        super(message);
    }

    public DBException(Throwable cause) {
        super(cause);
    }

}
