package com.yiban.javaBase.dev.algorithm.bitmap;

import org.springframework.util.StopWatch;

import java.util.BitSet;

/**
 * @auther WEI.DUAN
 * @date 2019/10/30
 * @website http://blog.csdn.net/dwshmilyss
 *
 * 判断一个数是否在20亿个数中
 */
public class BitMapDemo {
    public static void main(String[] args) {
        long beforeMemory = Runtime.getRuntime().totalMemory();
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        BitSet bitSet = new BitSet(2000000000);

        for (int i = 0; i < 2000000000; i++) {
            if (i != 898989) {
                bitSet.set(i, true);
            }
        }
        long afterMemory = Runtime.getRuntime().totalMemory();
        stopWatch.stop();
        System.out.println("总共内存使用:" + (afterMemory - beforeMemory) / 1024 / 1024 + "MB");
        System.out.println("存入内存耗时:" + stopWatch.getLastTaskTimeMillis() + " 毫秒" + ",task = " + stopWatch.currentTaskName());

        stopWatch.start();
        boolean isExit1 = bitSet.get(898989);
        boolean isExit2 = bitSet.get(900000);
        stopWatch.stop();
        System.out.println("20个亿中" + (isExit1 ? "包含" : "不包含") + 898989);
        System.out.println("20个亿中" + (isExit2 ? "包含" : "不包含") + 900000);
        System.out.println("存入内存耗时:" + stopWatch.getLastTaskTimeMillis() + " 毫秒" + ",task = " + stopWatch.currentTaskName());
        System.out.println("task count = " + stopWatch.getTaskCount());
    }
}