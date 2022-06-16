package com.vmo.core.security.exception;

import org.springframework.security.core.AuthenticationException;

public class AuthenticationFailException extends AuthenticationException {
    public AuthenticationFailException(String message, Throwable t) {
        super(message, t);
    }

    public AuthenticationFailException(String message) {
        super(message);
    }
}
