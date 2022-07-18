package com.yiban.javaBase.dev.GOF.behavior.observer;

import java.util.Observable;

/**
 * MyPerson是被观察者
 * setXXX();数据改变，通过notifyObservers();发送信号通知观察者。
 *
 * @auther WEI.DUAN
 * @date 2017/4/20
 * @website http://blog.csdn.net/dwshmilyss
 */
public class MyPerson extends Observable {
    private String name;
    private int age;
    private String sex;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        setChanged();
        notifyObservers();
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
        setChanged();
        notifyObservers();
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
        setChanged();
        notifyObservers();
    }

    @Override
    public String toString() {
        return "MyPerson [name=" + name + ", age=" + age + ", sex=" + sex + "]";
    }
}
