package com.yiban.javaBase.dev.utils;

import org.springframework.util.StopWatch;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class StopWatchDemo {
    public static void main(String[] args) {
        try {
            testSpringStopWatch();
//            testCommonStopWatch();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 测试Spring的StopWatch
     * @throws InterruptedException
     */
    public static void testSpringStopWatch() throws InterruptedException {
        // 强烈每一个秒表都给一个id，这样查看日志起来能够更加的精确
        // 至于Id 我觉得给UUID是可行的~
        StopWatch sw = new StopWatch(UUID.randomUUID().toString());

        //可以同时监控多个任务
        sw.start("起床");
        Thread.sleep(1000);
        System.out.println("当前任务名称：" + sw.currentTaskName());
        sw.stop();

        sw.start("洗漱");
        Thread.sleep(2000);
        System.out.println("当前任务名称：" + sw.currentTaskName());
        sw.stop();

        sw.start("锁门");
        Thread.sleep(500);
        System.out.println("当前任务名称：" + sw.currentTaskName());
        sw.stop();

        System.out.println("================ prettyPrint =====================");
        System.out.println(sw.prettyPrint()); // 这个方法打印在我们记录日志时是非常友好的  还有百分比的分析哦
        System.out.println("================ shortSummary =====================");
        System.out.println(sw.shortSummary());
        System.out.println("currentTaskName -> " + sw.currentTaskName()); // stop后它的值为null


        // 最后一个任务的相关信息
        System.out.println("LastTaskName -> " + sw.getLastTaskName());
        System.out.println("LastTaskInfo -> " + sw.getLastTaskInfo());

        // 任务总的耗时  如果你想获取到每个任务详情（包括它的任务名、耗时等等）可使用
        System.out.println("所有任务总耗时：" + sw.getTotalTimeMillis());
        System.out.println("任务总数：" + sw.getTaskCount());
        System.out.println("所有任务详情：" + sw.getTaskInfo()[0].toString()); // 拿到所有的任务
    }

    /**
     * 测试common-lang3的StopWatch
     * @throws InterruptedException
     */
    private static void testCommonStopWatch() throws InterruptedException {
        // createStarted()只有在common-lang3_3.6版本之后才有该方法
        org.apache.commons.lang3.time.StopWatch watch = org.apache.commons.lang3.time.StopWatch.createStarted(); //创建后立即start，常用
        //StopWatch watch = new StopWatch();
        //watch.start();

        Thread.sleep(1000);
        System.out.println("统计从开始到现在运行时间：" + watch.getTime() + "ms"); //1000ms

        Thread.sleep(1000);
        watch.split();//调用getSplitNanoTime()，返回的是start到split那时的时间差值。
        System.out.println("从start到此刻为止的时间：" + watch.getTime());
        System.out.println("从开始到第一个切入点运行时间：" + watch.getSplitTime()); //2245

        Thread.sleep(1000);
        watch.split();
        System.out.println("从开始到第二个切入点运行时间：" + watch.getSplitTime());

        watch.reset(); //重置后必须使用start方法
        watch.start();
        Thread.sleep(1000);
        System.out.println("重新开始后到当前运行时间是：" + watch.getTime()); //1000

        watch.suspend(); //暂停
        Thread.sleep(6000); //模拟暂停6秒钟

        watch.resume(); //上面suspend，这里要想重新统计，需要恢复一下
        System.out.println("恢复后执行的时间是：" + watch.getTime()); //1000  注意此时这个值还是1000，也就是说暂停之后的时间是不计入的

        watch.stop();
        System.out.println("花费的时间》》" + watch.getTime() + "ms"); //1002ms
        System.out.println("花费的时间》》" + watch.getTime(TimeUnit.SECONDS) + "s"); //1s 可以直接转成s
    }
}
