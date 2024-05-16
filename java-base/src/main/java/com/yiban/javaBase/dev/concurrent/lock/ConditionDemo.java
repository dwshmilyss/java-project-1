package com.yiban.javaBase.dev.concurrent.lock;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @auther WEI.DUAN
 * @date 2018/10/20
 * @website http://blog.csdn.net/dwshmilyss
 */
public class ConditionDemo {

    public static ReentrantLock lock = new ReentrantLock(true);
    public static Condition conditionA = lock.newCondition();
    public static int index = 0;
    public static void main(String[] args) throws InterruptedException {
        test1();
    }

    /**
     * @Description: TODO
     * @Date: 2023/10/19
     * @Auther: David.duan
     主要为了测试 signalAll
     **/
    public static void test1() throws InterruptedException {
        ThreadA threadA = new ThreadA();
        ThreadB threadB = new ThreadB();
        ThreadC threadC = new ThreadC();
        threadA.start();//（1）
        threadB.start();//（2）
        threadC.start();//（3）
        Thread.sleep(5000);
        lock.lock();
        conditionA.signalAll();//唤醒所有的await
        lock.unlock();
        threadA.join();
        threadB.join();
        threadC.join();
    }


    /**

     主要是为了看一下lock.lock lock.unlock condition.await condition.signal 的执行顺序
     输出如下:
        主线程sleep 2s前 //主线程sleep之后让出cpu，同时进入wait状态
        子线程获取锁资源并await挂起线程 //子线程获取cpu，执行lock.lock()后获取到锁(AQS中的state从0改为1)
        子线程sleep 5s前 //子线程sleep之后让出cpu，进入wait状态，但是并不释放锁
        主线程sleep 2s后 //主线程获取到cpu，继续执行到lock()时试图获取锁，发现锁已经被子线程所持有(state已经是1了)，于是把自己封装成Node放入AQS队列中，同时park(挂起)。
        子线程sleep 5s后 //睡眠了5s后子线程重新获取到cpu，之后执行condition.await()，该方法会将当前线程封装成Node扔到 Condition队列 中并放弃锁资源。于是我们的主线程被唤醒并且将state从0修改为1，获取到锁
        wait time = 5 //打印时长，这里打印5s是因为取的是两个线程中休眠最长的那个时间
        主线程等待5s拿到锁资源，子线程执行了await方法 //继续执行主线程的打印
        主线程唤醒了await挂起的子线程 // 主线程执行到condition.signal()时，由于主线程还持有锁，所以会将子线程从Condition队列中扔到AQS队列，等待被唤醒
        主线程释放了锁 // 继续执行主线程，直到lock.unlock(); 主线程释放了锁，同时唤醒AQS中的子线程
        子线程await //子线程继续执行
        子线程挂起后被唤醒！持有锁资源 //子线程继续执行
    */
    public static void test() throws InterruptedException {
        ReentrantLock lock = new ReentrantLock();
        Condition condition = lock.newCondition();

        new Thread(() -> {
            lock.lock();
            System.out.println("子线程获取锁资源并await挂起线程");
            try {
                System.out.println("子线程sleep 5s前");
                Thread.currentThread().sleep(5000);
                System.out.println("子线程sleep 5s后");
                condition.await();
                System.out.println("子线程await");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("子线程挂起后被唤醒！持有锁资源");
            lock.unlock();
            System.out.println("子线程释放锁");
        }).start();

        long a = System.currentTimeMillis();
        System.out.println("主线程sleep 2s前");
        Thread.currentThread().sleep(2000);
        System.out.println("主线程sleep 2s后");
        // =================main======================
        lock.lock();
        System.out.println("wait time = " + ((System.currentTimeMillis() - a)/1000));
        System.out.println("主线程等待5s拿到锁资源，子线程执行了await方法");
        condition.signal();
        System.out.println("主线程唤醒了await挂起的子线程");
        lock.unlock();
        System.out.println("主线程释放了锁");
    }

    static class ThreadA extends Thread{
        @Override
        public void run(){
            try{
                lock.lock();
                System.out.println("ThreadA 获取了锁");
                System.out.println("A进程输出" + " : " + ++index);
                conditionA.await();
                System.out.println("ThreadA 被唤醒了");
            }catch (Exception e){
                e.printStackTrace();
            }finally {
                lock.unlock();
                System.out.println("ThreadA 释放了锁");
            }
        }
    }

    static class ThreadB extends Thread{
        @Override
        public void run(){
            try{
                lock.lock();
                System.out.println("ThreadB 获取了锁");
                System.out.println("B进程输出" + " : " + ++index);
                conditionA.await();
                System.out.println("ThreadB 被唤醒了");
            }catch (Exception e){
                e.printStackTrace();
            }finally {
                lock.unlock();
                System.out.println("ThreadB 释放了锁");
            }
        }
    }

    static class ThreadC extends Thread{
        @Override
        public void run(){
            try{
                lock.lock();
                System.out.println("ThreadC 获取了锁");
                System.out.println("C进程输出" + " : " + ++index);
                conditionA.await();
                System.out.println("ThreadC 被唤醒了");
            }catch (Exception e){
                e.printStackTrace();
            }finally {
                lock.unlock();
                System.out.println("ThreadC 释放了锁");
            }
        }
    }

}
