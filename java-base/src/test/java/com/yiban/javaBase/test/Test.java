package com.yiban.javaBase.test;


import com.yiban.javaBase.dev.concurrent.fork_join.SortTask;
import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Random;
import java.util.TimeZone;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;

import static junit.framework.TestCase.assertTrue;

/**
 * Created by duanwei on 2017/3/7.
 */
public class Test {
    private static final double SPLIT_SLOP = 1.1;   // 10% slop


    private static Unsafe theUnsafe;
    private static sun.misc.Unsafe UNSAFE;
    private int a;
    private int b;

    //使用方法
    private static Unsafe getUnsafeInstance() throws SecurityException,
            NoSuchFieldException, IllegalArgumentException,
            IllegalAccessException {
        Field theUnsafeInstance = Unsafe.class.getDeclaredField("theUnsafe");
        theUnsafeInstance.setAccessible(true);
        return (Unsafe) theUnsafeInstance.get(Unsafe.class);
    }

    //    static
//    {
//        try {
    ////通过这样的方式获得Unsafe的实力会抛出异常信息，因为在unsafe的源码中会有对安全性的检查
//            UNSAFE = sun.misc.Unsafe.getUnsafe();
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//    }
    public static void main(String[] args) throws Exception {
        System.out.println(5 & 3);

        //for循环还可以这样写，b不用定义类型，应该是推断出来的
//        for (int a = 1,b = a;;){
//            System.out.println(b);
//        }

        Class<?> k = Test.class;
        System.out.println(getUnsafeInstance().objectFieldOffset(k.getDeclaredField("a")));

        Integer a = new Integer(0);
        Integer b = new Integer(1);
        System.out.println(a == b);
        System.out.println(a);
        System.out.println(a == (a = b));
        System.out.println(a);
//        System.out.println(UNSAFE.objectFieldOffset(k.getDeclaredField("a")));


        Calendar curDate = Calendar.getInstance();
        TimeZone timeZone = curDate.getTimeZone();
        System.out.println(timeZone.getID());
        System.out.println(timeZone.getDisplayName());
        Calendar tommorowDate = new GregorianCalendar(curDate.get(Calendar.YEAR),
                curDate.get(Calendar.MONTH),
                curDate.get(Calendar.DATE) + 1, 0, 0, 0);
        int val = (int) (tommorowDate.getTimeInMillis() - curDate.getTimeInMillis()) / 1000;
        System.out.println(val);

        System.out.println(curDate.getTimeInMillis());

    }

    @org.junit.Test
    public void test() {
        int temp = 50;
        loop:
        for (int i = 0; i < 100; i++) {
            System.out.println("i = " + i);
            for (int j = 0; j < 100; j++) {
                if (temp == j) {
                    break loop;
                }
                System.out.println("j = " + j);
            }
        }
    }

    @org.junit.Test
    public void run() throws InterruptedException {
        ForkJoinPool forkJoinPool = new ForkJoinPool();
        Random rnd = new Random();
        int SIZE = 10000;
        long[] array = new long[SIZE];
        for (int i = 0; i < SIZE; i++) {
            array[i] = rnd.nextInt();
        }
        forkJoinPool.submit(new SortTask(array));

        forkJoinPool.shutdown();
        forkJoinPool.awaitTermination(1000, TimeUnit.SECONDS);

        for (int i = 1; i < SIZE; i++) {
            assertTrue(array[i - 1] < array[i]);
        }
    }

    @org.junit.Test
    public void test1(){
        System.out.println(Math.abs("console-consumer-90932".hashCode()) % 50);
    }
}
