package com.adminpro.core.exceptions;

/**
 * @author simon
 */
public class BizException extends Exception {
    private static final long serialVersionUID = 1L;

    public BizException() {
        super();
    }

    public BizException(String message) {
        super(message);
    }

    public BizException(String message, Exception x) {
        super(message, x);
    }
}
