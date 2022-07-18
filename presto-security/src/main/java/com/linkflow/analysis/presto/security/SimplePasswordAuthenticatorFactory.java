package com.linkflow.analysis.presto.security;

import com.facebook.presto.spi.security.PasswordAuthenticator;
import com.facebook.presto.spi.security.PasswordAuthenticatorFactory;

import java.util.Map;

public class SimplePasswordAuthenticatorFactory implements PasswordAuthenticatorFactory {
    @Override
    public String getName() {
        return "custom-password-control";
    }

    @Override
    public PasswordAuthenticator create(Map<String, String> config) {
        PasswordAuthenticator auth = new SimplePasswordAuthenticator(config);
        return auth;
    }
}
