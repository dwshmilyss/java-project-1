package com.yiban.javaBase.dev.GOF.structure.proxy.static_proxy.demo1;

/**
 * 代理类（Proxy）
 * 小明的朋友，也就是代理类同样也要实现IShop接口，并且要持有被代理者，在buy()方法中调用了被代理者的buy()方法
 *
 * @auther WEI.DUAN
 * @create 2017/4/20
 * @blog http://blog.csdn.net/dwshmilyss
 */
public class StaticProxyBuyer implements IShop {
    private IShop mShop;

    public StaticProxyBuyer(IShop shop) {
        mShop = shop;
    }

    public void buy() {
        mShop.buy();
    }
}
