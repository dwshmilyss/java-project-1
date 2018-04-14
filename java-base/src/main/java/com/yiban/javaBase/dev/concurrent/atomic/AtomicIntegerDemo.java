package com.yiban.javaBase.dev.concurrent.atomic;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * AtomicInteger类中主要有三个成员属性：
 * private static final Unsafe unsafe = Unsafe.getUnsafe();
 * private static final long valueOffset;
 * private volatile int value;
 * <p/>
 * 1、Unsafe类
 * 2、valueOffset表示的是变量值在内存中的偏移地址，因为Unsafe就是根据内存偏移地址获取数据的原值的
 * 3、value是用volatile修饰的，保证多个线程之间修改立即可见和防止指令重排序
 *
 * @auther WEI.DUAN
 * @date 2018/1/22
 * @website http://blog.csdn.net/dwshmilyss
 */
public class AtomicIntegerDemo {
    public static void main(String[] args) {
        //初始化值为1
        AtomicInteger atomicInteger = new AtomicInteger(1);
        //add 2后返回
        System.out.println("atomicInteger = " + atomicInteger.addAndGet(2));
        // 减1后返回
        System.out.println("atomicInteger = " + atomicInteger.decrementAndGet());
        //比较修改 和当前的内存值比较 如果匹配就修改为3 并返回ture，反之返回false。这里就会返回false
        System.out.println("atomicInteger = " + atomicInteger.compareAndSet(1, 3));
    }
}
