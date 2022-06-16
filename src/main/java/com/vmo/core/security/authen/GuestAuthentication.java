package com.vmo.core.security.authen;

import org.springframework.security.authentication.AbstractAuthenticationToken;

public class GuestAuthentication extends AbstractAuthenticationToken {
    public GuestAuthentication() {
        super(null);
    }

    @Override
    public String getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return null;
    }
}
