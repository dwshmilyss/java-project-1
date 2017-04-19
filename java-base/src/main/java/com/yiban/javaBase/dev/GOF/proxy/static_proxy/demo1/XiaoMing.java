package com.yiban.javaBase.dev.GOF.proxy.static_proxy.demo1;

import org.apache.log4j.Logger;

/**
 * 真实主题类（RealSubject）
 * 例子中就是小明，他需要实现IShop接口提供的 buy()方法
 *
 * @auther WEI.DUAN
 * @create 2017/4/20
 * @blog http://blog.csdn.net/dwshmilyss
 */
public class XiaoMing implements IShop{
    Logger logger = Logger.getLogger(XiaoMing.class);
    public void buy() {
        System.out.println("小明进行购买");
    }
}
