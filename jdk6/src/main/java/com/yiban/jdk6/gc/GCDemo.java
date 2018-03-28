package com.yiban.jdk6.gc;

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
        System.out.println(System.getProperty("java.vm.name"));
        test2();
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
     * VM参数：-verbose:gc -XX:+PrintTLAB -Xms20M -Xmx20M -Xmn10M -XX:+PrintGCDetails -XX:SurvivorRatio=8
     * Eden:8m 2个survivor为1m
     *      * -XX:+PrintTLAB 查看TLAB(Thread-Local Allocation Buffers)
     *HotSpot虚拟机使用了两种技术来加快内存分配。他们分别是是”bump-the-pointer“和“TLABs（Thread-Local Allocation Buffers）”。
     Bump-the-pointer技术跟踪在伊甸园空间创建的最后一个对象。这个对象会被放在伊甸园空间的顶部。如果之后再需要创建对象，只需要检查伊甸园空间是否有足够的剩余空间。
     如果有足够的空间，对象就会被创建在伊甸园空间，并且被放置在顶部。这样以来，每次创建新的对象时，只需要检查最后被创建的对象。这将极大地加快内存分配速度。
     但是，如果我们在多线程的情况下，事情将截然不同。如果想要以线程安全的方式以多线程在伊甸园空间存储对象，不可避免的需要加锁，而这将极大地的影响性能。
     TLABs 是HotSpot虚拟机针对这一问题的解决方案。该方案为每一个线程在伊甸园空间分配一块独享的空间，这样每个线程只访问他们自己的TLAB空间，
     再与bump-the-pointer技术结合可以在不加锁的情况下分配内存。
     */
    static void test2(){
        byte[] a1,a2,a3,a4;
        a1 = new byte[2*_1M];
        a2 = new byte[2*_1M];
        a3 = new byte[2*_1M];
        a4 = new byte[4*_1M];//minor gc
    }

    /**
     * 设定对象大小阈值 超过阈值直接进入年老代
     * VM参数：-verbose:gc -Xms20M -Xmx20M -Xmn10M -XX:+PrintGCDetails -XX:SurvivorRatio=8 -XX:PretenureSizeThreshold=3145728
     */
    static void test3(){
        byte[] a1 = new byte[4*_1M];
    }

    /**
     * 设定存活次数阈值 超过阈值进入年老代
     * VM参数：-verbose:gc -Xms20M -Xmx20M -Xmn10M -XX:+PrintGCDetails -XX:SurvivorRatio=8 -XX:MaxTenuringThreshold=1 -XX:+PrintTenuringDistribution
     */
    static void test4(){
        byte[] a1,a2,a3;
        a1 = new byte[_1M/4];
        a2 = new byte[4*_1M];
        a3 = new byte[4*_1M];
        a3 = null;
        a3 = new byte[4*_1M];
    }
}
