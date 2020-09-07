package com.yiban.javaBase.dev.algorithm.sort;

import org.apache.commons.lang3.time.StopWatch;

import java.util.concurrent.TimeUnit;

/**
 * 归并排序
 * 如果要排序一个数组，我们先把数组从中间分成前后两部分，然后对前后两部分分别排序，再将排好序的两部分合并在一起，这样整个数组就都有序了。
 * 所以说归并排序的核心思想是排序和合并两个有序数组，这个过程需要用递归来实现。
 * 核心思想：
 * 1、将数组每次从中间拆分为两个数组（该操作和二分查找类似），然后对两个数组各自进行排序。
 * 2、合并两个排好序的数组。
 * 需要注意的是，对子数组进行排序依然是使用归并排序，所以这就涉及到了递归。
 *
 * 排序耗时：
 * cost time = 154ms
 * cost time = 0s
 */
public class MergeSortDemo {
    public static void main(String[] args) {
//        int[] array = {4, 6, 8, 5, 9};
//        System.out.println("归并排序：");
//        System.out.println(SortUtils.getArrayToString(array));
//        MergeSortDemo.mergeSort(array, array.length);
//        System.out.println(SortUtils.getArrayToString(array));

        //测试效率
        int[] array = SortUtils.generateRandomArray(1000000, 10000);
        System.out.println(array[0] + "->" + array[array.length-1]);
        System.out.println("------------------------");
        StopWatch stopWatch = StopWatch.createStarted();
        mergeSort(array,array.length);
        stopWatch.stop();
        System.out.println(array[0] + "->" + array[array.length-1]);
        System.out.println("cost time = " + stopWatch.getTime() + "ms");
        System.out.println("cost time = " + stopWatch.getTime(TimeUnit.SECONDS) + "s");

    }


    /**
     * 归并排序
     * @param array 待排序的数组
     * @param length 数组大小
     */
    public static void mergeSort(int[] array, int length) {
        //从0开始，到array.size-1
        mergeSortRecursion(array, 0, length - 1);
    }

    /**
     * 递归调用的函数
     * @param array 待排序数组
     * @param start 开始位置
     * @param end 结束位置
     */
    public static void mergeSortRecursion(int[] array, int start, int end) {
        // 递归终止条件
        if (start >= end) {
            return;
        }

        // 每次取start到end的中间位置middle
        int middle = start + (end  - start) / 2;
        // 分治递归
        mergeSortRecursion(array, start, middle);
        mergeSortRecursion(array, middle + 1, end);

        // 将array[start...middle]和array[middle+1...end]合并为array[start...end]
        merge(array, start, middle, end);
    }

    /**
     * 合并两个有序数组
     * @param array 合并好的有序数组，需要放到这个位置上。
     * @param p 开始位置
     * @param q 中间位置（分割位置）
     * @param r 结束位置
     */
    public static void merge(int[] array, int p, int q, int r) {
        int i = p;
        int j = q + 1;
        int k = 0;
        int[] tmp = new int[r - p + 1];

        // 最少把一个数组中的数据取完。
        while (i <= q && j <= r) {
            //TODO 改变这里的比较运算符就可以实现逆序
            if (array[i] <= array[j]) {
                tmp[k++] = array[i++];
            } else {
                tmp[k++] = array[j++];
            }
        }

        // 判断哪个子数组中有剩余的数据。
        int start = i;
        int end = q;
        if (j <= r) {
            start = j;
            end = r;
        }
        // 将剩余的数据copy到临时数组 tmp。
        while (start <= end) {
            tmp[k++] = array[start++];
        }

        //将 tmp 中的数据拷贝回 a 中
        System.arraycopy(tmp, 0, array, p, r - p + 1);
    }
}
