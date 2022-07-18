package com.linkflow.analysis.presto.security;

import java.security.Principal;

public class SimplePrincipal implements Principal {

    private String name;

    public SimplePrincipal() {
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return "SimplePrincipal";
    }

    public SimplePrincipal(String name) {
        this.name = name;
    }
}
