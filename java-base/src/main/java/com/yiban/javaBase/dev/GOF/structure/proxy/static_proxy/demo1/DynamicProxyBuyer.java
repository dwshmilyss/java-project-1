package com.yiban.javaBase.dev.GOF.structure.proxy.static_proxy.demo1;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * Created by 10000347 on 2017/4/20.
 */
public class DynamicProxyBuyer implements InvocationHandler {
    private final Object mTarget;

    public DynamicProxyBuyer(Object target) {
        mTarget = target;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        //可以在调用目标方法前做一些事情 例如 日志，事务控制
        System.out.println("DynamicProxyBuyer.invoke start.....");

        //调用被代理对象的方法
        Object obj = method.invoke(mTarget, args);

        //可以在调用目标方法后做一些事情 例如 日志，事务控制
        System.out.println("DynamicProxyBuyer.invoke end.....");
        return obj;
    }
}
