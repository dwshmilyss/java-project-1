package com.yiban.javaBase.dev.GOF.behavior.observer.demo2;

/**
 * 测试类
 *
 * @auther WEI.DUAN
 * @date 2017/4/20
 * @website http://blog.csdn.net/dwshmilyss
 */
public class Test {
    public static void main(String[] args) {
        IObserver obs = new Observer();
        IObserver obs1 = new Observer1();
        IObserver obs2 = new Observer2();
        Subject subject = new Subject();
        subject.register(obs);
        subject.register(obs1);
        subject.register(obs2);
        subject.setData("one");
        subject.notifyObservers();
    }
}
