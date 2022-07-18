package com.yiban.javaBase.dev.GOF.structure.decorator.demo1;

/**
 * @auther WEI.DUAN
 * @date 2018/7/24
 * @website http://blog.csdn.net/dwshmilyss
 */
public class Test {
    public static void main(String[] args) {
        Component component = new ConcreteDecorator(new ConcreteComponent());
        component.show();
    }
}
