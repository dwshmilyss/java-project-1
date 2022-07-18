package com.yiban.javaBase.dev.GOF.structure.proxy.dynamic_porxy.demo1;


import java.lang.reflect.Proxy;

/**
 * 测试静态代理
 *
 * @auther WEI.DUAN
 * @date 2017/5/8
 * @website http://blog.csdn.net/dwshmilyss
 */
public class Test {
    public static void main(String[] args) {
        /**
         * 测试JDK的动态代理
         */
        CountImplDynamicProxyByJDK proxyInstance = new CountImplDynamicProxyByJDK();
        CountImpl countImpl = new CountImpl();
        //下面两种生成动态代理对象的方式等价
//        ICount countProxy = (ICount) proxyInstance.bind(new CountImpl());
        ICount countProxy = (ICount) Proxy.newProxyInstance(ICount.class.getClassLoader(), new Class[]{ICount.class}, new CountImplDynamicProxyByJDK(countImpl));
        countProxy.query();

        System.out.println(" ============================== ");

        /**
         * 测试CGlib的动态代理
         */
        CountImplDynamicProxyByCGLib cgLib = new CountImplDynamicProxyByCGLib();
        //生成代理对象
        CountClass countClass = (CountClass) cgLib.getInstance(new CountClass());
        //调用目标的方法
        countClass.query();

    }
}
