package com.yiban.javaBase.dev.GOF.behavior.observer.demo2;

/**
 * 观察者对象（Observer）“拉”数据得到数据响应
 *
 * @auther WEI.DUAN
 * @date 2017/4/20
 * @website http://blog.csdn.net/dwshmilyss
 */
public class Observer1 implements IObserver {
    @Override
    public void refresh(ISubject obj) {
        Subject subject = (Subject) obj;
        System.out.println("Observer1观察到数据正在更新为:" + subject.getData());
    }
}
