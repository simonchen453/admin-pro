package com.adminpro.core.exceptions;

/**
 * @author simon
 */
public class SysException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public SysException() {
        super();
    }

    public SysException(Exception x) {
        super(x);
    }

    public SysException(String message) {
        super(message);
    }

    public SysException(String message, Exception x) {
        super(message, x);
    }
}
