package com.yiban.javaBase.dev.GOF.structure.proxy.static_proxy.demo1;

import java.lang.reflect.Proxy;

/**
 * 客户端类
 *
 * @auther WEI.DUAN
 * @create 2017/4/20
 * @blog http://blog.csdn.net/dwshmilyss
 */
public class Client {
    public static void main(String[] args) {
        dynamiccProxyTest();
    }

    public static void staticProxyTest() {
        //static proxy
        IShop xiaoMing = new XiaoMing();
        //创建代购者并将xiaoMing作为构造函数传
        IShop proxyBuyer = new StaticProxyBuyer(xiaoMing);
        //这里调用代理类的方法时，同步调用了被代理类(XiaoMing)的bug()
        proxyBuyer.buy();
    }

    public static void dynamiccProxyTest() {
        //构造被代理类小明
        IShop xiaoMing = new XiaoMing();
        //构造一个动态代理
        DynamicProxyBuyer dynamicProxy = new DynamicProxyBuyer(xiaoMing);
        //获取被代理类小明的classLoader
        ClassLoader classLoader = xiaoMing.getClass().getClassLoader();
        //动态构建代购的人
        IShop proxyBuyer = (IShop) Proxy.newProxyInstance(classLoader, new Class[]{IShop.class}, dynamicProxy);
        //进行代购
        proxyBuyer.buy();
    }
}
