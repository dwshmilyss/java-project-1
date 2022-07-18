package com.yiban.javaBase.dev.algorithm.sort;

import org.apache.commons.lang3.time.StopWatch;

import java.util.concurrent.TimeUnit;

/**
 * 堆排序
 * 堆其实就是一棵完全二叉树
 * 大顶堆（升序）即每次调整堆都会将最大值放到根节点，即{4,5,6,8,9}
 * 小顶堆（降序）即每次调整堆都会将最小值放到根节点，即{9,8,6,5,4}
 * cost time = 211ms
 * cost time = 0s
 */
public class HeapSortDemo {

    public static void main(String[] args) {
//        int[] array = {4, 6, 8, 5, 9};
//        System.out.println(Arrays.toString(array));
//        System.out.println("------------------------");
//        heap(array);
//        System.out.println(Arrays.toString(array));

        //用一百万个数据测试排序效率
        int[] array = SortUtils.generateRandomArray(1000000, 10000);
        System.out.println(array[0] + "->" + array[array.length-1]);
        System.out.println("------------------------");
        StopWatch stopWatch = StopWatch.createStarted();
        heap(array);
        stopWatch.stop();
        System.out.println(array[0] + "->" + array[array.length-1]);
        System.out.println("cost time = " + stopWatch.getTime() + "ms");
        System.out.println("cost time = " + stopWatch.getTime(TimeUnit.SECONDS) + "s");
    }

    public static void heap(int[] array) {
        //开始位置 其实这两种开始位置对于结果来说没有区别
        //但是(array.length / 2 - 1)作为开始位置比较好理解，这里元素的索引是从0开始的,所以最后一个非叶子结点的索引就是array.length/2 - 1
        // 因为只有有子节点的节点才需要调整，那么就从最后一个非叶子节点开始调整吧
        //这个循环走完之后，最大值会被调整到数组的第一位，即堆(二叉树)的根节点
        int start = (array.length-1)/2;
//        int start = array.length / 2 - 1;
        for (int i = start; i >= 0; i--) {
            max_heap(array, array.length, i);
        }
        //大顶堆因为是升序，所以需要交换位置，把堆顶元素(即最大的那个，也就是数组里的第一个)放到数组的尾部
        for (int i = array.length - 1; i > 0; i--) {
            int temp = array[0];
            array[0] = array[i];
            array[i] = temp;
            max_heap(array, i, 0);
        }
    }


    /**
     * 堆排序（大顶堆） 调用完该方法后，完全二叉树的root节点就是最大值，也就是数组的第一个元素
     * @param arr
     * @param size
     * @param index
     */
    public static void max_heap(int[] arr, int size, int index) {
        //左子节点
        int leftNode = 2 * index + 1;
        //右子节点
        int rightNode = 2 * index + 2;

        //设置最大值
        int max = index;

        //进行和左子节点比较
        if (leftNode < size && arr[leftNode] > arr[max]) {
            max = leftNode;
        }
        //和右子节点比较
        if (rightNode < size && arr[rightNode] > arr[max]) {
            max = rightNode;
        }

        //找到最大的节点之后就替换
        if (max != index) {
            int temp = arr[index];
            arr[index] = arr[max];
            arr[max] = temp;
            //然后如果还有的话就继续替换
            max_heap(arr, size, max);
        }
    }


    /**
     * 堆排序（小顶堆） 调用完该方法后，完全二叉树的root节点就是最大值，也就是数组的第一个元素
     * @param arr
     * @param size
     * @param index
     */
    public static void min_heap(int[] arr, int size, int index) {
        //左子节点
        int leftNode = 2 * index + 1;
        //右子节点
        int rightNode = 2 * index + 2;

        //设置最小值
        int min = index;

        //进行和左子节点比较
        if (leftNode < size && arr[leftNode] < arr[min]) {
            min = leftNode;
        }
        //和右子节点比较
        if (rightNode < size && arr[rightNode] < arr[min]) {
            min = rightNode;
        }

        //找到最大的节点之后就替换
        if (min != index) {
            int temp = arr[index];
            arr[index] = arr[min];
            arr[min] = temp;
            //然后如果还有的话就继续替换
            min_heap(arr, size, min);
        }
    }

    //下面这个算法有问题
//    /**
//     * 选择排序-堆排序
//     * @param array 待排序数组
//     * @return 已排序数组
//     */
//    public static int[] heapSort(int[] array) {
//        //这里元素的索引是从0开始的,所以最后一个非叶子结点array.length/2 - 1
//        //构建堆
//        //其实第一次构建堆也即是第一次调整堆，这个for循环结束后最大值将会调整到数组的第一位
//        for (int i = (array.length - 1) / 2; i >= 0; i--) {
//            adjustHeap(array, i, array.length);
//        }
//        // 上述逻辑，建堆结束，因为建堆结束后已经进行了一次调整，这时候最大值已经跑到数组的最后一个，
//        // 所以循环从array.length-1开始
//        // 下面，开始排序逻辑
//        for (int j = array.length - 1; j > 0; j--) {
//            // 元素交换,作用是去掉大顶堆
//            // 把大顶堆的根元素，放到数组的最后；换句话说，就是每一次的堆调整之后，都会有一个元素到达自己的最终位置
//            swap(array, 0, j);
//            // 元素交换之后，毫无疑问，最后一个元素无需再考虑排序问题了。
//            // 接下来我们需要排序的，就是已经去掉了部分元素的堆了，这也是为什么此方法放在循环里的原因
//            // 而这里，实质上是自上而下，自左向右进行调整的
//            adjustHeap(array, 0, j);
//        }
//        return array;
//    }
//
//    public static void adjustHeap(int[] array, int index, int length) {
//        //先把当前的元素取出来，因为当前元素可能要一直移动
//        int temp = array[index];
//        //2*index+1为左子节点index的左子节点(因为index是从0开始的，而数据结构又是一棵完全二叉树)
//        //k其实就是index的左子树,2*k+1为k的左子树
//        //先让k指向左子节点
//        //如果循环一次后，k还小于数组长度，那么下次k = 2 * k + 1，即再找k的左子节点
//        for (int k = 2 * index + 1; k < length; k = 2 * k + 1) {
//            //k始终不能超过数组长度，并且如果左子树小于右子树
//            if (k < length && array[k] < array[k + 1]) {
//                k++;
//            }
//            //如果发现子节点大于父节点，则两者进行交换
//            if (array[k] > temp) {
//                array[index] = array[k];
//                // 如果子节点更换了，那么，以子节点为根的子树会受到影响,所以，循环对子节点所在的树继续进行判断
//                index = k;
//            } else {//不用交换，直接终止循环
//                break;
//            }
//        }
//        array[index] = temp;
//    }
//
//    /**
//     * 交换元素
//     *
//     * @param arr
//     * @param a   元素的下标
//     * @param b   元素的下标
//     */
//    public static void swap(int[] arr, int a, int b) {
//        int temp = arr[a];
//        arr[a] = arr[b];
//        arr[b] = temp;
//    }
}
