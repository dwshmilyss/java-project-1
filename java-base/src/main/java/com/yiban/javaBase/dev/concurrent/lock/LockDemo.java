package com.yiban.javaBase.dev.concurrent.lock;

import java.util.Collection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * JAVA 锁的demo
 * 1、读写锁
 * 读锁是共享锁，阻塞写操作，但不阻塞读操作
 * 写锁是独占所，阻塞任何操作
 * 2、重入锁
 *
 * @auther WEI.DUAN
 * @date 2017/5/2
 * @website http://blog.csdn.net/dwshmilyss
 */
public class LockDemo {
    static final MyLock lock = new MyLock();


    public static void main(String[] args) {
        Thread[] threads = new Thread[2];
        ExecutorService executorService = Executors.newCachedThreadPool();
        executorService.execute(new TestReEnterLock(lock));
        executorService.execute(new TestReEnterLock(lock));
//        for (int i = 0; i < 2; i++) {
//            threads[i] = new Thread(new TestReEnterLock(), "thread-" + i);
//            threads[i].start();
//            try {
//                threads[i].join();
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
        executorService.shutdown();
    }

    static class MyLock extends ReentrantLock {
        // 2. 实现 getOwnerName() 方法。此方法使用Lock类的保护方法 getOwner()， 返回控制锁的线程（如果存在）的名字。
        public String getOwnerName() {
            if (this.getOwner() == null) {
                return "None";
            }
            return this.getOwner().getName();
        }

        // 3. 实现 getThreads() 方法。此方法使用Lock类的保护方法 getQueuedThreads()，返回在锁里的线程的 queued list。
        public Collection<Thread> getThreads() {
            return this.getQueuedThreads();
        }
    }

    static class TestReEnterLock implements Runnable {
        private String name;
        private MyLock lock;

        public TestReEnterLock(String name) {
            this.name = name;
        }

        public TestReEnterLock() {

        }
        public TestReEnterLock(MyLock lock) {
            this.lock = lock;
        }

        public String getName() {
//            synchronized (TestReEnterLock.class){
//                System.out.println(Thread.currentThread().getName() +" getName : "+ name);
//                setName("aa");
//                System.out.println(Thread.currentThread().getName() +" getName : "+ name);
//            }

            System.out.println(Thread.currentThread().getName() + " getName : " + name);
            setName("aa");
            System.out.println(Thread.currentThread().getName() + " getName : " + name);
            return name;
        }

        public void setName(String name) {
//            synchronized (TestReEnterLock.class) {
//                System.out.println(Thread.currentThread().getName() +" setName: "+ name);
//                this.name = name;
//            }

            System.out.println(Thread.currentThread().getName() + " setName: " + name);
            this.name = name;
        }

        @Override
        public void run() {
            lock.lock();
            for (int i = 0; i < 2; i++) {
                setName("bb");
                try {
                    TimeUnit.SECONDS.sleep(3);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                getName();
            }
            System.out.printf("************************\n");
            System.out.printf("Lock: Owner : %s\n", lock.getOwnerName());

            // 16. 显示锁queued的线程的号码和名字。
            System.out.printf("Lock: Queued Threads: %s\n", lock.hasQueuedThreads()); // 译者注：加上 System
            if (lock.hasQueuedThreads()) {
                System.out.printf("Lock: Queue Length: %d\n", lock.getQueueLength());
                System.out.printf("Lock: Queued Threads: ");
                Collection<Thread> lockedThreads = lock.getThreads();
                for (Thread lockedThread : lockedThreads) {
                    System.out.printf("%s ", lockedThread.getName());
                }
                System.out.printf("\n");
            }
            // 17. 显示关于Lock对象的公平性和状态的信息。
            System.out.printf("Lock: Fairness: %s\n", lock.isFair());
            System.out.printf("Lock: Locked: %s\n", lock.isLocked());
            System.out.printf("************************\n");

            lock.unlock();
        }
    }

}
