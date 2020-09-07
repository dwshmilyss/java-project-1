package com.yiban.javaBase.dev.algorithm.sort;

import java.util.Arrays;

/**
 * 选择排序
 * 基于此思想的算法主要有简单选择排序、树型选择排序和堆排序
 *
 * 排序耗时：（100万）
 * cost time = 922109ms
 * cost time = 922s
 */
public class SelectSortDemo {
    public static void main(String[] args) {

        int[] array = new int[]{9, 6, 8, 7, 0, 1, 10, 4, 2};
        System.out.println(Arrays.toString(array));
        System.out.println("------------------------");
        selectSort(array, array.length);
        System.out.println(Arrays.toString(array));


//        int[] array = SortUtils.generateRandomArray(1000000, 10000);
//        System.out.println(array[0] + "->" + array[array.length-1]);
//        System.out.println("------------------------");
//        StopWatch stopWatch = StopWatch.createStarted();
//        selectSort(array,array.length);
//        stopWatch.stop();
//        System.out.println(array[0] + "->" + array[array.length-1]);
//        System.out.println("cost time = " + stopWatch.getTime() + "ms");
//        System.out.println("cost time = " + stopWatch.getTime(TimeUnit.SECONDS) + "s");
    }

    /**
     *  简单选择排序的基本思想：给定数组：int[] arr={里面n个数据}；
     *  第1趟排序，在待排序数据arr[1]~arr[n]中选出最小的数据，将它与arrr[1]交换；
     *  第2趟，在待排序数据arr[2]~arr[n]中选出最小的数据，将它与r[2]交换；以此类推，
     *  第i趟在待排序数据arr[i]~arr[n]中选出最小的数据，将它与r[i]交换，直到全部排序完成
     *
     * 从未排序区间中找到最小元素，将其放到已排序区间的头部（升序）。
     * 从未排序区间中找到最大元素，将其放到已排序区间的头部（升序）。
     * @param array 待排序数组
     * @param length 数组长度
     */
    public static void selectSort(int[] array, int length) {
        if (length <= 1) return;
        for (int i = 0; i < length - 1; i++) {
            // 查找最小值的下标
            int minIndex = i;
            for (int j = i + 1; j < length; j++) {
                //TODO 改变这里的比较运算符可以实现逆序，那么这里的minIndex就该叫maxIndex
                if (array[j] < array[minIndex]) {
                    minIndex = j;
                }
            }

            // 减少无用的交换。
            if (minIndex == i) {
                continue;
            }

            // 交换
            int tmp = array[i];
            array[i] = array[minIndex];
            array[minIndex] = tmp;
        }
    }
}
