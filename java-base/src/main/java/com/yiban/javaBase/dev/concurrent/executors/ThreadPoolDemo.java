package com.yiban.javaBase.dev.concurrent.executors;

import java.util.concurrent.*;

/**
 * 测试一些线程池
 *
 * @auther WEI.DUAN
 * @date 2019-02-26
 * @website http://blog.csdn.net/dwshmilyss
 */
public class ThreadPoolDemo {
    public static void main(String[] args) {
        ExecutorService cachedThreadPool = new ThreadPoolExecutor(0, Integer.MAX_VALUE, 6L, TimeUnit.SECONDS, new SynchronousQueue<Runnable>());
        for (int i = 50; i <= 100; i++) {
            final int index = i;
            try {
                Thread.sleep(index * 100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            cachedThreadPool.execute(new Runnable() {
                @Override
                public void run() {
                    System.out.println(index + " 当前线程 ： " + Thread.currentThread().getName());
                }
            });
        }
    }
}
