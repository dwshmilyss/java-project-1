package com.yiban.javaBase.dev.concurrent.lock;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 读写锁
 * 读锁是共享锁，阻塞写操作，但不阻塞读操作
 * 写锁是独占所，阻塞任何操作
 * <p/>
 * 假如线程A和线程B使用同一个锁LOCK，此时线程A首先获取到锁LOCK.lock()，并且始终持有不释放。如果此时B要去获取锁，有四种方式：
 * LOCK.lock(): 此方式会始终处于等待中，即使调用B.interrupt()也不能中断，除非线程A调用LOCK.unlock()释放锁。
 * LOCK.lockInterruptibly(): 此方式会等待，但当调用B.interrupt()会被中断等待，并抛出InterruptedException异常，否则会与lock()一样始终处于等待中，直到线程A释放锁。
 * LOCK.tryLock(): 该处不会等待，获取不到锁并直接返回false，去执行下面的逻辑。
 * LOCK.tryLock(10, TimeUnit.SECONDS)：该处会在10秒时间内处于等待中，但当调用B.interrupt()会被中断等待，并抛出InterruptedException。10秒时间内如果线程A释放锁，会获取到锁并返回true，否则10秒过后会获取不到锁并返回false，去执行下面的逻辑。
 *
 * @auther WEI.DUAN
 * @date 2017/5/2
 * @website http://blog.csdn.net/dwshmilyss
 */
public class ReadWriteLockDemo {


    public static void main(String[] args) {
        PricesInfo pricesInfo = new PricesInfo();
        Reader readers[] = new Reader[5];
        Thread threadsReader[] = new Thread[5];
        for (int i = 0; i < 2; i++) {
            readers[i] = new Reader(pricesInfo);
            threadsReader[i] = new Thread(readers[i]);
        }

        Writer writer = new Writer(pricesInfo);
        Thread threadWriter = new Thread(writer);

        //启动2个读线程和1个写线程
        for (int i = 0; i < 2; i++) {
            threadsReader[i].start();
        }
        threadWriter.start();

    }

    static class Reader implements Runnable {
        private PricesInfo pricesInfo;

        public Reader(PricesInfo pricesInfo) {
            this.pricesInfo = pricesInfo;
        }

        @Override
        public void run() {
            //读取5次两个价格的值
            for (int i = 0; i < 5; i++) {
                System.out.printf("%s: Price1: %f\n", Thread.currentThread().getName(), pricesInfo.getPrice1());
                System.out.printf("%s: Price2: %f\n", Thread.currentThread().getName(), pricesInfo.getPrice2());
            }

        }
    }

    static class Writer implements Runnable {
        private PricesInfo pricesInfo;

        public Writer(PricesInfo pricesInfo) {
            this.pricesInfo = pricesInfo;
        }

        @Override
        public void run() {
            //修改了三次两个价格的值，并且在每次修改之后睡眠2秒
            for (int i = 0; i < 3; i++) {
                System.out.printf("Writer: Attempt to modify the prices.\n");
                pricesInfo.setPrices(Math.random() * 10, Math.random() * 8);
                System.out.printf("Writer: Prices have been modified.\n");
                try {
                    Thread.sleep(2);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }


        }
    }

    static class PricesInfo {
        private double price1;
        private double price2;
        private ReadWriteLock lock;

        public PricesInfo() {
            this.price1 = 1.0;
            this.price2 = 2.0;
            this.lock = new ReentrantReadWriteLock();
        }

        public double getPrice1() {
            double value = 0.0;
            if (lock.readLock().tryLock()) {
//                lock.readLock().lock();
                value = price1;
                lock.readLock().unlock();
            }
            return value;
        }

        public void setPrices(double price1, double price2) {
            if (lock.writeLock().tryLock()) {
//                lock.writeLock().lock();
                this.price1 = price1;
                this.price2 = price2;
                lock.writeLock().unlock();
            }
        }

        public double getPrice2() {
            double value = 0.0;
            if (lock.readLock().tryLock()) {
//                lock.readLock().lock();
                value = price2;
                lock.readLock().unlock();
            }
            return value;
        }

    }
}
