package com.yiban.javaBase.dev.GOF.proxy.static_proxy.demo1;

/**
 * 客户端类
 *
 * @auther WEI.DUAN
 * @create 2017/4/20
 * @blog http://blog.csdn.net/dwshmilyss
 */
public class Client {
    public static void main(String[] args) {
        IShop xiaoMing = new XiaoMing();
        //创建代购者并将xiaoMing作为构造函数传
        IShop proxyBuyer = new ProxyBuyer(xiaoMing);
        //这里调用代理类的方法时，同步调用了被代理类(XiaoMing)的bug()
        proxyBuyer.buy();
    }
}
