package com.adminpro.tools.lock;

/**
 * @author simon
 * @date 2019/4/3
 */
public class CacheLockException extends RuntimeException {

    public CacheLockException(String message, Throwable cause) {
        super(message, cause);
    }

    public CacheLockException(String message) {
        super(message);
    }

    public CacheLockException(Throwable cause) {
        super(cause);
    }
}
