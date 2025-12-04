package com.adminpro.core.exceptions;

public class EntityNotExistsException extends BaseRuntimeException {

    /**
     *
     */
    private static final long serialVersionUID = 6165625838683522855L;

    public EntityNotExistsException(String message, Throwable cause) {
        super(message, cause);
    }

    public EntityNotExistsException(String message) {
        super(message);
    }

    public EntityNotExistsException(Throwable cause) {
        super(cause);
    }

}
