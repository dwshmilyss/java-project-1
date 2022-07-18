package com.yiban.javaBase.dev.algorithm.sort;

import org.apache.commons.lang3.time.StopWatch;

import java.util.concurrent.TimeUnit;

/**
 * 冒泡排序
 * 最好的时间复杂度为O(n)。
 * 最坏情况下时间复杂度为O(n^2)
 * 总的平均时间复杂度为：O(n^2)
 *
 * 排序耗时：
 * cost time = 2056378ms
 * cost time = 2056s
 */
public class BubbleSortDemo {
    public static void main(String[] args) {
//        int[] array = new int[]{9, 6, 8, 7, 0, 1, 10, 4, 2};
//        System.out.println(Arrays.toString(array));
//        System.out.println("------------------------");
//        bubbleSort(array,array.length);
//        System.out.println(Arrays.toString(array));

        //测试效率
        int[] array = SortUtils.generateRandomArray(1000000, 10000);
        System.out.println(array[0] + "->" + array[array.length-1]);
        System.out.println("------------------------");
        StopWatch stopWatch = StopWatch.createStarted();
        bubbleSort(array,array.length);
        stopWatch.stop();
        System.out.println(array[0] + "->" + array[array.length-1]);
        System.out.println("cost time = " + stopWatch.getTime() + "ms");
        System.out.println("cost time = " + stopWatch.getTime(TimeUnit.SECONDS) + "s");


    }

    /**
     * 冒泡排序 升序
     * @param arr
     * @param length
     */
    public static void bubbleSort(int[] arr, int length) {
        if (length <= 1) return;
        //外层循环为数组中所有的元素
        //因为每一个元素都要比较后移动位置，所以外层循环的条件为数组的长度
        for (int i = 0; i < length; i++) {
            //内层循环为数组的长度 - 已经冒过泡的元素个数
            //因为外层每循环一次后都会有一个元素到达指定位置
            //所以内层循环再执行的时候需要去除掉已经排序好的元素(减i)
            //至于减1则是因为arr[j]要和arr[j+1]比较，如果不减一，那么j+1到最后会数组越界
            for (int j = 0; j < length - 1 - i; j++) {
                //TODO 升序(如果要降序，只需改变比较运算符即可)
                if (arr[j] > arr[j + 1]) {
                    int temp = arr[j + 1];
                    arr[j + 1] = arr[j];
                    arr[j] = temp;
                }
            }
        }
    }

    /**
     * 优化后的冒泡排序（多了一个提前退出的标识位）
     * 因为未优化的冒泡排序外层循环为数组长度，但是有可能在外层循环进行几次后数组已经是有序的了，
     * 这时候并不需要把循环进行到底，所以给定一个提前退出的标识位
     * @param arr
     * @param length
     */
    public static void bubbleSortOptimize(int[] arr, int length) {
        if (length <= 1) return;
        for (int i = 0; i < length; i++) {
            // 提前退出冒泡循环的标志位
            boolean flag = false;
            for (int j = 0; j < length - i - 1; j++) {
                if (arr[j] > arr[j + 1]) {
                    int tmp = arr[j];
                    arr[j] = arr[j + 1];
                    arr[j + 1] = tmp;
                    flag = true;
                }
            }
            // 没有数据交换，提前退出
            // 如果数组刚好是有序的，那么这是最好的情况，时间复杂度为O(n)
            if (!flag) break;
        }
    }
}
