package com.yiban.javaBase.dev.proxy.static_proxy;

/**
 * 4：在使用时，首先创建业务实现类对象，然后把业务实现类对象作构造参数创建一个代理类对象，最后通过代理类对象进行业务方法的调用。
 * @auther WEI.DUAN
 * @date 2018/9/18
 * @website http://blog.csdn.net/dwshmilyss
 */
public class Test {
    public static void main(String[] args) {
        CountImpl countImpl = new CountImpl();
        CountProxy countProxy = new CountProxy(countImpl);
        countProxy.updateCount();
        countProxy.queryCount();
    }
}
