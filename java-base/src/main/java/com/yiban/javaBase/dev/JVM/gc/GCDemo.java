package com.yiban.javaBase.dev.JVM.gc;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * gc demo
 * ### 二、 默认参数
 *
 * @auther WEI.DUAN
 * @date 2018/1/9
 * @website http://blog.csdn.net/dwshmilyss
 */
public class GCDemo {

    //单位 M
    private static final int _1M = 1024 * 1024;

    public static void main(String[] args) {
        test5();
    }

    /**
     * server模式默认的gc算法：Parallel Scavenge(Young) + Parallel Old (old)
     * -verbose:gc 显示GC信息
     * -verbose:class 显示类加载信息
     * -verbose:jni 显示jni调用信息 用于native方法调试
     * -Xms60m
     * -Xmx60m
     * -Xmn20m
     * -XX:NewRatio=2 ( 若 Xms = Xmx, 并且设定了 Xmn, 那么该项配置就不需要配置了 )
     * -XX:SurvivorRatio=8
     * -XX:PermSize=30m
     * -XX:MaxPermSize=30m
     * -XX:+PrintGCDetails
     */
    static void test() {
        //申请1M的内存空间
        byte[] bytes = new byte[_1M];
        bytes = null;//断开引用链
        System.gc();//通知GC
        System.out.println();

        bytes = new byte[_1M];  //重新申请 1M 大小的内存空间
        bytes = new byte[_1M];  //再次申请 1M 大小的内存空间
        System.gc();
        System.out.println();
    }

    static void test1() {
        byte[] b1 = new byte[_1M]; // allocate 1M
        byte[] b2 = new byte[_1M * 2]; // allocate 2M
        byte[] b3 = new byte[_1M]; // allocate 1M, 发生年轻代GC(Minor GC)
    }

    /**
     * 测试对象在Eden区的分配
     * VM参数：-verbose:gc -Xms20M -Xmx20M -Xmn10M -XX:PrintGCDetails -XX:SurvivorRatio=8
     * Eden:8m 2个survivor为1m
     */
    static void test2() {
        byte[] alloc1, alloc2, alloc3, alloc4;
        alloc1 = new byte[2 * _1M];
        System.out.println("alloc1 " + printAddressOf(alloc1));
        alloc2 = new byte[2 * _1M];
        System.out.println("alloc2 " + printAddressOf(alloc2));
        alloc3 = new byte[2 * _1M];
        System.out.println("alloc3 " + printAddressOf(alloc3));
        alloc4 = new byte[3 * _1M];
        System.out.println("alloc4 " + printAddressOf(alloc4));
    }

    static String printAddressOf(Object object) {
        return Integer.toHexString(System.identityHashCode(object));
    }

    /**
     * 设定阈值 超过阈值直接进入年老代
     * VM参数：-verbose:gc -Xms20M -Xmx20M -Xmn10M -XX:+PrintGCDetails -XX:SurvivorRatio=8 -XX:PretenureSizeThreshold=3145728
     * PretenureSizeThreshold在Paralle Scavenge中无效
     */
    static void test3() {
        System.out.println("aa");
        byte[] a1 = new byte[4 * _1M];
    }

    static List list;

    /**
     * VM参数：-verbose:gc -Xms1024M -Xmx1024M  -XX:SurvivorRatio=4 -XX:+PrintGCDetails
     */
    static void test4(){
        list = new ArrayList<>();
        for (int i = 1; i <= 900; i++) {
            list.add(new byte[_1M]);
        }
        System.out.println(list.size());
    }

    /**
     * -verbose:gc -XX:MaxDirectMemorySize=10m -XX:+PrintGC -XX:+PrintGCDetails -XX:+DisableExplicitGC
      */
    static void test5(){
        for (int i = 0; i < 100000; i++) {
            ByteBuffer.allocateDirect(128);
        }
        System.out.println("done");
    }
}

