package com.yiban.javaBase.dev.algorithm.sort;

import java.util.Arrays;

/**
 * @Description: 插入排序
 * 排序耗时 ：
 * cost time = 104438ms
 * cost time = 104s
 */
public class InsertSortDemo {

    public static void main(String[] args) {
        int[] array = new int[]{9, 6, 8, 7, 0, 3, 5, 1, 10, 4, 2};
        System.out.println(Arrays.toString(array));
        System.out.println("------------------------");
        insertSort(array, array.length);
        System.out.println(Arrays.toString(array));

//        //测试效率
//        int[] array = SortUtils.generateRandomArray(1000000, 10000);
//        System.out.println(array[0] + "->" + array[array.length - 1]);
//        System.out.println("------------------------");
//        StopWatch stopWatch = StopWatch.createStarted();
//        insertSort(array,array.length);
//        stopWatch.stop();
//        System.out.println(array[0] + "->" + array[array.length - 1]);
//        System.out.println("cost time = " + stopWatch.getTime() + "ms");
//        System.out.println("cost time = " + stopWatch.getTime(TimeUnit.SECONDS) + "s");

    }

    /**
     * 插入排序
     * 将数据插入到有序数组中。
     *
     * @param a 待排序数组
     * @param n 长度
     */
    public static void insertSort(int[] a, int n) {
        if (n <= 1) return;
        //这里i从1开始是因为要用第二个元素和第一个元素比较，即每一次都是后面的一个和前面有序的数组中的每一个元素比较
        for (int i = 1; i < n; i++) {
            int temp = a[i];//用一个中间值保存要移动的值
            int j = i - 1;//第一次进来的时候这里是0，以后就刚好是有序数组中的最后一个元素
            // 查找插入的位置
            //这里的j--因为在循环体之后会执行，所以很巧妙，
            // 因为第一次进来j=0,j--之后就是-1，刚好对应后面的a[j+1]=temp，即a[0]=temp。
            for (; j >= 0; j--) {
                if (a[j] < temp) {
                    a[j + 1] = a[j];// 数据移动
                } else {
                    break;
                }
            }
            a[j + 1] = temp;// 插入数据
        }
    }
}
