package com.yiban.javaBase.dev.GOF.behavior.observer.demo2;

/**
 * 主题子类定义被监控数据
 *
 * @auther WEI.DUAN
 * @date 2017/4/20
 * @website http://blog.csdn.net/dwshmilyss
 */
public class Subject extends AbstractSubject {
    //被监控的数据
    private String data;

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
