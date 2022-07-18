package com.linkflow.analysis.presto.security;

import com.facebook.airlift.log.Logger;
import com.facebook.presto.spi.security.AccessDeniedException;
import com.facebook.presto.spi.security.PasswordAuthenticator;

import java.security.Principal;
import java.util.Map;

public class SimplePasswordAuthenticator implements PasswordAuthenticator {
    private final Logger logger = Logger.get(SimplePasswordAuthenticator.class);
    private Map<String, String> config;

    public SimplePasswordAuthenticator(Map<String, String> config) {
        this.config = config;
    }

    @Override
    public Principal createAuthenticatedPrincipal(String user, String password) {
        logger.info("createAuthenticatedPrincipal -> user : " + user + ",password : " + password);
        Map<String, String> userInfo = (SecurityUtil.getInstance()).userInfo;
        if (userInfo.containsKey(user) && userInfo.get(user).equals(password)) {
            logger.info("validate user success");
            return new SimplePrincipal(user);
        }
        throw new AccessDeniedException("password or username are not right!!!");
    }
}
