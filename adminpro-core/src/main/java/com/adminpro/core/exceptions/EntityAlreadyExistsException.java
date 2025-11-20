package com.adminpro.core.exceptions;

public class EntityAlreadyExistsException extends BaseRuntimeException {

    /**
     *
     */
    private static final long serialVersionUID = 6165625838683522855L;

    public EntityAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }

    public EntityAlreadyExistsException(String message) {
        super(message);
    }

    public EntityAlreadyExistsException(Throwable cause) {
        super(cause);
    }

}
