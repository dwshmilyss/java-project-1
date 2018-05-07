package com.yiban.javaBase.dev.JVM.clinit;

/**
 * sub class
 *
 * @auther WEI.DUAN
 * @date 2018/4/17
 * @website http://blog.csdn.net/dwshmilyss
 */
public class SubClass extends SuperClass {
    static
    {
        System.out.println("SubClass init");
    }
}
