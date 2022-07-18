package com.yiban.javaBase.dev.concurrent.lock.clh;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * @auther WEI.DUAN
 * @date 2018/10/23
 * @website http://blog.csdn.net/dwshmilyss
 */
public class CLHLock implements Lock {
    //用atomic声明尾节点可以保证尾节点更新时的原子性
    AtomicReference<QNode> tail = new AtomicReference<>(new QNode());
    ThreadLocal<QNode> myPred;//前驱
    ThreadLocal<QNode> myNode;//当前节点

    public CLHLock() {
        tail = new AtomicReference<>(new QNode());

        myNode = new ThreadLocal<QNode>(){
            @Override
            protected QNode initialValue() {
                return new QNode();
            }
        };

        myPred = new ThreadLocal<QNode>(){
            @Override
            protected QNode initialValue() {
                return null;
            }
        };
    }

    @Override
    public void lock() {
        QNode qNode = myNode.get();
        qNode.locked = true;
        QNode pred = tail.getAndSet(qNode);
        myPred.set(pred);
        while (pred.locked){
            //自旋锁
        }
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {

    }

    @Override
    public boolean tryLock() {
        return false;
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        return false;
    }

    @Override
    public void unlock() {
        QNode qNode = myNode.get();
        qNode.locked=false;
        myNode.set(myPred.get());
    }

    @Override
    public Condition newCondition() {
        return null;
    }
}
