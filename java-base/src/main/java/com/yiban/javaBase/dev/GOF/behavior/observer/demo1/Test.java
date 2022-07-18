package com.yiban.javaBase.dev.GOF.behavior.observer.demo1;

import java.util.Observer;

/**
 * run test
 *
 * @auther WEI.DUAN
 * @date 2017/4/20
 * @website http://blog.csdn.net/dwshmilyss
 */
public class Test {
    public static void main(String[] args) {
        Observer observer1 = new Observer1();
        Observer observer2 = new Observer2();
        Subject subject = new Subject();
        //主题subject要知道哪些观察者对其进行观察，即将观察者add到主题的观察者列表中
        subject.addObserver(observer1);
        subject.addObserver(observer2);
        //可以移除某个观察者
        subject.deleteObserver(observer1);
        subject.setData("One");
    }
}
