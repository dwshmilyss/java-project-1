package com.yiban.javaBase.dev.concurrent;

import java.util.concurrent.CountDownLatch;

/**
 * @auther WEI.DUAN
 * @date 2019/6/8
 * @website http://blog.csdn.net/dwshmilyss
 */
public class CountDownLatchDemo {
    public static void main(String[] args) {
        test();
    }

    //模拟并发压测的时候可以用这种模式
    public static void test() {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        for (int i = 0; i < 50; i++) {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        countDownLatch.await();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    //TODO 下面的代码是具体的业务代码
                    for (int j = 0; j < 10; j++) {
                        System.out.println(Thread.currentThread().getName() + "  " + j);
                    }
                }
            });
            thread.start();
        }
        countDownLatch.countDown();
        synchronized (CountDownLatchDemo.class) {
            try {
                CountDownLatchDemo.class.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    public static void test1() {
        //设置等待2个线程执行结束
        CountDownLatch countDownLatch = new CountDownLatch(2);
        new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println(Thread.currentThread().getName() + " start .... ");
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println(Thread.currentThread().getName() + " end .... ");
                //休眠结束，等待线程数-1
                countDownLatch.countDown();
            }
        }).start();


        new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println(Thread.currentThread().getName() + " start.... ");
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println(Thread.currentThread().getName() + " end.... ");
                //休眠结束，等待线程数-1
                countDownLatch.countDown();
            }
        }).start();


        //main线程阻塞 直到上面两个子线程都执行完毕才能继续
        try {
            System.out.println("等待2个子线程执行完毕...");
            countDownLatch.await();
            System.out.println("2个子线程已经执行完毕");
            System.out.println("继续执行主线程");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}