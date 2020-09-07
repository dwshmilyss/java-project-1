package com.yiban.javaBase.dev.concurrent;


import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.apache.spark.util.UninterruptibleThread;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 对比 Semaphore，CountDownLatch，Thread.join()的使用场景及异同
 * 要求：启动3个线程同时对一个数进行累加1，每个线程累加10次，最终的结果要求是30
 * <p/>
 * CountDownLatch与join的区别：
 * 调用thread.join() 方法必须等thread 执行完毕，当前线程才能继续往下执行，
 * 而CountDownLatch通过计数器提供了更灵活的控制，只要检测到计数器为0当前线程就可以往下执行而不用管相应的thread是否执行完毕。
 * <p/>
 * Semaphore 主要是用在线程池 连接池等一些服务器资源上，控制资源的并发访问数量。一般情况下，会给予Semaphore(N) ： N = 连接池资源数
 * CyclicBarrier 也能实现同样的效果 有2种方式
 * 1、 在其构造函数中定义一个Runnable() 该Runnable()是在所有子线程结束之后运行
 * 2、 如果要在main中运行，则permits数应该=子线程数+main线程数，即子线程数+1
 * <p/>
 * phaser
 *
 * @auther WEI.DUAN
 * @date 2017/4/25
 * @website http://blog.csdn.net/dwshmilyss
 */
public class ConcurrentWaitDemo {
    /**
     * 利用 CyclicBarrier 实现主线程等待子线程执行完再运行
     */
    public static boolean flag = false;

    public static void main(String[] args) {
        ThreadFactory threadFactory = new ThreadFactoryBuilder()
                .setDaemon(true)
                .setNameFormat("thread_id-%d")
                .setThreadFactory(new ThreadFactory() {
                    @Override
                    public Thread newThread(@NotNull Runnable r) {
                        return new UninterruptibleThread(r,"unused");
                    }
                }).build();
        //测试不同的线程池的使用效果
        ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor();
        ExecutorService cachedThreadPool = Executors.newCachedThreadPool();
//        ExecutorService cachedThreadPool = Executors.newCachedThreadPool(threadFactory);
        ExecutorService fixedThreadPool = Executors.newFixedThreadPool(10,threadFactory);
        ExecutorService executor4 = Executors.newWorkStealingPool();

//        useThreadJoin();
        useCountDownLatch(fixedThreadPool);
//        useSemaphore(fixedThreadPool);
//        useCyclicBarrier(fixedThreadPool);

//        usePhaser(fixedThreadPool);

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

    /**
     * 利用Thread.join()实现主线程等待子线程执行完再运行
     * 其实并没有实现真正意义上的并行执行，因为线程是依次创建，也是依次运行的，从打印结果就能看出来
     */
    public static void useThreadJoin() {
        //创建100个线程
        for (int i = 0; i < 3; i++) {
            ThreadJoinDemo threadJoinDemo = new ThreadJoinDemo();
            threadJoinDemo.setName("task thread - " + i);
            threadJoinDemo.start();
            try {
                //main线程(调用者线程)等待，直到所有join的threadJoinDemo线程终止
                threadJoinDemo.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("所有任务都已完成");
        System.out.println(Thread.currentThread().getName() + ",n=" + ThreadJoinDemo.n);
    }

    /**
     * Executors.newFixedThreadPool(1) ==  Executors.newSingleThreadExecutor()
     * 利用CountDownLatch实现主线程等待子线程执行完再运行
     */
    public static void useCountDownLatch(ExecutorService executorService) {
        //指定parties个数
        final CountDownLatch countDownLatch = new CountDownLatch(3);
        System.out.println("线程" + Thread.currentThread().getName() + "已发出任务，等待完成");
        for (int i = 0; i < 3; i++) {
            CountDownLatchDemo countDownLatchDemo = new CountDownLatchDemo(countDownLatch);
            executorService.execute(countDownLatchDemo);
        }
        try {
            //阻塞直到所有的线程执行完毕，getCount()为0
            countDownLatch.await();
            System.out.println("CountDownLatch.count = " + countDownLatch.getCount());
            System.out.println("所有任务都已完成");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(Thread.currentThread().getName() + ",n=" + CountDownLatchDemo.n);
        executorService.shutdownNow();
    }

    /**
     * 利用 Semaphore 实现主线程等待子线程执行完再运行 感觉这个并不严谨
     * Semaphore
     */
    public static void useSemaphore(ExecutorService cachedThreadPool) {
        final Semaphore semaphore = new Semaphore(0, true);
        final ReentrantLock lock = new ReentrantLock();
        System.out.println("线程" + Thread.currentThread().getName() + "即将发出任务");
        for (int i = 0; i < 3; i++) {
            SemaphoreDemo semaphoreDemo = new SemaphoreDemo(semaphore, lock);
            //启动线程执行任务
            cachedThreadPool.execute(semaphoreDemo);
        }

        System.out.println("线程" + Thread.currentThread().getName() + "已发出任务，等待完成");
        try {
            //等待线程池中的线程执行完毕，因为启动了三个线程，每次调用semaphore.release()都会使acquire(permits)增加1，所以这里的参数为3
            System.out.println(Thread.currentThread().getName() + ",begin semaphore.availablePermits() = " + semaphore.availablePermits());
            //因为要在主线程中阻塞，所以这里获取资源一定要在主线程中获取
            semaphore.acquire(3);
            System.out.println(Thread.currentThread().getName() + ",end semaphore.availablePermits() = " + semaphore.availablePermits());
            System.out.println("所有任务都已完成");
            System.out.println(Thread.currentThread().getName() + ",n=" + SemaphoreDemo.n);
        } catch (Exception e) {
            e.printStackTrace();
        }
        cachedThreadPool.shutdown();
    }

    public static boolean getFlag() {
        return flag;
    }

    public static void useCyclicBarrier(ExecutorService fixedThreadPool) {
        //因为子线程有三个，外加一个Main线程中的监视器，所以这里的参数要为4
        final CyclicBarrier cyclicBarrier = new CyclicBarrier(4);

        //CyclicBarrier构造函数中可以指定一个new Runnable()对象，当子任务都完成后，就会执行new Runnable()中的任务
//        CyclicBarrier cyclicBarrier = new CyclicBarrier(3, new Runnable() {
//            @Override
//            public void run() {
//                if (getFlag()) {
//                    System.out.println("所有任务都已完成");
//                    System.out.println(Thread.currentThread().getName() + ",n=" + CyclicBarrierDemo.n);
//                }
//            }
//        });
        //用CyclicBarrier最好和newFixedThreadPool一起使用，因为可以控制线程的数目，此数目对应CyclicBarrier的参数
        System.out.println("线程" + Thread.currentThread().getName() + "即将发出任务");
        for (int i = 0; i < 3; i++) {
            CyclicBarrierDemo cyclicBarrierDemo = new CyclicBarrierDemo(cyclicBarrier);
            fixedThreadPool.execute(cyclicBarrierDemo);
        }
        System.out.println("线程" + Thread.currentThread().getName() + "已发出任务，等待完成");
        flag = true;
        try {
            TimeUnit.SECONDS.sleep(5);
            System.out.println(Thread.currentThread().getName() + " ,before = " + cyclicBarrier.getNumberWaiting() + " : " + cyclicBarrier.getParties());
            cyclicBarrier.await();
            TimeUnit.SECONDS.sleep(5);
            System.out.println(Thread.currentThread().getName() + " ,after = " + cyclicBarrier.getNumberWaiting() + " : " + cyclicBarrier.getParties());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (BrokenBarrierException e) {
            e.printStackTrace();
        }
        System.out.println("所有任务都已完成");
        System.out.println(Thread.currentThread().getName() + ",n=" + CyclicBarrierDemo.n);
        fixedThreadPool.shutdown();
    }

    /**
     * 利用 Semaphore 实现主线程等待子线程执行完再运行
     * Semaphore
     */
    public static void usePhaser(ExecutorService cachedThreadPool) {
        int parties = 3;
        //可以不指定parties个数
        final Phaser phaser = new Phaser();
        final ReentrantLock lock = new ReentrantLock();
        System.out.println("线程" + Thread.currentThread().getName() + "即将发出任务");
        //主线程先注册一个
        //对应下文中，主线程可以等待所有的parties到达后再解除阻塞（类似与CountDownLatch）
        phaser.register();
        for (int i = 0; i < 3; i++) {
            PhaserDemo phaserDemo = new PhaserDemo(phaser, lock);
            phaser.register();//每创建一个task，我们就注册一个party
            //启动线程执行任务
            cachedThreadPool.execute(phaserDemo);
        }

        System.out.println("线程" + Thread.currentThread().getName() + "已发出任务，等待完成");
        int flag = phaser.getPhase();
        System.out.println(phaser.getArrivedParties() + "," + phaser.getUnarrivedParties() + "," + phaser.getRegisteredParties());
//        phaser.awaitAdvance(flag);
        //主线程到达，且注销自己 此后线程池中的线程即可开始按照周期，同步执行。
        phaser.arriveAndDeregister();
        System.out.println("所有任务都已完成");
        System.out.println(phaser.getArrivedParties() + "," + phaser.getUnarrivedParties() + "," + phaser.getRegisteredParties());
        System.out.println(Thread.currentThread().getName() + ",n=" + PhaserDemo.n);
        cachedThreadPool.shutdown();
    }

    /**
     * 用于测试线程等待的方法 每次累加10后睡眠3秒
     *
     * @param n
     */
    public static void testMethon(AtomicInteger n) {
        for (int i = 0; i < 10; i++, n.getAndIncrement()) {
        }
        System.out.println(n.get());
        try {
            Thread.sleep(3000);  // 为了使运行结果更随机，延迟3秒
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 利用Thread.join()实现主线程等待子线程执行完再运行
     */
    static class ThreadJoinDemo extends Thread {
        public static AtomicInteger n = new AtomicInteger(0);

        public void run() {
            System.out.println("线程 : " + Thread.currentThread().getName() + ",开始运行。。。。");
            testMethon(n);
            System.out.println("线程 : " + Thread.currentThread().getName() + ",结束运行。。。。" + "，n = " + n.get());
        }
    }

    /**
     * 利用CountDownLatch实现主线程等待子线程执行完再运行
     */
    static class CountDownLatchDemo implements Runnable {
        public static AtomicInteger n = new AtomicInteger(0);
        private CountDownLatch countDownLatch;

        CountDownLatchDemo(CountDownLatch countDownLatch) {
            this.countDownLatch = countDownLatch;
        }


        @Override
        public void run() {
            System.out.println("线程 : " + Thread.currentThread().getName() + ",开始运行。。。。");
            testMethon(n);
            //这里的输出有可能全是30，因为testMethon()方法里有个3秒钟的等待，CPU执行到这句的时候有可能n已经累加到30
            //包括下面的打印CountDownLatch计数，可能并没有减去1，还是3，也是由于这种情况，这也从侧面证明了Thread.sleep是让出了CPU时间 给其他线程运行
            System.out.println("线程 : " + Thread.currentThread().getName() + ",结束运行。。。。" + "，n = " + n.get());
            System.out.println("线程 : " + Thread.currentThread().getName() + ",CountDownLatch计数：" + countDownLatch.getCount());
            this.countDownLatch.countDown();
            /**
             * 这里其实还可以继续干其他的事情，当前线程是不阻塞的，阻塞的是调用了await()的线程
             */
        }
    }

    /**
     * 利用Semaphore实现主线程等待子线程执行完再运行
     */
    static class SemaphoreDemo implements Runnable {

        public static AtomicInteger n = new AtomicInteger(0);

        ReentrantLock lock;
        SpinLock spinLock;
        Semaphore semaphore;

        public SemaphoreDemo(Semaphore semaphore) {
            this.semaphore = semaphore;
        }

        public SemaphoreDemo(Semaphore semaphore, ReentrantLock lock) {
            this.semaphore = semaphore;
            this.lock = lock;
            spinLock = new SpinLock();
        }

        @Override
        public void run() {
            try {
                //这里锁住之后打印结果会更清新(其实这里如果运行的话 完全没必要加锁，只是为了方便看打印结果)
                lock.lock();
//                synchronized (CountDownLatchDemo.class){
                testMethon(n);
                semaphore.release();
                //当前Semaphore的可用资源数，即允许几个线程获取资源
                System.out.println(Thread.currentThread().getName() + ",left : " + semaphore.availablePermits());
//                }
                lock.unlock();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * 利用 CyclicBarrier 实现主线程等待子线程执行完再运行
     */
    static class CyclicBarrierDemo implements Runnable {

        public static AtomicInteger n = new AtomicInteger(0);

        CyclicBarrier cyclicBarrier;

        public CyclicBarrierDemo(CyclicBarrier cyclicBarrier) {
            this.cyclicBarrier = cyclicBarrier;
        }

        @Override
        public void run() {
            if (!cyclicBarrier.isBroken()) {
                testMethon(n);
                try {
                    //当前线程执行完任务后阻塞，等待其他任务的完成
                    System.out.println(Thread.currentThread().getName() + ",before = " + cyclicBarrier.getNumberWaiting() + " : " + cyclicBarrier.getParties());
                    System.out.println(Thread.currentThread().getName() + ",after = " + cyclicBarrier.getNumberWaiting() + " : " + cyclicBarrier.getParties());
                    cyclicBarrier.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (BrokenBarrierException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 利用 Phaser 实现主线程等待子线程执行完再运行
     */
    static class PhaserDemo implements Runnable {
        public static AtomicInteger n = new AtomicInteger(0);
        Phaser phaser;
        ReentrantLock lock;

        public PhaserDemo(Phaser phaser) {
            this.phaser = phaser;
        }

        public PhaserDemo(Phaser phaser, ReentrantLock lock) {
            this.phaser = phaser;
            this.lock = lock;
        }

        @Override
        public void run() {
            try {
                if (!phaser.isTerminated()) {
//            lock.lock();
                    testMethon(n);
//            int a = phaser.arrive();
                    //等待同一周期内，其他Task到达
                    //然后进入新的周期，并继续同步进行
                    phaser.arriveAndAwaitAdvance();
//                phaser.arriveAndDeregister();
                    //在运行过程中可以随时注册一个parties
//            int b = phaser.register();
//            lock.unlock();
                }
            } catch (Exception e) {

            } finally {
                phaser.arriveAndDeregister();
            }
        }
    }


    public static class SpinLock {
        private AtomicReference<Thread> sign = new AtomicReference<>();

        public void lock() {
            Thread current = Thread.currentThread();
            while (!sign.compareAndSet(null, current)) {
            }
        }

        public void unlock() {
            Thread current = Thread.currentThread();
            sign.compareAndSet(current, null);
        }
    }
}
