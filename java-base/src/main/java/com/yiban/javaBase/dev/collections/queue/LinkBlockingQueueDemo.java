package com.yiban.javaBase.dev.collections.queue;

import java.util.concurrent.LinkedBlockingQueue;

public class LinkBlockingQueueDemo {
    public static void main(String[] args) throws InterruptedException {
        LinkedBlockingQueue<String> linkedBlockingQueue = new LinkedBlockingQueue<>(10);
        linkedBlockingQueue.offer("a");
        linkedBlockingQueue.offer("a");
        linkedBlockingQueue.offer("b");
        linkedBlockingQueue.offer("c");
        System.out.println(linkedBlockingQueue.size());
        System.out.println(linkedBlockingQueue.remove("a"));
        System.out.println(linkedBlockingQueue.remove("a"));
        System.out.println(linkedBlockingQueue.size());
        System.out.println(linkedBlockingQueue.peek());
    }
}
