package com.yiban.javaBase.dev.algorithm;

import java.util.HashMap;
import java.util.Map;

/**
 * 动态规划demo
 * 以切钢条为例：
 * 给一段长度为n的钢条和一个价格表，找到一种切割方案，使得收益最大
 *
 * @auther WEI.DUAN
 * @date 2017/4/25
 * @website http://blog.csdn.net/dwshmilyss
 */
public class DynamicProgrammingDemo {
    public static Map<Integer, Integer> prices = new HashMap<Integer, Integer>();

    static {
        prices.put(1, 1);
        prices.put(2, 5);
        prices.put(3, 8);
        prices.put(4, 9);
        prices.put(5, 10);
        prices.put(6, 17);
        prices.put(7, 17);
        prices.put(8, 20);
        prices.put(9, 24);
        prices.put(10, 30);
    }

    public static void main(String[] args) {
//        System.out.println(cut_rod(prices, 4));
//        System.out.println(memoized_cut_rod(prices,4));
        System.out.println(bottom_up_cut_rod(prices, 4));
    }

    /**
     * 未优化的动态规划算法
     *
     * @param prices 价格对应表
     * @param n      长度n
     * @return 最优值
     */
    public static int cut_rod(Map<Integer, Integer> prices, int n) {
        if (n == 0) {
            return 0;
        }
        int q = Integer.MIN_VALUE;
        for (int i = 1; i <= n; i++) {
            int temp = cut_rod(prices, n - i);
            int val = prices.get(i);
            q = Math.max(q, val + temp);
        }
        return q;
    }


    public static int memoized_cut_rod(Map<Integer, Integer> prices, int n) {
        Map<Integer, Integer> memo = new HashMap<Integer, Integer>();
        for (int i = 0; i < n; i++) {
            memo.put(i, Integer.MIN_VALUE);
        }
        return memoized_cut_rod_aux(prices, n, memo);
    }

    /**
     * 动态规划算法1：添加备忘录(优化算法)
     * 先判断某一个子任务是否已经计算过，有则直接返回。这样可以大大减少重复任务的计算
     *
     * @param prices 价格对应表
     * @param n      长度
     * @param memo   备忘录
     * @return 最优值
     */
    private static int memoized_cut_rod_aux(Map<Integer, Integer> prices, int n, Map<Integer, Integer> memo) {
        if (memo.containsKey(n) && memo.get(n) > 0) {
            return memo.get(n);
        }
        int q = Integer.MIN_VALUE;
        if (n == 0) {
            return 0;
        } else {
            for (int i = 1; i <= n; i++) {
                int temp = memoized_cut_rod_aux(prices, n - i, memo);
                int val = prices.get(i);
                q = Math.max(q, val + temp);
            }
        }
        memo.put(n, q);
        return q;
    }

    /**
     * 自底向上的动态规划算法 即：计算每个子任务下的所有子子任务
     *
     * @param prices 价格表
     * @param n      长度
     * @return 最优解
     */
    public static int bottom_up_cut_rod(Map<Integer, Integer> prices, int n) {
        Map<Integer, Integer> memo = new HashMap();
        memo.put(0, 0);
        for (int j = 1; j <= n; j++) {
            int q = Integer.MIN_VALUE;
            for (int i = 1; i <= j; i++) {
                q = Math.max(q, prices.get(i) + memo.get(j - i));
            }
            memo.put(j, q);
        }
        return memo.get(n);
    }
}
