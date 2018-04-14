package com.yiban.javaBase.dev.concurrent;

/**
 * Created by Administrator on 2018/1/15 0015.
 */
public class VolatileDemo {
//    volatile int a = 1;
//    volatile boolean ready;

    int a = 1;
    volatile boolean ready;

    public static void main(String[] args) throws InterruptedException {
        VolatileDemo t = new VolatileDemo();
        t.new PrintA().start();
        //下面两行如果不加volatile的话，执行的先后顺序是不可预测的。并且下面两行都是原子操作，但是这两行作为一个整体的话就不是一个原子操作。
        t.a = 48; //这是一个原子操作，但是其结果不一定具有可见性。加上volatile后就具备了可见性。
        t.ready = true;//同理
    }

    public class PrintA extends Thread {
        @Override
        public void run() {
            while (!ready) {
                Thread.yield();
            }
            System.out.println(a);
        }
    }
}
