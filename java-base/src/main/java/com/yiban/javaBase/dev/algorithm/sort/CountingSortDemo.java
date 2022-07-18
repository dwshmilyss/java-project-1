package com.yiban.javaBase.dev.algorithm.sort;

import org.apache.commons.lang3.time.StopWatch;

import java.util.concurrent.TimeUnit;

/**
 * 计数排序
 * 排序耗时
 * cost time = 21ms
 * cost time = 0s
 */
public class CountingSortDemo {
    public static void main(String[] args) {
//        int[] array = new int[]{9, 6, 8, 7, 0, 1, 10, 4, 2};
//        System.out.println(Arrays.toString(array));
//        System.out.println("------------------------");
//        countingSort(array, array.length);
//        System.out.println(Arrays.toString(array));

        //测试效率
        int[] array = SortUtils.generateRandomArray(1000000, 10000);
        System.out.println(array[0] + "->" + array[array.length - 1]);
        System.out.println("------------------------");
        StopWatch stopWatch = StopWatch.createStarted();
        countingSort(array, array.length);
        stopWatch.stop();
        System.out.println(array[0] + "->" + array[array.length - 1]);
        System.out.println("cost time = " + stopWatch.getTime() + "ms");
        System.out.println("cost time = " + stopWatch.getTime(TimeUnit.SECONDS) + "s");

    }

    /**
     * 假设数组中的数据都是非负整数
     *
     * @param a
     * @param n
     */
    public static void countingSort(int[] a, int n) {
        if (n <= 1) {
            return;
        }

        // 查找数组中数据的最大值
        int max = a[0];
        for (int i = 1; i < n; i++) {
            if (a[i] > max) {
                max = a[i];
            }
        }

        // 创建数组c，下标大小为[0, max]
        int[] c = new int[max + 1];

        // 计算每个元素的个数，放入c中
        for (int i = 0; i < n; i++) {
            c[a[i]]++;
        }

        //依次累加
        for (int i = 1; i < c.length; i++) {
            c[i] += c[i - 1];
        }

        // 新建数组r, 存储排序之后的结果
        int[] r = new int[n];

        // 计算排序,倒序遍历
        for (int i = n - 1; i >= 0; i--) {
            int index = c[a[i]] - 1;
            r[index] = a[i];
            c[a[i]]--;
        }


        // 将结果拷贝到a数组
        for (int i = 0; i < n; i++) {
            a[i] = r[i];
        }
    }

    /**
     * 计数排序，个人实现。
     *
     * @param a
     * @param n
     */
    public static void countingSort1(int[] a, int n) {
        if (n <= 1) {
            return;
        }

        // 查找数组中数据的最大值
        int max = a[0];
        for (int i = 1; i < n; i++) {
            if (a[i] > max) {
                max = a[i];
            }
        }

        // 创建数组c，下标大小为[0, max]
        int[] c = new int[max + 1];

        // 计算每个元素的个数，放入c中
        for (int i = 0; i < n; i++) {
            c[a[i]]++;
        }

        //依次累加
        for (int i = 1; i < c.length; i++) {
            c[i] += c[i - 1];
        }

        // 新建数组r, 存储排序之后的结果
        int[] r = new int[n];

        // 计算排序,倒序遍历
        // 个人实现
        for (int i = c.length - 1; i >= 0; i--) {
            if (i > 0) {
                while (c[i] > c[i - 1]) {
                    int index = c[i] - 1;
                    r[index] = i;
                    c[i]--;
                }
            } else {
                while (c[i] > 0) {
                    int index = c[i] - 1;
                    r[index] = i;
                    c[i]--;
                }
            }
        }

        // 将结果拷贝到a数组
        for (int i = 0; i < n; i++) {
            a[i] = r[i];
        }
    }
}
