package com.linkflow.analysis.presto.security;

import com.facebook.presto.spi.security.SystemAccessControl;
import com.facebook.presto.spi.security.SystemAccessControlFactory;

import java.util.Map;

public class SimpleSystemAccessControlFactory implements SystemAccessControlFactory {
    @Override
    public String getName() {
        return "custom-access-control";
    }

    @Override
    public SystemAccessControl create(Map<String, String> config) {
        SimpleSystemAccessControl simpleSystemAccessControl = new SimpleSystemAccessControl(config);
        return simpleSystemAccessControl;
    }
}
