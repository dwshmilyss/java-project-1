package com.yiban.javaBase.test.fastutil;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntBigArrayBigList;
import it.unimi.dsi.fastutil.ints.IntBigList;
import it.unimi.dsi.fastutil.ints.IntList;
import org.junit.Test;
import org.springframework.util.StopWatch;

import java.util.ArrayList;
import java.util.List;

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

    //cost time : 67
    IntBigList biglist = new IntBigArrayBigList();
    //cost time : 228
    List  list = new ArrayList();
    StopWatch stopWatch = new StopWatch();

    @Test
    public void test2(){
        stopWatch.start();
        for (int i = 0; i < 1000; i++) {
            for (int j = 0; j < 5000; j++) {
                biglist.add(j);
            }
        }
        System.out.println(biglist.size64());
        stopWatch.stop();
        System.out.println(stopWatch.getTotalTimeMillis());
    }
}