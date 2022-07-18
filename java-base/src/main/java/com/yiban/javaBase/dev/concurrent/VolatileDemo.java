package com.yiban.javaBase.dev.concurrent;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Administrator on 2018/1/15 0015.
 */
public class VolatileDemo {
//    volatile int a = 1;
//    volatile boolean ready;

    int a = 1;
    volatile boolean ready;

    public volatile AtomicInteger count = new AtomicInteger(0);

    public static void main(String[] args) throws InterruptedException {
//        VolatileDemo t = new VolatileDemo();
//        t.new PrintA().start();
//        //下面两行如果不加volatile的话，执行的先后顺序是不可预测的。并且下面两行都是原子操作，但是这两行作为一个整体的话就不是一个原子操作。
//        t.a = 48; //这是一个原子操作，但是其结果不一定具有可见性。加上volatile后就具备了可见性。
//        t.ready = true;//同理

        final VolatileDemo demo = new VolatileDemo();
        Executor executor = Executors.newFixedThreadPool(10);
        for (int i = 0; i < 1000; i++) {
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    demo.count.getAndIncrement();
                }
            });
        }
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("final count value:" + demo.count);

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
