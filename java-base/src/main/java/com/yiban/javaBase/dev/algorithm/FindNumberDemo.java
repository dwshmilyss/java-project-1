package com.yiban.javaBase.dev.algorithm;

import java.util.*;

/**
 * 在很大的数据集中查找数字的例子，一般使用bit
 *
 * @auther WEI.DUAN
 * @date 2019/5/21
 * @website http://blog.csdn.net/dwshmilyss
 */
public class FindNumberDemo {
    /**
     * 40亿个整数中找到那个唯一重复的数字
     * 这里利用bitset不存储重复元素的原理来操作
     * 一个1G的空间，有 8*1024*1024*1024=8.58*10^9bit，也就是可以表示85亿个不同的数
     */
    public static void test1() {
        BitSet bitSet = new BitSet();
        //测试数据集 这里只用几个数字 实际情景可能是上亿的数字
        int[] nums = {1, 2, 3, 4, 5, 6, 1};
        for (int num : nums) {
            if (bitSet.get(num)) {
                System.out.println(num);
                break;
            } else {
                bitSet.set(num);
            }
        }
        System.out.println(bitSet.size());
    }


    /**
     * 从十亿数字中找出重复出现次数最多的数以及次数并按次数排序
     */
    public static void test2() {
        //数据集（假设是10E个）
        final int input[] = {2389, 2389, 8922, 3382, 6982, 5231, 8934, 8923, 7593
                , 4322, 7922, 6892, 5224, 4829, 3829, 8934, 8922
                , 6892, 6872, 4682, 6723, 8923, 3492, 9527, 8923
                , 7593, 7698, 7593, 7593, 7593, 8922, 9527, 4322
                , 8934, 4322, 3382, 5231, 5231, 4682, 9527, 9527};

        final int input1[] = {123, 123, 123, 123, 123};

        int sort[] = new int[1000];
        for (int i = 0, len = sort.length; i < len; i++) {
            sort[i] = 0;
        }
        Map<Integer, Integer> numCountMap = new HashMap<>();
        for (int number : input1) {
            //这里会计算数字出现过的次数
            int existTimes = (sort[number >>> 4] >>> (2 * (number % 16))) & (1 | 1 << 1);
            //Increase counter in sort array.
            if (existTimes <= 2) {
                existTimes++;
                //这两个地方的写法有点难搞
                //set two bit zero
                sort[number >>> 4] &= ~((1 | 1 << 1) << (2 * (number % 16)));
                //set increased bit value
                sort[number >>> 4] |= existTimes << (2 * (number % 16));
                //只存储>=3次的数字
                if (3 == existTimes) {
                    numCountMap.put(number, existTimes);
                }
            } else {
                //如果已经出现了3次，继续增加次数
                if (3 == existTimes) {
                    int mapCounter = numCountMap.get(number).intValue();
                    mapCounter++;
                    numCountMap.put(number, existTimes);
                }
            }
        }
        //按map的value排序
        List<Map.Entry<Integer, Integer>> list = new ArrayList<>(numCountMap.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<Integer, Integer>>() {
            @Override
            public int compare(Map.Entry<Integer, Integer> o1, Map.Entry<Integer, Integer> o2) {
                //降序
                return o2.getValue().compareTo(o1.getValue());
            }
        });

        for (Map.Entry<Integer, Integer> entry : list) {
            System.out.println(entry.getKey() + "  " + entry.getValue());
        }
    }

    /**
     * 从亿级数据中找出重复的数字
     */
    public static void test3() {
        // 定义一个byte数组缓存所有的数据 这里的29是因为用bit是32位，换算成byte要减少3位（1byte=8bit，刚好是2^3）
        byte[] dataBytes = new byte[1 << 29];
        byte[] bytes = null;
        //数据量10亿
        final int CAPACITY  = 1000000000;
        Random random = new Random();
        for (int i = 0; i < CAPACITY; i++) {
            int num = random.nextInt();
        }
    }


    public static void main(String[] args) {
//        test2();
        System.out.println(~((1 | 1 << 1) << (2 * (17 % 16))));
        System.out.println(Integer.toBinaryString(~12));
        System.out.println(17>>>4);

    }
}