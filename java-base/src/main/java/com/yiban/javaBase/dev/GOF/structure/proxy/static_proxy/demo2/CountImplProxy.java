package com.yiban.javaBase.dev.GOF.structure.proxy.static_proxy.demo2;

/**
 * 静态代理类
 *
 * @auther WEI.DUAN
 * @date 2017/5/8
 * @website http://blog.csdn.net/dwshmilyss
 */
public class CountImplProxy implements ICount {
    private CountImpl countImpl;

    public CountImplProxy(CountImpl countImpl) {
        this.countImpl = countImpl;
    }

    @Override
    public void query() {
        System.out.println("方法执行前执行一些操作");
        countImpl.query();
        System.out.println("方法执行后执行一些操作");
    }
}
