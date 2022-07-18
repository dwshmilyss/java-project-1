package com.yiban.javaBase.test.string;

import io.netty.util.collection.IntObjectHashMap;
import it.unimi.dsi.fastutil.ints.IntList;
import org.junit.Test;
import org.springframework.util.StopWatch;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

/**
 * @auther WEI.DUAN
 * @date 2018/11/27
 * @website http://blog.csdn.net/dwshmilyss
 */
public class StringBuilderTest {

    Map<Integer,String> hashMap = new HashMap<>();
    IntObjectHashMap intObjectHashMap = new IntObjectHashMap();

    @Test
    public void test(){
        StringBuilder stringBuilder = new StringBuilder(496);
        System.out.println(hash(new Integer(10)));
        System.out.println(hash(new Long(10)));
    }

    @Test
    public void test1(){
        int[] src = {1,2,3,4,5,6};
        int[] des = new int[5];
        System.arraycopy(src,1,des,0,5);
        for (int i = 0; i < des.length; i++) {
            System.out.println("i = " + des[i]);
        }
    }

    @Test
    public void test2(){
        int[] src = {1,2,4,3,6,5};
        Arrays.sort(src);
        for (int i = 0; i < src.length; i++) {
            System.out.println(src[i]);
        }
    }

    @Test
    public void test3(){
        Map<Integer,String> map = new HashMap<>();
        map.put(1,"aa");

        System.out.println((int) ((float) 10 / 0.75F + 1.0F));

//        EnumMap<Integer,String> enumMap = new EnumMap<Integer, String>();

        IntObjectHashMap intObjectHashMap = new IntObjectHashMap();
        intObjectHashMap.put(1,"aa");
        intObjectHashMap.put(2,"bb");
        intObjectHashMap.put(3,"cc");

    }


    @Test
    public void test4(){
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        /**
         * hashmap cost time : 4119
         * intObjectHashMap cost time : 1675
         */
        add();
        stopWatch.stop();
        System.out.println(stopWatch.getTotalTimeMillis());
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test5(){
    }



    final void add()
    {
        for (int i = 0; i < 10000;i++) {
            for(int j = 0;j<50000;j++)
            {
                if(intObjectHashMap.get(j) == null)
                {
                    intObjectHashMap.put(j, "小毛驴");
                }
            }
        }
    }


    final int hash(Object k) {
        int h = 0;

        h ^= k.hashCode();

        // This function ensures that hashCodes that differ only by
        // constant multiples at each bit position have a bounded
        // number of collisions (approximately 8 at default load factor).
        h ^= (h >>> 20) ^ (h >>> 12);
        return h ^ (h >>> 7) ^ (h >>> 4);
    }
}