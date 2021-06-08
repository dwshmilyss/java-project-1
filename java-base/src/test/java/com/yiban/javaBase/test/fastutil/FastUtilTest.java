package com.yiban.javaBase.test.fastutil;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntBigArrayBigList;
import it.unimi.dsi.fastutil.ints.IntBigList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.objects.ObjectBigArrayBigList;
import org.junit.Test;
import org.springframework.util.StopWatch;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @auther WEI.DUAN
 * @date 2018/11/29
 * @website http://blog.csdn.net/dwshmilyss
 */
public class FastUtilTest {
    @Test
    public void test1(){
        IntList intList = new IntArrayList();
        for(int i = 0; i < 1000; i++){
            intList.add(i);
        }
        //取值
        int value = intList.getInt(0);
        System.out.println(value);// 0

        int[] values = intList.toIntArray();
        System.out.println(values.length);


        //===========IntBigList
        IntBigList biglist = new IntBigArrayBigList();

        biglist.add(0);
        biglist.add(1);
        biglist.add(2);
        long size = biglist.size64();
        System.out.println("size = " + size);
    }


    IntBigList intBigArrayBigList = new IntBigArrayBigList();
    IntArrayList intArrayList = new IntArrayList();
    List<Integer> originlIntArrayList = new ArrayList();

    //存string这两者的效率差不多
    List<String> originlStringArrayList = new ArrayList();
    ObjectBigArrayBigList stringArrayBigList = new ObjectBigArrayBigList();
    StopWatch stopWatch = new StopWatch();

    @Test
    public void testIntBigArrayBigList(){
        stopWatch.start();
        //cost time : 67ms
        for (int i = 0; i < 1000; i++) {
            for (int j = 0; j < 5000; j++) {
                intBigArrayBigList.add(j);
            }
        }
        System.out.println(intBigArrayBigList.size64());
        stopWatch.stop();
        System.out.println(stopWatch.getTotalTimeMillis());
    }

    @Test
    public void testIntArrayList(){
        stopWatch.start();
        //cost time : 51ms
        for (int i = 0; i < 1000; i++) {
            for (int j = 0; j < 5000; j++) {
                intArrayList.add(j);
            }
        }
        System.out.println(intArrayList.size());
        stopWatch.stop();
        System.out.println(stopWatch.getTotalTimeMillis());
    }

    @Test
    public void testArrayList(){
        stopWatch.start();
        //cost time : 149ms
        for (int i = 0; i < 1000; i++) {
            for (int j = 0; j < 5000; j++) {
                originlIntArrayList.add(j);
            }
        }
        System.out.println(originlIntArrayList.size());
        stopWatch.stop();
        System.out.println(stopWatch.getTotalTimeMillis());
    }

    @Test
    public void testObjectBigArrayBigList(){
        stopWatch.start();
        //cost time : 5414ms
        for (int i = 0; i < 1000; i++) {
            for (int j = 0; j < 5000; j++) {
                stringArrayBigList.add(UUID.randomUUID().toString());
            }
        }
        System.out.println(stringArrayBigList.size());
        stopWatch.stop();
        System.out.println(stopWatch.getTotalTimeMillis());
    }

    @Test
    public void testStringArrayList(){
        stopWatch.start();
        //cost time : 5414ms
        for (int i = 0; i < 1000; i++) {
            for (int j = 0; j < 5000; j++) {
                originlStringArrayList.add(UUID.randomUUID().toString());
            }
        }
        System.out.println(originlStringArrayList.size());
        stopWatch.stop();
        System.out.println(stopWatch.getTotalTimeMillis());
    }
}