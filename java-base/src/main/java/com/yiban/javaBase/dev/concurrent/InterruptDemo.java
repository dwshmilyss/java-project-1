package com.yiban.javaBase.dev.concurrent;

import java.util.concurrent.locks.AbstractQueuedSynchronizer;
import java.util.concurrent.locks.LockSupport;

/**
 * interrupt demo
 *
 * @auther WEI.DUAN
 * @create 2017/10/28
 * @blog http://blog.csdn.net/dwshmilyss
 */
public class InterruptDemo {

    public static void main(String[] args) throws InterruptedException {
        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                boolean flag;
                System.out.println("子线程开始执行");
                flag = Thread.currentThread().isInterrupted();
                System.out.println("park before flag = " + flag);
                System.out.println("子线程开始阻塞");
                LockSupport.park(this);//子线程阻塞
                flag = Thread.currentThread().isInterrupted();
                System.out.println("park after flag = " + flag);
                System.out.println("子线程结束执行");
            }
        });

        t1.start();
        Thread.sleep(5000); // 等待1秒

        System.out.println("主线程中断子线程");
        t1.interrupt();//中断子线程的阻塞，让子线程继续执行

        t1.join();// 等待子线程执行完毕
        System.out.println("主线程结束");
    }

}
