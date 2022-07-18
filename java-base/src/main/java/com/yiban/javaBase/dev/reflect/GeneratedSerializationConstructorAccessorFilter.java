package com.yiban.javaBase.dev.reflect;

import sun.jvm.hotspot.oops.InstanceKlass;
import sun.jvm.hotspot.tools.jcore.ClassFilter;

public class GeneratedSerializationConstructorAccessorFilter implements ClassFilter {
    @Override
    public boolean canInclude(InstanceKlass instanceKlass) {
        String klassName = instanceKlass.getName().asString();
        return klassName.startsWith("sun/reflect/GeneratedSerializationConstructorAccessor");
    }
}
