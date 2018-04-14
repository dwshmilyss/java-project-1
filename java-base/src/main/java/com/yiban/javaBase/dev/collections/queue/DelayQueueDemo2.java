package com.yiban.javaBase.dev.collections.queue;

import org.springframework.util.StopWatch;

import java.util.Iterator;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

/**
 * 延时队列demo
 *
 * @auther WEI.DUAN
 * @create 2017/5/2
 * @blog http://blog.csdn.net/dwshmilyss
 */
public class DelayQueueDemo2 {
    public static void main(String[] args) {
        StopWatch stopWatch = new StopWatch();
        DelayQueue dq = new DelayQueue();
        DeleyedTest ob1 = new DeleyedTest(10);
        DeleyedTest ob2 = new DeleyedTest(5);

        DeleyedTest ob3 = new DeleyedTest(15);
        dq.offer(ob1);
        dq.offer(ob2);
        dq.offer(ob3);

//        try {
//            stopWatch.start();
//            DeleyedTest a1 = (DeleyedTest) dq.take();
//            System.out.println(a1.deleyTime);
//            stopWatch.stop();
//            System.out.println(stopWatch.getTotalTimeSeconds());
//            System.out.println(dq.size());
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }

        Iterator itr = dq.iterator();
        while (itr.hasNext()) {
            DeleyedTest dt = (DeleyedTest) itr.next();
            System.out.println(dt.deleyTime);
        }

        System.out.println(dq.size());
    }

    static class DeleyedTest implements Delayed {
        public long deleyTime = 0;
        public long tempTime = 0;

        DeleyedTest(long deleyTime) {
            this.deleyTime = deleyTime;
            this.tempTime = TimeUnit.NANOSECONDS.convert(deleyTime, TimeUnit.SECONDS) + System.nanoTime();
        }

        @Override
        public int compareTo(Delayed ob) {
            if (this.deleyTime < ((DeleyedTest) ob).deleyTime) {
                return -1;
            } else if (this.deleyTime > ((DeleyedTest) ob).deleyTime) {
                return 1;
            }
            return 0;
        }

        @Override
        public long getDelay(TimeUnit unit) {
            return unit.convert(tempTime - System.nanoTime(), TimeUnit.NANOSECONDS);
//            return 1;
        }

    }
}
