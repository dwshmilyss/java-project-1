package com.yiban.javaBase.dev.collections.queue;

import java.util.concurrent.*;

/**
 * LinkedBlockingDeque的测试代码
 * LinkedBlockingDeque 是一个双端队列，提供first和last指针的操作，可以实现类似LIFO的数据结构
 * 例如：offer 1,2,3  peekLast()
 * <p/>
 * LinkedBlockingQueue虽然在内部也维护了2个指针 head tail 但是并没有暴露出操作的API
 *
 * @auther WEI.DUAN
 * @date 2017/4/25
 * @website http://blog.csdn.net/dwshmilyss
 */
public class LinkedBlockingDequeDemo {
    public static void main(String[] args) {
        BlockingDeque deque = new LinkedBlockingDeque<Integer>();
        deque.offerFirst(1);
        deque.offerFirst(100);
        deque.offer(10);
        System.out.println(deque.peek());

        BlockingQueue queue = new LinkedBlockingQueue<Integer>();
        queue.offer(1);
        System.out.println(queue.size());

        BlockingQueue array = new ArrayBlockingQueue<Integer>(1);
        array.offer(1);
        array.offer(2);
        System.out.println(array.size() + " : " + array.peek());
    }
}
