package com.yiban.javaBase.dev.GOF.structure.proxy.static_proxy.demo2;

/**
 * 测试静态代理
 *
 * @auther WEI.DUAN
 * @date 2017/5/8
 * @website http://blog.csdn.net/dwshmilyss
 */
public class Test {
    public static void main(String[] args) {
        CountImpl countImpl = new CountImpl();
        //静态代理类持有被代理类的对象引用，实例化的时候作为构造函数的参数传进去
        CountImplProxy countImplProxy = new CountImplProxy(countImpl);
        //调用代理类的方法
        countImplProxy.query();

    }
}
