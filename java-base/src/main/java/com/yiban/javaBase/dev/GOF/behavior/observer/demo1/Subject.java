package com.yiban.javaBase.dev.GOF.behavior.observer.demo1;

import java.util.Observable;

/**
 * 主题（Subject）：Observable类派生出来的子类，只需要定义各被监控的数据及getter()、setter()方法，getter方法主要用于具体观察者“拉”数据，setter方法主要用于更新、设置changed变量及通知各具体观察者进行数据响应。
 *
 * @auther WEI.DUAN
 * @date 2017/4/20
 * @website http://blog.csdn.net/dwshmilyss
 */
public class Subject extends Observable {
    private String data;

    public String getData() {
        return data;
    }

    public void setData(String data) {
        //更新数据
        this.data = data;
        //置更新数据标志
        setChanged();
        //通知各个具体的观察者，这里有推数据的作用
        notifyObservers(null);
    }
}
