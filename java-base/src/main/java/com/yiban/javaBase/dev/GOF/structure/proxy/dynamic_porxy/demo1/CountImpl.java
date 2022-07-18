package com.yiban.javaBase.dev.GOF.structure.proxy.dynamic_porxy.demo1;

/**
 * 被代理类
 *
 * @auther WEI.DUAN
 * @date 2017/5/8
 * @website http://blog.csdn.net/dwshmilyss
 */
public class CountImpl implements ICount {
    @Override
    public void query() {
        System.out.println("实现query方法");
    }
}
