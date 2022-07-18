package com.yiban.javaBase.dev.GOF.structure.proxy.dynamic_porxy.demo1;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * JDK 动态代理
 * 这里不是实现Count接口了  而是实现反射包里的InvocationHandler
 *
 * @auther WEI.DUAN
 * @date 2017/5/8
 * @website http://blog.csdn.net/dwshmilyss
 */
public class CountImplDynamicProxyByJDK implements InvocationHandler {
    //被代理对象
    private Object target;

    public CountImplDynamicProxyByJDK() {
    }

    public CountImplDynamicProxyByJDK(Object target) {
        this.target = target;
    }

    /**
     * 绑定委托对象并返回一个代理类
     *
     * @param target
     * @return
     */
    public Object bind(Object target) {
        this.target = target;
        return Proxy.newProxyInstance(target.getClass().getClassLoader(), target.getClass().getInterfaces(), this);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object result = null;
        System.out.println("开始事务");
        result = method.invoke(target, args);
        System.out.println("结束事务");
        //返回代理类
        return result;
    }
}
