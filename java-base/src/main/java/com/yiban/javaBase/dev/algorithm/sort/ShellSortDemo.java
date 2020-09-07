package com.yiban.javaBase.dev.algorithm.sort;

import org.apache.commons.lang3.time.StopWatch;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

/**
 * shell排序
 * shell排序是插入排序的一种
 * 排序耗时：
 * cost time = 218ms
 * cost time = 0s
 */
public class ShellSortDemo {
    public static void main(String[] args) {
//        int[] array = new int[]{9, 6, 8, 7, 0, 1, 10, 4, 2};
//        System.out.println(Arrays.toString(array));
//        System.out.println("------------------------");
////        shellSort(array);
//        shellSort(array,true);
//        System.out.println(Arrays.toString(array));

        //测试效率
        int[] array = SortUtils.generateRandomArray(1000000, 10000);
        System.out.println(array[0] + "->" + array[array.length - 1]);
        System.out.println("------------------------");
        StopWatch stopWatch = StopWatch.createStarted();
//        shellSort(array);
        shellSort(array,true);
        stopWatch.stop();
        System.out.println(array[0] + "->" + array[array.length - 1]);
        System.out.println("cost time = " + stopWatch.getTime() + "ms");
        System.out.println("cost time = " + stopWatch.getTime(TimeUnit.SECONDS) + "s");

    }

    public static void shellSort(int[] data) {
        int j = 0;
        int max = 0;
        //第一层循环  计算需要 排序的小数组
        for (int step = data.length / 2; step > 0; step /= 2) {
            //第二层循环 比较  当前小组累加间隔
            for (int i = step; i < data.length; i++) {
                max = data[i];
                //第三层循环，进行插入操作
                for (j = i; j >= step; j -= step) {
                    //逆序只需改变这里的比较运算符
                    if (max < data[j - step]) {
                        data[j] = data[j - step];
                    } else {
                        break;
                    }
                }
                data[j] = max;
            }
        }
    }


    /**
     * shell排序算法
     * 增量h=(h*3)+1; 这个增量公式是由Knuth给出的
     * 如果不是很了解的话请百度一下吧
     *
     * @param array
     */
    private static void shellSort(int[] array,boolean flag) {
        //首先根据数组的长度确定增量的最大值
        int h = 1;
        // 按h * 3 + 1得到增量序列的最大值
        while (h <= array.length / 3) {
            h = h * 3 + 1;
        }
        //进行增量查找和排序
        while (h > 0) {
            for (int i = h; i < array.length; i++) {
                for (int k = i; k < array.length; k += h) {
                    //判断是否需要重新排序,如果小于k-h处的值，需要重新排序
                    if (array[k] < array[k - h]) {
                        int tempValue = array[k];
                        int j = k;
                        for (; j >= i && tempValue < array[j - h]; j -= h) {
                            array[j] = array[j - h];
                        }
                        array[j] = tempValue;
                    }
                }
                //查看每次插入后的中间状态
//                System.out.println("array = " + SortUtils.getArrayToString(array));
            }
            h = (h - 1) / 3;
        }
    }
}
