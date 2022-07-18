package com.yiban.javaBase.dev.algorithm;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @auther WEI.DUAN
 * @date 2020/6/8
 * @website http://blog.csdn.net/dwshmilyss
 */
public class NumberDemo {
    public static void main(String[] args) {
        System.out.println(Math.pow(2,0));
        test1(10);
    }



    // 实现数组元素的翻转
    public static void reverse(char[] arr) {
        // 遍历数组
        for (int i = 0; i < arr.length / 2; i++) {
            // 交换元素
            char temp = arr[arr.length - i - 1];
            arr[arr.length - i - 1] = arr[i];
            arr[i] = temp;
        }
    }

    /**
     * 把任意一个正整数分解成多个2的次方相加
     * @param num
     */
    public static void test1(int num) {
        String str = Integer.toBinaryString(num);
        char[] chars = str.toCharArray();
        reverse(chars);
        for (char ele : chars) {
            System.out.println(ele);
        }
        int res = 0;
        int chars_0 = Integer.parseInt(String.valueOf(chars[0]));
        if (chars_0 == 1){
            res += Math.pow(2, 0);
        }
        for (int i = chars.length - 1; i >= 1 ; i--) {
            int temp = Integer.parseInt(String.valueOf(chars[i]));
            if (temp == 1){
                res += Math.pow(2, i);
            }
        }
        System.out.println(res);
    }
}