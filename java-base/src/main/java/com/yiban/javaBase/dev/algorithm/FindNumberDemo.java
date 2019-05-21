package com.yiban.javaBase.dev.algorithm;

import java.util.BitSet;

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


    public static void main(String[] args) {
        test1();
    }
}