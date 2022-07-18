package com.yiban.javaBase.dev.GOF.behavior.observer.demo1;

import java.util.Observable;
import java.util.Observer;

/**
 * 观察者（Observer）：编写具体的观察者类实现观察者接口，通过参数传递主题对象获取更新的数据。update()方法主要用于“拉”数据及处理过程
 *
 * @auther WEI.DUAN
 * @date 2017/4/20
 * @website http://blog.csdn.net/dwshmilyss
 */
public class Observer1 implements Observer {
    @Override
    public void update(Observable o, Object arg) {
        if (o instanceof Subject) {
            Subject subject = (Subject) o;
            System.out.println(this.getClass().getName() + ": 观察到数据正在更新为：" + subject.getData());
        }
    }
}
