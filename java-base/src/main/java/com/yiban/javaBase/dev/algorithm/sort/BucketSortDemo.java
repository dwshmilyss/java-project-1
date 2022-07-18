package com.yiban.javaBase.dev.algorithm.sort;

import org.apache.commons.lang3.time.StopWatch;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 桶排序
 * 排序耗时：
 * cost time = 347ms
 * cost time = 0s
 */
public class BucketSortDemo {
    public static void main(String[] args) {
//        int[] array = new int[]{9, 6, 8, 7, 0, 1, 10, 4, 2};
//        System.out.println(Arrays.toString(array));
//        System.out.println("------------------------");
//        bucketSort(array);
//        System.out.println(Arrays.toString(array));

        //测试效率
        int[] array = SortUtils.generateRandomArray(1000000, 10000);
        System.out.println(array[0] + "->" + array[array.length - 1]);
        System.out.println("------------------------");
        StopWatch stopWatch = StopWatch.createStarted();
        bucketSort(array);
        stopWatch.stop();
        System.out.println(array[0] + "->" + array[array.length - 1]);
        System.out.println("cost time = " + stopWatch.getTime() + "ms");
        System.out.println("cost time = " + stopWatch.getTime(TimeUnit.SECONDS) + "s");

    }


    public static void bucketSort(int[] arr) {
        int max = Integer.MIN_VALUE;
        int min = Integer.MAX_VALUE;
        //找到待排序数组的最大值和最小值
        for (int i = 0; i < arr.length; i++) {
            max = Math.max(max, arr[i]);
            min = Math.min(min, arr[i]);
        }

        //确定桶的个数
        int bucketNum = (max - min) / arr.length + 1;
        ArrayList<ArrayList<Integer>> bucketArr = new ArrayList<>(bucketNum);
        //初始化每个桶(其实这里的桶就是ArrayList<Integer>)
        for (int i = 0; i < bucketNum; i++) {
            bucketArr.add(new ArrayList<Integer>());
        }

        //将每个元素放入桶(这个入桶的算法很重要)
        // 实际开发中需要根据场景具体设计
        for (int i = 0; i < arr.length; i++) {
            int num = (arr[i] - min) / (arr.length);
            bucketArr.get(num).add(arr[i]);
        }

        //对每个桶进行排序
        int index = 0;
        for (int i = 0; i < bucketArr.size(); i++) {
            Collections.sort(bucketArr.get(i));
            for (int data :
                    bucketArr.get(i)) {
                arr[index++] = data;
            }
        }
    }


    public static void bucketSort(float[] arr) {
        // 新建一个桶的集合
        ArrayList<LinkedList<Float>> buckets = new ArrayList<LinkedList<Float>>();
        for (int i = 0; i < 10; i++) {
            // 新建一个桶，并将其添加到桶的集合中去。
            // 由于桶内元素会频繁的插入，所以选择 LinkedList 作为桶的数据结构
            buckets.add(new LinkedList<Float>());
        }
        // 将输入数据全部放入桶中并完成排序
        for (float data : arr) {
            int index = getBucketIndex(data);
            insertSort(buckets.get(index), data);
        }
        // 将桶中元素全部取出来并放入 arr 中输出
        int index = 0;
        for (LinkedList<Float> bucket : buckets) {
            for (Float data : bucket) {
                arr[index++] = data;
            }
        }
    }

    /**
     * 计算得到输入元素应该放到哪个桶内
     */
    public static int getBucketIndex(float data) {
        // float[] arr = new float[] { 0.12f, 2.2f, 8.8f, 7.6f, 7.2f, 6.3f, 9.0f, 1.6f, 5.6f, 2.4f };
        // 这里例子写的比较简单，仅使用浮点数的整数部分作为其桶的索引值
        // 实际开发中需要根据场景具体设计
        return (int) data;
    }

    /**
     * 我们选择插入排序作为桶内元素排序的方法 每当有一个新元素到来时，我们都调用该方法将其插入到恰当的位置
     */
    public static void insertSort(List<Float> bucket, float data) {
        ListIterator<Float> it = bucket.listIterator();
        boolean insertFlag = true;
        while (it.hasNext()) {
            if (data <= it.next()) {
                it.previous(); // 把迭代器的位置偏移回上一个位置
                it.add(data); // 把数据插入到迭代器的当前位置
                insertFlag = false;
                break;
            }
        }
        if (insertFlag) {
            bucket.add(data); // 否则把数据插入到链表末端
        }
    }
}
