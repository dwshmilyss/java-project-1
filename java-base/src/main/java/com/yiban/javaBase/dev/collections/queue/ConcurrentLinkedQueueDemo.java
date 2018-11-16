package com.yiban.javaBase.dev.collections.queue;

import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * 线程安装的并发非阻塞队列
 *
 * @auther WEI.DUAN
 * @date 2017/5/9
 * @website http://blog.csdn.net/dwshmilyss
 */
public class ConcurrentLinkedQueueDemo {
    public static void main(String[] args) {
        ConcurrentLinkedQueue concurrentLinkedQueue = new ConcurrentLinkedQueue<String>();
        concurrentLinkedQueue.offer("a");
        concurrentLinkedQueue.offer("b");
        concurrentLinkedQueue.offer("c");

        for (Iterator<String> iterator = concurrentLinkedQueue.iterator();iterator.hasNext();){
            String item = iterator.next();
            System.out.println(item);
        }


        System.out.println(concurrentLinkedQueue.remove());
        System.out.println(concurrentLinkedQueue.remove());
        System.out.println(concurrentLinkedQueue.remove());
        //只有三个元素 第四次调用remove就会抛异常
//        System.out.println(concurrentLinkedQueue.remove());
        //但是调用poll只是返回Null 不会抛出异常
        System.out.println(concurrentLinkedQueue.poll());



//        Node node = new Node();
//        System.out.println(node.next);
//        /**
//         * 通过CAS方法更新node的next属性
//         * 原子操作
//         */
//        boolean flag = node.casNext(null,new Node());
//        System.out.println(flag);
//        System.out.println(node.next);
    }


    private static class Node {

        private static final long nextOffset;
        /**
         * 获取Unsafe的方法
         * 获取了以后就可以愉快的使用CAS啦
         *
         * @return
         */
        private static Unsafe theUnsafe;

        static {
            try {
                theUnsafe = getUnsafeInstance();
                Class<?> k = Node.class;
                nextOffset = theUnsafe.objectFieldOffset(k.getDeclaredField("next"));
                System.out.println("nextOffset = " + nextOffset);
                System.out.println("nextOffset = " + theUnsafe.objectFieldOffset(k.getDeclaredField("next1")));
            } catch (Exception e) {
                throw new Error(e);
            }
        }

        volatile Node next;
        volatile Node next1;

        //使用方法
        private static Unsafe getUnsafeInstance() throws SecurityException,
                NoSuchFieldException, IllegalArgumentException,
                IllegalAccessException {
            Field theUnsafeInstance = Unsafe.class.getDeclaredField("theUnsafe");
            theUnsafeInstance.setAccessible(true);
            return (Unsafe) theUnsafeInstance.get(Unsafe.class);
        }

        /**
         * 使用Unsafe CAS方法
         *
         * @param cmp 目标值与cmp比较，如果相等就更新返回true；如果不相等就不更新返回false；
         * @param val 需要更新的值；
         * @return
         */
        boolean casNext(Node cmp, Node val) {
            /**
             * compareAndSwapObject(Object var1, long var2, Object var3, Object var4)
             * var1 操作的对象
             * var2 操作的对象属性
             * var3 var2与var3比较，相等才更新
             * var4 更新值
             */
            boolean flag = theUnsafe.compareAndSwapObject(this, nextOffset, cmp, val);
            return flag;
        }
    }
}
