package com.vmo.core.security.exception;

import org.springframework.security.core.AuthenticationException;

public class TokenExpiredException extends AuthenticationException {
    private String token;

    public TokenExpiredException(String message, String token, Throwable t) {
        super(message, t);
        this.token = token;
    }

    public String getToken() {
        return token;
    }
}
