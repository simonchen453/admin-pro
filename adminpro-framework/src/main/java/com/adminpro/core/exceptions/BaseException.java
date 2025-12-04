package com.adminpro.core.exceptions;


public class BaseException extends Exception {

    /**
     *
     */
    private static final long serialVersionUID = -6556339507933203571L;

    public BaseException(String message, Throwable cause) {
        super(message, cause);
    }

    public BaseException(String message) {
        super(message);
    }

    public BaseException(Throwable cause) {
        super(cause);
    }

}
