package com.yiban.javaBase.dev.concurrent.lock;

import java.util.concurrent.locks.ReentrantLock;

/**
 * 公平锁和非公平锁
 * 公平锁：哪个线程先运行 哪个线程就先获得锁
 * 费公平锁：随机获取锁对象
 *
 * @auther WEI.DUAN
 * @date 2017/4/25
 * @website http://blog.csdn.net/dwshmilyss
 */
public class FairAndNonFairLockDemo {
    //不加参数或者false 就是非公平锁
    private static final ReentrantLock lock = new ReentrantLock();
    private static final ReentrantLock fairlock = new ReentrantLock(true);
    private int n;

    public static void main(String[] args) {
        FairAndNonFairLockDemo rlt = new FairAndNonFairLockDemo();
        for (int i = 0; i < 100; i++) {
            Thread nonT = new Thread(new NonFairTestThread(rlt));
            nonT.setName("nonFair[" + (i + 1) + "]");
            nonT.start();

//            Thread fairT = new Thread(new FairTestThread(rlt));
//            fairT.setName("fair[" + (i + 1) + "]");
//            fairT.start();
        }
    }

    public int getNum() {
        return n;
    }

    public void setNum(int n) {
        this.n = n;
    }

    // 非公平锁
    static class NonFairTestThread implements Runnable {
        private FairAndNonFairLockDemo rlt;

        public NonFairTestThread(FairAndNonFairLockDemo rlt) {
            this.rlt = rlt;
        }

        public void run() {
            lock.lock();
            try {
                rlt.setNum(rlt.getNum() + 1);
                System.out.println(Thread.currentThread().getName()
                        + " nonfairlock***************" + rlt.getNum());
            } finally {
                lock.unlock();
            }
        }
    }

    // 公平锁
    static class FairTestThread implements Runnable {
        private FairAndNonFairLockDemo rlt;

        public FairTestThread(FairAndNonFairLockDemo rlt) {
            this.rlt = rlt;

        }

        public void run() {
            fairlock.lock();
            try {
                rlt.setNum(rlt.getNum() + 1);
                System.out.println(Thread.currentThread().getName()
                        + "   fairlock=======" + rlt.getNum() + "   "
                        + fairlock.getHoldCount() + " queuelength="
                        + fairlock.getQueueLength());
            } finally {
                fairlock.unlock();
            }

        }
    }
}
