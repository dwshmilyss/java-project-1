package com.yiban.javaBase.dev.concurrent.lock.clh;

/**
 * @auther WEI.DUAN
 * @date 2018/10/23
 * @website http://blog.csdn.net/dwshmilyss
 */
public class QNode {
    public boolean locked;

    public QNode() {
    }

    public QNode(boolean locked) {

        this.locked = locked;
    }
}
