package com.yiban.javaBase.dev.concurrent.lock.clh;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @auther WEI.DUAN
 * @date 2018/10/23
 * @website http://blog.csdn.net/dwshmilyss
 */
public class Test {
    public static void main(String[] args) {
        ExecutorService executers = Executors.newFixedThreadPool(2);
        CLHLock clhLock = new CLHLock();
        ReentrantLock lock = new ReentrantLock();
        for (int i = 0; i < 2; i++) {
            executers.execute(new Runnable() {
                @Override
                public void run() {
                    clhLock.lock();
                    for (int j = 0; j < 10; ) {
                        System.out.println("name = " + Thread.currentThread().getName() + "开始执行: j = " + j++);
                    }
//                    try {
//                        Thread.sleep(5000);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
                    clhLock.unlock();
                }
            });
        }
        executers.shutdownNow();
    }
}
