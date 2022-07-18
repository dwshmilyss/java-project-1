package com.yiban.javaBase.dev.GOF.structure.decorator.demo1;

/**
 * 具体的装饰器类  通常都是增加被装饰类的功能
 *
 * @auther WEI.DUAN
 * @date 2018/7/24
 * @website http://blog.csdn.net/dwshmilyss
 */
public class ConcreteDecorator extends Decorator {

    public ConcreteDecorator(Component component) {
        super(component);
    }

    @Override
    public void show() {
        System.out.println("装饰器类，通常都是增加被装饰类的功能，这里只是简单打印了一句话，当然可以有多个装饰器类");
        this.component.show();
    }
}
