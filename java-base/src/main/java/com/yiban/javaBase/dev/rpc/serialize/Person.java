package com.yiban.javaBase.dev.rpc.serialize;

import java.io.Serializable;

/**
 * entity
 *
 * @auther WEI.DUAN
 * @date 2018/9/18
 * @website http://blog.csdn.net/dwshmilyss
 */
public class Person implements Serializable {
    private static final long serialVersionUID = 5050322404095136870L;
    private String name;
    private int age;

    public Person(String name, int age) {
        this.name = name;
        this.age = age;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return this.age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    @Override
    public String toString() {
        return "name is : " + name + ",age is : " + age;
    }
}
