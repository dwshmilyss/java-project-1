package com.yiban.javaBase.dev.GOF.behavior.observer.demo2;

/**
 * 观察者接口
 */
public interface IObserver {
    //传入的参数对象可以间接获取变化后的主题数据
    public void refresh(ISubject subject);
}
