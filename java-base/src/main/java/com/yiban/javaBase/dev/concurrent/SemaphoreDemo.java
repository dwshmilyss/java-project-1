package com.yiban.javaBase.dev.concurrent;


import java.util.Random;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 对比 Semaphore，CountDownLatch，Thread.join()的使用场景及异同
 * CountDownLatch与join的区别：
 * 调用thread.join() 方法必须等thread 执行完毕，当前线程才能继续往下执行，
 * 而CountDownLatch通过计数器提供了更灵活的控制，只要检测到计数器为0当前线程就可以往下执行而不用管相应的thread是否执行完毕。
 *
 * @auther WEI.DUAN
 * @date 2017/4/25
 * @website http://blog.csdn.net/dwshmilyss
 */
public class SemaphoreDemo {
    public static void main(String[] args) {
//        useThreadJoin();
//        useCountDownLatch();
//        useSemaphore();
        useCyclicBarrier();


//        ExecutorService executor = Executors.newCachedThreadPool();
//        CountDownLatch latch = new CountDownLatch(3);
//
//        Worker w1 = new Worker(latch, "张三");
//        Worker w2 = new Worker(latch, "李四");
//        Worker w3 = new Worker(latch, "王二");
//        Boss boss = new Boss(latch);
//
//        executor.execute(w3);
//        executor.execute(w2);
//        executor.execute(w1);
//        executor.execute(boss);
//        executor.shutdown();

//        int COUNT_BITS = Integer.SIZE - 3;
//        System.out.println(COUNT_BITS);
//        int CAPACITY = (1 << COUNT_BITS) - 1;
//        System.out.println(CAPACITY + " : " + Integer.toBinaryString(CAPACITY));
//        int RUNNING = -1 << COUNT_BITS;    // running 线程池能接受新任务
//        int SHUTDOWN = 0 << COUNT_BITS;    // shutdown 线程池不再接受新任务
//        int STOP = 1 << COUNT_BITS;    // stop 线程池不再接受新任务，不再执行队列中的任务，而且要中断正在处理的任务
//        int TIDYING = 2 << COUNT_BITS;    // tidying 线程池所有任务均已终止
//        int TERMINATED = 3 << COUNT_BITS;    // terminated terminated()方法执行结束
//        System.out.println("STOP = "+STOP + " : " + Integer.toBinaryString(STOP));
//        System.out.println("TIDYING = "+TIDYING + " : " + Integer.toBinaryString(TIDYING));
//        System.out.println("TERMINATED = "+TERMINATED + " : " + Integer.toBinaryString(TERMINATED));
//
//        String s = "100000000000000000000000000000";
//        System.out.println(s.length());

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


    /**
     * Executors.newFixedThreadPool(1) ==  Executors.newSingleThreadExecutor()
     */
    public static void useCountDownLatch() {
        final CountDownLatch countDownLatch = new CountDownLatch(3);
        final CountDownLatch startSignal = new CountDownLatch(1);
        final CountDownLatch doneSignal = new CountDownLatch(3);
        ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor();
        ExecutorService cachedThreadPool = Executors.newCachedThreadPool();
        ExecutorService fixedThreadPool = Executors.newFixedThreadPool(10);
        ExecutorService executor4 = Executors.newWorkStealingPool();
        System.out.println("线程" + Thread.currentThread().getName() + "即将发出任务");
//        startSignal.countDown();
        for (int i = 0; i < 3; i++) {
//            CountDownLatchDemo countDownLatchDemo = new CountDownLatchDemo(startSignal,doneSignal);
            CountDownLatchDemo countDownLatchDemo = new CountDownLatchDemo(countDownLatch);
            fixedThreadPool.execute(countDownLatchDemo);
        }
        try {
            System.out.println("线程" + Thread.currentThread().getName() + "已发出任务，等待完成");
//            doneSignal.await();
            //阻塞直到所有的线程执行完毕，getCount()为0
            countDownLatch.await();
            System.out.println("CountDownLatch.count = "+countDownLatch.getCount());
            System.out.println("所有任务都已完成");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(Thread.currentThread().getName() + ",n=" + CountDownLatchDemo.n);
        fixedThreadPool.shutdownNow();
    }


    public static void useSemaphore(){
        final Semaphore semaphore = new Semaphore(0);
        ExecutorService cachedThreadPool = Executors.newCachedThreadPool();
        System.out.println("线程" + Thread.currentThread().getName() + "即将发出任务");
        for (int i = 0; i < 3; i++) {
            SemaphoreExample semaphoreExample = new SemaphoreExample(semaphore);
            //启动线程执行任务
            cachedThreadPool.execute(semaphoreExample);
        }

        System.out.println("线程" + Thread.currentThread().getName() + "已发出任务，等待完成");
        try {
            //等待线程池中的线程执行完毕，因为启动了三个线程，每次调用semaphore.release()都会使acquire(permits)增加1，所以这里的参数为3
            semaphore.acquire(3);
            System.out.println("semaphore.availablePermits() = "+semaphore.availablePermits());
            System.out.println("所有任务都已完成");
            System.out.println(Thread.currentThread().getName() + ",n=" + SemaphoreExample.n);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        cachedThreadPool.shutdown();
    }

    public static boolean flag = false;
    public static boolean getFlag() {
        return flag;
    }
    public static void useCyclicBarrier(){
        //因为子线程有三个，外加一个Main线程中的监视器，所以这里的参数要为4
//        final CyclicBarrier cyclicBarrier = new CyclicBarrier(4);
        CyclicBarrier cyclicBarrier1 = new CyclicBarrier(3, new Runnable() {
            @Override
            public void run() {
                if (getFlag()){
                    System.out.println("所有任务都已完成");
                    System.out.println(Thread.currentThread().getName() + ",n=" + CyclicBarrierDemo.n);
                }
            }
        });
//        ExecutorService cachedThreadPool = Executors.newCachedThreadPool();
//        ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor();
        //用CyclicBarrier最好和newFixedThreadPool一起使用，因为可以控制线程的数目，此数目对应CyclicBarrier的参数
        ExecutorService fixedThreadPool = Executors.newFixedThreadPool(3);
        System.out.println("线程" + Thread.currentThread().getName() + "即将发出任务");
        for (int i = 0; i < 3; i++) {
            CyclicBarrierDemo cyclicBarrierDemo = new CyclicBarrierDemo(cyclicBarrier1);
            fixedThreadPool.execute(cyclicBarrierDemo);
        }
        System.out.println("线程" + Thread.currentThread().getName() + "已发出任务，等待完成");
        flag = true;
//        try {
//            TimeUnit.SECONDS.sleep(5);
//            System.out.println(Thread.currentThread().getName()+" ,before = "+cyclicBarrier.getNumberWaiting()+" : "+cyclicBarrier.getParties());
//            cyclicBarrier.await();
//            TimeUnit.SECONDS.sleep(5);
//            System.out.println(Thread.currentThread().getName()+" ,after = "+cyclicBarrier.getNumberWaiting()+" : "+cyclicBarrier.getParties());
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        } catch (BrokenBarrierException e) {
//            e.printStackTrace();
//        }
//        System.out.println("所有任务都已完成");
//        System.out.println(Thread.currentThread().getName() + ",n=" + CyclicBarrierDemo.n);
        fixedThreadPool.shutdown();
    }
}



/**
 * 利用Thread.join()实现线程的等待
 */
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

/**
 * 利用CountDownLatch实现线程的等待
 */
class CountDownLatchDemo implements Runnable {
    public static AtomicInteger n = new AtomicInteger(0);
    private CountDownLatch countDownLatch;
    private CountDownLatch startSignal;
    private CountDownLatch doneSignal;

    CountDownLatchDemo(CountDownLatch countDownLatch) {
        this.countDownLatch = countDownLatch;
    }

    CountDownLatchDemo(CountDownLatch startSignal, CountDownLatch doneSignal) {
        this.startSignal = startSignal;
        this.doneSignal = doneSignal;
    }

    @Override
    public void run() {
        method2();
    }

    void method1() {
        System.out.println("线程" + Thread.currentThread().getName() + "正在准备");
        try {
            startSignal.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("线程" + Thread.currentThread().getName() + "开始运行");
        for (int i = 0; i < 10; i++) {
            try {
                Thread.sleep(3);  // 为了使运行结果更随机，延迟3毫秒
            } catch (Exception e) {
                e.printStackTrace();
            }
            n.getAndIncrement();
        }
        doneSignal.countDown();
        System.out.println("线程" + Thread.currentThread().getName() + "已运行完");
    }

    void method2() {
        for (int i = 0; i < 10; i++,n.getAndIncrement()) {
//            System.out.println(Thread.currentThread().getName()+" :　"+ n++);
        }
        try {
            Thread.sleep(3000);  // 为了使运行结果更随机，延迟3秒
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.countDownLatch.countDown();
    }

}

/**
 * 测试CountDownLatch with ThreadPool
 * 配合Worker
 */
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

/**
 * 测试CountDownLatch with ThreadPool
 * 配合Boss
 */
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
        System.out.println("--------- " + this.name + "活干完了！");
        this.downLatch.countDown();
        System.out.println("count = " + this.downLatch.getCount());
    }

    private void doWork() {
        System.out.println(this.name + " ,正在干活!");
    }
}

/**
 * 利用Semaphore实现主线程等待子线程执行完再运行
 */
class SemaphoreExample implements Runnable{

    public static AtomicInteger n = new AtomicInteger(0);

    Semaphore semaphore;

    public SemaphoreExample(Semaphore semaphore) {
        this.semaphore = semaphore;
    }

    @Override
    public void run() {
        try {
            method2();
            semaphore.release();
            System.out.println("-----------------" + semaphore.availablePermits());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void method2() {
        for (int i = 0; i < 10; i++,n.getAndIncrement()) {
//            System.out.println(Thread.currentThread().getName()+" :　"+ n++);
        }
        try {
            Thread.sleep(1000);  // 为了使运行结果更随机，延迟3秒
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}


class CyclicBarrierDemo implements Runnable{

    public static AtomicInteger n = new AtomicInteger(0);

    CyclicBarrier cyclicBarrier;

    public CyclicBarrierDemo(CyclicBarrier cyclicBarrier) {
        this.cyclicBarrier = cyclicBarrier;
    }

    @Override
    public void run() {
        method2();
        try {
            //当前线程执行完任务后阻塞，等待其他任务的完成
            System.out.println(Thread.currentThread().getName()+",before = "+cyclicBarrier.getNumberWaiting()+" : "+cyclicBarrier.getParties());
            cyclicBarrier.await();
            System.out.println(Thread.currentThread().getName()+",after = "+cyclicBarrier.getNumberWaiting()+" : "+cyclicBarrier.getParties());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (BrokenBarrierException e) {
            e.printStackTrace();
        }
    }

    void method2() {
        for (int i = 0; i < 10; i++,n.getAndIncrement()) {
        }
        try {
            Thread.sleep(1000);  // 为了使运行结果更随机，延迟3秒
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}