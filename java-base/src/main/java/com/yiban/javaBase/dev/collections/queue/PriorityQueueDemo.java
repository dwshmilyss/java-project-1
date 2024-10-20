package com.yiban.javaBase.dev.collections.queue;

import lombok.Data;

import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Created by duanwei on 2017/3/6.
 * 优先级队列是不同于先进先出队列的另一种队列。每次从队列中取出的是具有最高优先权的元素。
 * PriorityQueue是从JDK1.5开始提供的新的数据结构接口。
 * 如果不提供Comparator的话，优先队列中元素默认按自然顺序排列，也就是数字默认是小的在队列头，字符串则按字典序排列。
 * 主要特点
 * 1/ 非线程安全：PriorityQueue 不是线程安全的。如果需要在多线程环境中使用，可以考虑使用 PriorityBlockingQueue。
 * 2/ 无界队列：默认情况下，PriorityQueue 的大小是无界的，能够根据需要动态扩展。
 * 3/ 不保证顺序：同一优先级的元素出队的顺序是不确定的。
 */
public class PriorityQueueDemo {

    public static void main(String args[]) throws InterruptedException {
//        testPriorityQueue();
        testPriorityBlockingQueue();
    }

    private static void testPriorityBlockingQueue() throws InterruptedException {
        PriorityBlockingQueue<Task> priorityBlockingQueue = new PriorityBlockingQueue<>(10, new TaskComparatorWithDesc() {});
        Task t1 = new Task("task 1", 1);
        Task t3 = new Task("task 3", 3);
        Task t2 = new Task("task 2", 2);
        Task t4 = new Task("task 4", 0);
        //入队列的时候是顺序入队
        priorityBlockingQueue.add(t1);
        priorityBlockingQueue.add(t3);
        priorityBlockingQueue.add(t2);
        priorityBlockingQueue.add(t4);
        //出队列的时候按照优先级，优先级高的先出队
//        while (!priorityBlockingQueue.isEmpty()) {
////            Task task = priorityBlockingQueue.poll();
//            Task task = priorityBlockingQueue.take();
//            System.out.println(task.name + " with priority " + task.priority);
//        }

        // 因为队列中只有4个元素，这里却要取出5个，所以定义一个超时时间，如果超过5s还没有新的元素加入，那就抛出异常
        // 这里如果用take()的话，那么就会一直阻塞
        for (int i = 0; i < 5; i++) {
//            Task task = priorityBlockingQueue.poll(5, TimeUnit.SECONDS);
            Task task = priorityBlockingQueue.take();
            System.out.println(task.name + " with priority " + task.priority);
        }
    }

    private static void testPriorityQueue() {
        //        Queue<Task> priorityQueue = new PriorityQueue<Task>(11, new TaskComparatorWithAsc());//升序
        PriorityQueue<Task> priorityQueue = new PriorityQueue<Task>(11, new TaskComparatorWithDesc());//降序

        Task t1 = new Task("task 1", 1);
        Task t3 = new Task("task 3", 3);
        Task t2 = new Task("task 2", 2);
        Task t4 = new Task("task 4", 0);
        //入队列的时候是顺序入队
        priorityQueue.add(t1);
        priorityQueue.add(t3);
        priorityQueue.add(t2);
        priorityQueue.add(t4);
        //出队列的时候按照优先级，优先级高的先出队
        while (!priorityQueue.isEmpty()) {
            Task task = priorityQueue.poll();
            System.out.println(task.name + " with priority " + task.priority);
        }
    }

    @Data
    static class Task{
        private String name;
        private int priority;//作为优先级的比较字段

        public Task(String name, int priority) {
            this.name = name;
            this.priority = priority;
        }

        @Override
        public String toString() {
            return "Task{" +
                    "name='" + name + '\'' +
                    ", priority=" + priority +
                    '}';
        }
    }

    /**
     * 定义优先级顺序：升序
     */
    static class TaskComparatorWithAsc implements Comparator<Task> {
        @Override
        public int compare(Task o1, Task o2) {
            return Integer.compare(o1.priority,o2.priority);//升序
        }
    }

    /**
     * 定义优先级顺序：降序
     */
    static class TaskComparatorWithDesc implements Comparator<Task> {
        @Override
        public int compare(Task o1, Task o2) {
            return Integer.compare(o2.priority,o1.priority);//降序
        }
    }

}
