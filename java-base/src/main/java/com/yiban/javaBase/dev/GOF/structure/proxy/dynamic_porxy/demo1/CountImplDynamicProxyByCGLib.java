package com.yiban.javaBase.dev.GOF.structure.proxy.dynamic_porxy.demo1;

import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

/**
 * CGLib 动态代理
 * 这里不是实现 InvocationHandler 接口了  而是实现反射包里的 MethodInterceptor
 *
 * @auther WEI.DUAN
 * @date 2017/5/8
 * @website http://blog.csdn.net/dwshmilyss
 */
public class CountImplDynamicProxyByCGLib implements MethodInterceptor {
    //被代理对象
    private Object target;

    /**
     * 绑定委托对象并返回一个代理类
     *
     * @param target
     * @return
     */
    public Object getInstance(Object target) {
        this.target = target;
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(this.target.getClass());
        // 回调方法
        enhancer.setCallback(this);
        //创建代理对象
        return enhancer.create();
    }

    @Override
    public Object intercept(Object target, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
        System.out.println("开始事务");
        methodProxy.invokeSuper(target, args);
        System.out.println("结束事务");
        return null;
    }
}
