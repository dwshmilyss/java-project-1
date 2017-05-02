package com.yiban.javaBase.dev.collections.queue;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by duanwei on 2017/3/13.Ø
 * 利用LinkedBlockingQueue实现主线程和子线程的交替执行
 * 因为队列的take 如果取不到数据会阻塞
 */
public class BlockingQueueDemo {

    public static void main(String[] args) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        final Business business = new Business();
        executorService.execute(new Runnable() {
            public void run() {
                for (int i = 0; i < 50; i++) {
                    business.sub();
                }
            }
        });
        for (int i = 0; i < 50; i++) {
            business.main();
        }

    }


    static class Business {
        BlockingQueue mainQueue = new LinkedBlockingQueue();
        BlockingQueue subQueue = new LinkedBlockingQueue();

        //这里是匿名构造方法，只要new一个对象都会调用这个匿名构造方法，它与静态块不同，静态块只会执行一次，
        //在类第一次加载到JVM的时候执行
        // 这里主要是让main线程首先put一个，就有东西可以取，如果不加这个匿名构造方法put一个的话程序就死锁了
        {
            try {
                mainQueue.put(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        public void sub() {
            try {
                mainQueue.take();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            for (int i = 0; i < 10; i++) {
                System.out.println(Thread.currentThread().getName() + " : " + i);
            }
            try {
                subQueue.put(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        public void main() {
            try {
                subQueue.take();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            for (int i = 0; i < 5; i++) {
                System.out.println(Thread.currentThread().getName() + " : " + i);
            }
            try {
                mainQueue.put(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }

    }
}



