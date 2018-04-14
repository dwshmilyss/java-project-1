package com.yiban.javaBase.dev.algorithm;

import java.util.Scanner;

/**
 * 贪心算法
 *
 * @auther WEI.DUAN
 * @date 2017/11/27
 * @website http://blog.csdn.net/dwshmilyss
 */
public class GreedyDemo {

}

/**
 * 模拟找零钱的方案
 * 输入：需要找零的金额
 * 输出：人民币组成方案（人民币分为：100 50 20 10 5 2 1 0.5 0.2 0.1）
 */
class demo1 {
    public static final int MAX = 10;
    //人民币面值 这里扩大的100倍  如果用原来的值结果会报错
    public static final double VALUES[] = {10000, 5000, 2000, 1000, 500, 200, 100, 50, 20, 10};
    public static int NUM[] = new int[MAX];

    public static void main(String[] args) {
        System.out.println("请输入要换的数值");
        Scanner scanner = new Scanner(System.in);
        double a = scanner.nextDouble();
        conver(a * 100);
        System.out.println("找零");
        for (int i = 0; i < MAX; i++) {
            if (NUM[i] > 0) {
                System.out.println("面值" + VALUES[i] / 100 + "一共需要 " + NUM[i] + "张");
            }

        }
    }

    private static void conver(double a) {
        int i, j;
        for (i = 0; i < MAX; i++)
            if (a > VALUES[i])
                break;

        while (a > 0 && i < MAX) {
            if (a >= VALUES[i]) {
                a -= VALUES[i];
                NUM[i]++;
            } else if (a < 10 && a >= 5) {
                NUM[MAX - 1]++;
                break;
            } else
                i++;
        }
    }

}
