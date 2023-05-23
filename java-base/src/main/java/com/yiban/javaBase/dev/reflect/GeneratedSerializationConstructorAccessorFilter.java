package com.yiban.javaBase.dev.reflect;

import sun.jvm.hotspot.oops.InstanceKlass;
import sun.jvm.hotspot.tools.jcore.ClassFilter;

/**
 * @Description: 这个类编译需要sa-jdi.jar
 * 这个包在$JAVA_HOME/lib下面
 * @Date: 2023/5/23
 * @Auther: David.duan
 * @Param null:
 **/
public class GeneratedSerializationConstructorAccessorFilter implements ClassFilter {
    @Override
    public boolean canInclude(InstanceKlass instanceKlass) {
        String klassName = instanceKlass.getName().asString();
        return klassName.startsWith("sun/reflect/GeneratedSerializationConstructorAccessor");
    }
}
