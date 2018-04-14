package com.yiban.javaBase.dev.JVM.gc;

import java.nio.ByteBuffer;

/**
 * 堆外内存是在jvm heap之外分配的。所以它不直接受GC的影响
 * 但是可以通过触发System.gc()来回收堆外内存。因为堆外内存所关联的对象引用 DirectByteBuffer 是在heap中的。它记录了堆外内存的地址和大小
 * 所以可以通过操作 DirectByteBuffer 来操作堆外内存
 *
 * @auther WEI.DUAN
 * @date 2018/1/23
 * @website http://blog.csdn.net/dwshmilyss
 */
public class DirectBufferDemo {
    public static void main(String[] args) {
        //这个声明在Heap中是不占用空间的
        ByteBuffer buffer = ByteBuffer.allocateDirect(1024 * 1024 * 1);
        //这个就占用空间了
//        byte[] bytes = new byte[1024*1024*5];
    }
}
