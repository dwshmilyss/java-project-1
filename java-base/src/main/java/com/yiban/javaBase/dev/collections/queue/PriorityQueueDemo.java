package com.yiban.javaBase.dev.collections.queue;

import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Queue;

/**
 * Created by duanwei on 2017/3/6.
 * 优先级队列是不同于先进先出队列的另一种队列。每次从队列中取出的是具有最高优先权的元素。
 * PriorityQueue是从JDK1.5开始提供的新的数据结构接口。
 * 如果不提供Comparator的话，优先队列中元素默认按自然顺序排列，也就是数字默认是小的在队列头，字符串则按字典序排列。
 */
public class PriorityQueueDemo {
    private String name;
    private int population;

    public PriorityQueueDemo(String name, int population) {
        this.name = name;
        this.population = population;
    }

    public static void main(String args[]) {
        Comparator<PriorityQueueDemo> OrderIsdn = new Comparator<PriorityQueueDemo>() {
            @Override
            public int compare(PriorityQueueDemo o1, PriorityQueueDemo o2) {
                // TODO Auto-generated method stub
                int numbera = o1.getPopulation();
                int numberb = o2.getPopulation();
                if (numberb > numbera) {
                    return 1;
                } else if (numberb < numbera) {
                    return -1;
                } else {
                    return 0;
                }
            }
        };
        Queue<PriorityQueueDemo> priorityQueue = new PriorityQueue<PriorityQueueDemo>(11, OrderIsdn);

        PriorityQueueDemo t1 = new PriorityQueueDemo("t1", 1);
        PriorityQueueDemo t3 = new PriorityQueueDemo("t3", 3);
        PriorityQueueDemo t2 = new PriorityQueueDemo("t2", 2);
        PriorityQueueDemo t4 = new PriorityQueueDemo("t4", 0);
        priorityQueue.add(t1);
        priorityQueue.add(t3);
        priorityQueue.add(t2);
        priorityQueue.add(t4);
        System.out.println(priorityQueue.poll().toString());
        System.out.println(priorityQueue.poll().toString());
        System.out.println(priorityQueue.poll().toString());
        System.out.println(priorityQueue.poll().toString());
    }

    public String getName() {
        return this.name;
    }

    public int getPopulation() {
        return this.population;
    }

    @Override
    public String toString() {
        return getName() + " - " + getPopulation();
    }
}
