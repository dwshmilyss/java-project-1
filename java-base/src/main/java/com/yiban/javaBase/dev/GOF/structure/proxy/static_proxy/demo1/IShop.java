package com.yiban.javaBase.dev.GOF.structure.proxy.static_proxy.demo1;

/**
 * Created by EiT on 2017/4/19.
 * 抽象主题类（Subject）
 * 抽象主题类具有真实主题类和代理的共同接口方法，在这里共同的方法就是购买：
 */
public interface IShop {
    void buy();
}
