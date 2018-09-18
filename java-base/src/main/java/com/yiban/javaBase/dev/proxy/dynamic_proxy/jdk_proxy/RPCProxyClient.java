package com.yiban.javaBase.dev.proxy.dynamic_proxy.jdk_proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * RPC客户端代理类
 *
 * @auther WEI.DUAN
 * @date 2018/7/18
 * @website http://blog.csdn.net/dwshmilyss
 */
public class RPCProxyClient implements InvocationHandler {
    // 目标对象
    private Object target;

    public RPCProxyClient(Object object) {
        target = object;
    }

    /**
     * 得到被代理对象
     * @return
     */
    public Object getProxy() {
        return Proxy.newProxyInstance(target.getClass().getClassLoader(), target.getClass().getInterfaces(), this);
    }

    /**
     * 调用此方法执行
     * @param proxy
     * @param method
     * @param args
     * @return
     * @throws Throwable
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        //结果参数
        Object result = method.invoke(target,args);
        //TODO 执行业务逻辑
        return result;
    }
}
