package com.yiban.javaBase.dev.concurrent.unsafe;

import sun.misc.Unsafe;

import java.lang.reflect.Field;

/**
 * @auther WEI.DUAN
 * @date 2018/1/22
 * @website http://blog.csdn.net/dwshmilyss
 */
public class UnsafeDemo {
    private static Unsafe theUnsafe;

    static {
        try {
            theUnsafe = getUnsafeInstance();
        } catch (Exception e) {
            throw new Error(e);
        }
    }

    /**
     * 正常情况下，程序员是无法直接获取Unsafe的实例的
     * 但是可以通过反射的方式获取
     *
     * @return
     * @throws SecurityException
     * @throws NoSuchFieldException
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     */
    private static Unsafe getUnsafeInstance() throws SecurityException,
            NoSuchFieldException, IllegalArgumentException,
            IllegalAccessException {
        Field theUnsafeInstance = Unsafe.class.getDeclaredField("theUnsafe");
        theUnsafeInstance.setAccessible(true);
        return (Unsafe) theUnsafeInstance.get(Unsafe.class);
    }

    public static void main(String[] args) {
        System.out.println(theUnsafe.allocateMemory(1l));
        /**
         * 利用Unsafe类获取某个类的内存地址
         */
        //JDK默认开启指针压缩
        long addressOfSampleClass = theUnsafe.getLong(SampleClass.class, 160L);
        System.out.println("addressOfSampleClass = " + addressOfSampleClass);

        //关闭指针压缩
        long addressOfSampleClassByCompress = theUnsafe.getInt(SampleClass.class, 84L);
        System.out.println("addressOfSampleClassByCompress = " + addressOfSampleClassByCompress);
    }
}

class SampleBaseClass {

    protected short s = 20;
}

final class SampleClass extends SampleBaseClass {

    private final static byte b = 100;

    private int i = 5;
    private long l = 10;

    public SampleClass() {

    }

    public SampleClass(int i, long l) {
        this.i = i;
        this.l = l;
    }

    public static byte getB() {
        return b;
    }

    public int getI() {
        return i;
    }

    public void setI(int i) {
        this.i = i;
    }

    public long getL() {
        return l;
    }

    public void setL(long l) {
        this.l = l;
    }
}
