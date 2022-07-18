package com.linkflow.analysis.presto.security;


import com.facebook.presto.spi.Plugin;
import com.facebook.presto.spi.security.PasswordAuthenticatorFactory;
import com.facebook.presto.spi.security.SystemAccessControlFactory;
import com.google.common.collect.ImmutableList;

public class LfSecurityPlugin implements Plugin {
    @Override
    public Iterable<SystemAccessControlFactory> getSystemAccessControlFactories() {
        return ImmutableList.of(new SimpleSystemAccessControlFactory());
    }

    @Override
    public Iterable<PasswordAuthenticatorFactory> getPasswordAuthenticatorFactories() {
        return ImmutableList.of(new SimplePasswordAuthenticatorFactory());
    }
}
