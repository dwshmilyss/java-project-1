package com.yiban.javaBase.dev.GOF.behavior.observer;

import java.util.Observable;
import java.util.Observer;

/**
 * MyObserver是观察者
 *
 * @auther WEI.DUAN
 * @date 2017/4/20
 * @website http://blog.csdn.net/dwshmilyss
 */
public class MyObserver implements Observer {
    private int id;
    private MyPerson myPerson;

    public MyObserver(int id) {
        System.out.println("我是观察者---->" + id);
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public MyPerson getMyPerson() {
        return myPerson;
    }

    public void setMyPerson(MyPerson myPerson) {
        this.myPerson = myPerson;
    }

    @Override
    public void update(Observable observable, Object data) {
        System.out.println("观察者---->" + id + "得到更新");
        this.myPerson = (MyPerson) observable;
        System.out.println(((MyPerson) observable).toString());
    }

}
