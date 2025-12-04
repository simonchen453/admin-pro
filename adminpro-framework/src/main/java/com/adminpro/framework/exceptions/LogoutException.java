package com.adminpro.framework.exceptions;

public class LogoutException extends RuntimeException {

    /**
     *
     */
    private static final long serialVersionUID = -8910595672166978962L;

    public LogoutException(String message, Throwable cause) {
        super(message, cause);
    }

    public LogoutException(String message) {
        super(message);
    }

    public LogoutException(Throwable cause) {
        super(cause);
    }

}
