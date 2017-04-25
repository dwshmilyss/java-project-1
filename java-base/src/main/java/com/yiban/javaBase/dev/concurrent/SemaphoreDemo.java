package com.yiban.javaBase.dev.concurrent;


import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * 对比 Semaphore，CountDownLatch，Thread.join()的使用场景及异同
 * CountDownLatch与join的区别：
 * 调用thread.join() 方法必须等thread 执行完毕，当前线程才能继续往下执行，
 * 而CountDownLatch通过计数器提供了更灵活的控制，只要检测到计数器为0当前线程就可以往下执行而不用管相应的thread是否执行完毕。
 * @auther WEI.DUAN
 * @date 2017/4/25
 * @website http://blog.csdn.net/dwshmilyss
 */
public class SemaphoreDemo {
    public static void main(String[] args) {
//        useThreadJoin();
        useCountDownLatch();

//        ExecutorService executor = Executors.newCachedThreadPool();
//        CountDownLatch latch = new CountDownLatch(3);
//
//        Worker w1 = new Worker(latch, "张三");
//        Worker w2 = new Worker(latch, "李四");
//        Worker w3 = new Worker(latch, "王二");
//
//        Boss boss = new Boss(latch);
//
//        executor.execute(w3);
//        executor.execute(w2);
//        executor.execute(w1);
//        executor.execute(boss);
//
//        executor.shutdown();
    }


    public static void useThreadJoin() {
        //创建100个线程
        for (int i = 0; i < 100; i++) {
            ThreadJoinDemo threadJoinDemo = new ThreadJoinDemo();
            threadJoinDemo.start();
            try {
                threadJoinDemo.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("n=" + ThreadJoinDemo.n);
    }


    public static void useCountDownLatch() {
        final CountDownLatch countDownLatch = new CountDownLatch(2);
        ExecutorService executor1 = Executors.newSingleThreadExecutor();
        ExecutorService executor2 = Executors.newCachedThreadPool();
        ExecutorService executor3 = Executors.newFixedThreadPool(10);
        ExecutorService executor4 = Executors.newWorkStealingPool();
        for (int i = 0; i < 2; i++) {
            CountDownLatchDemo countDownLatchDemo = new CountDownLatchDemo(countDownLatch);
//            countDownLatchDemo.run();
            executor2.execute(countDownLatchDemo);
        }


        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(Thread.currentThread().getName()+",n=" + CountDownLatchDemo.n);
        executor2.shutdown();
    }
}

class ThreadJoinDemo extends Thread {
    public static volatile int n = 0;

    public void run() {
        for (int i = 0; i < 10; i++, n++) {
            try {
                sleep(3);  // 为了使运行结果更随机，延迟3毫秒
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}


class CountDownLatchDemo implements Runnable {
    public static volatile int n = 0;
    CountDownLatch countDownLatch;

    CountDownLatchDemo(CountDownLatch countDownLatch) {
        this.countDownLatch = countDownLatch;
    }

    @Override
    public void run() {
        for (int i = 0; i < 10; i++) {
            try {
                Thread.sleep(3);  // 为了使运行结果更随机，延迟3毫秒
            } catch (Exception e) {
                e.printStackTrace();
            }
            n++;
        }
        countDownLatch.countDown();
        System.out.println(Thread.currentThread().getName()+",count = " + countDownLatch.getCount());
    }
}


class Boss implements Runnable {

    private CountDownLatch downLatch;

    public Boss(CountDownLatch downLatch) {
        this.downLatch = downLatch;
    }

    public void run() {
        System.out.println("老板正在等所有的工人干完活......");
        try {
            this.downLatch.await();
        } catch (InterruptedException e) {
        }
        System.out.println("工人活都干完了，老板开始检查了！");
    }
}

class Worker implements Runnable {

    private CountDownLatch downLatch;
    private String name;

    public Worker(CountDownLatch downLatch, String name) {
        this.downLatch = downLatch;
        this.name = name;
    }

    public void run() {
        this.doWork();
        try {

            TimeUnit.SECONDS.sleep(new Random().nextInt(10));

        } catch (InterruptedException ie) {
        }
        System.out.println("--------- "+ this.name + "活干完了！");
        this.downLatch.countDown();
        System.out.println("count = "+this.downLatch.getCount());

    }

    private void doWork() {
        System.out.println(this.name + " ,正在干活!");
    }

}