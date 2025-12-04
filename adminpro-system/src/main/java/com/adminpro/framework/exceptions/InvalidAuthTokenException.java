package com.adminpro.framework.exceptions;

import org.springframework.security.core.AuthenticationException;

/**
 * @author simon
 */
public class InvalidAuthTokenException extends AuthenticationException {

    public InvalidAuthTokenException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidAuthTokenException(String message) {
        super(message);
    }

}
