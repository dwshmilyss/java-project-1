package com.yiban.javaBase.dev.GOF.structure.decorator.demo1;

/**
 * ConcreteComponent
 *
 * @auther WEI.DUAN
 * @date 2018/7/24
 * @website http://blog.csdn.net/dwshmilyss
 */
public class ConcreteComponent implements Component {
    @Override
    public void show() {
        System.out.println("被装饰类");
    }
}
