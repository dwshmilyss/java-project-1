package com.yiban.javaBase.dev.GOF.structure.decorator.demo1;

/**
 * 装饰类
 * 装饰类要持有被装饰类的实例（为了扩展 也就是要持有统一接口的实例）
 * @auther WEI.DUAN
 * @date 2018/7/24
 * @website http://blog.csdn.net/dwshmilyss
 */
public class Decorator implements Component {
    public Component component;

    public Decorator(Component component) {
        this.component = component;
    }

    @Override
    public void show() {
        this.component.show();
    }
}
