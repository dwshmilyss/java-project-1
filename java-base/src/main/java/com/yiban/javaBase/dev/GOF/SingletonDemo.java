package com.yiban.javaBase.dev.GOF;

/**
 * Created by duanwei on 2017/3/23.
 * 单例模式
 * 懒汉模式 声明的实例成员变量不初始化
 * 饥汉模式 声明的实例成员变量初始化
 */
public class SingletonDemo {
    //防止jvm指令重排序
    public static volatile SingletonDemo instance;

    /**
     * 第一种实现方式(懒汉模式)
     */
    private SingletonDemo() {
    }

    public static SingletonDemo getInstance() {
        //双重检查 第一次判断是为了减少阻塞，因为下面是一个同步代码块
        if (instance == null) {
            synchronized (SingletonDemo.class) {
                //第二次检查是因为jdk会乱序执行一个非原子的操作，例如：new JDBCToHiveUtils();
                //这个new 会被jvm分为三个CPU指令
                //1、memory =allocate();    //1：分配对象的内存空间
                //2、ctorInstance(memory);  //2：初始化对象
                //3、instance =memory;     //3：设置instance指向刚分配的内存地址
                /*上面操作2依赖于操作1，但是操作3并不依赖于操作2，所以JVM是可以针对它们进行指令的优化重排序的，经过重排序后如下：
                memory =allocate();    //1：分配对象的内存空间
                instance =memory;     //3：instance指向刚分配的内存地址，此时对象还未初始化
                ctorInstance(memory);  //2：初始化对象
                所以在多线程的情况下，一个线程在执行完第三步后，另外一个线程就会判断到instance!=null，这时候返回的话就会报错
                */
                if (null == instance) {
                    instance = new SingletonDemo();
                }
            }
        }
        return instance;
    }
}
