package com.yiban.javaBase.dev.JVM.gc;

/**
 * gc demo
 * ### 二、 默认参数
 * @auther WEI.DUAN
 * @date 2018/1/9
 * @website http://blog.csdn.net/dwshmilyss
 */
public class GCDemo {

    //单位 M
    public static final int M = 1024*2014*1;

    public static void main(String[] args) {
        test();
    }

    /**
     * -verbose:gc 显示GC信息
     * -verbose:class 显示类加载信息
     * -verbose:jni 显示jni调用信息 用于native方法调试
     -Xms60m
     -Xmx60m
     -Xmn20m
     -XX:NewRatio=2 ( 若 Xms = Xmx, 并且设定了 Xmn, 那么该项配置就不需要配置了 )
     -XX:SurvivorRatio=8
     -XX:PermSize=30m
     -XX:MaxPermSize=30m
     -XX:+PrintGCDetails
     */
    static void test(){
        //申请1M的内存空间
        byte[] bytes = new byte[1*M];
        bytes = null;//断开引用链
        System.gc();//通知GC
        System.out.println();

        bytes = new byte[1 * M];  //重新申请 1M 大小的内存空间
        bytes = new byte[1 * M];  //再次申请 1M 大小的内存空间
        System.gc();
        System.out.println();
    }
}
