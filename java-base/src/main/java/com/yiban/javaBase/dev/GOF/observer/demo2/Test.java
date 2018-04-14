package com.yiban.javaBase.dev.GOF.observer.demo2;

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
        Subject subject = new Subject();
        subject.register(obs);
        subject.setData("one");
        subject.notifyObservers();
    }
}
