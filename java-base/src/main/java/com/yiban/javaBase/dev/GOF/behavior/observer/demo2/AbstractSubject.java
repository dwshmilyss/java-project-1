package com.yiban.javaBase.dev.GOF.behavior.observer.demo2;

import java.util.ArrayList;

/**
 * 主题抽象类
 *
 * @auther WEI.DUAN
 * @date 2017/4/20
 * @website http://blog.csdn.net/dwshmilyss
 */
public class AbstractSubject implements ISubject {
    private ArrayList<IObserver> observers = new ArrayList<IObserver>();

    @Override
    public void register(IObserver obs) {
        observers.add(obs);
    }

    @Override
    public void unregister(IObserver obs) {
        observers.remove(obs);
    }

    @Override
    public void notifyObservers() {
        for (int i = 0; i < observers.size(); i++) {
            observers.get(i).refresh(this);
        }
    }
}
