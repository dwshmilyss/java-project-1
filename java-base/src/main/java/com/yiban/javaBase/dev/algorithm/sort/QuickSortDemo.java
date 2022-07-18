package com.yiban.javaBase.dev.algorithm.sort;

import org.apache.commons.lang3.time.StopWatch;

import java.util.concurrent.TimeUnit;

/**
 * 快速排序
 * 快速排序之所比较快，因为相比冒泡排序，每次交换是跳跃式的。
 * 每次排序的时候设置一个基准点，将小于等于基准点的数全部放到基准点的左边，
 * 将大于等于基准点的数全部放到基准点的右边。这样在每次交换的时候就不会像冒泡排序一样每次只能在相邻的数之间进行交换，
 * 交换的距离就大的多了。因此总的比较和交换次数就少了，速度自然就提高了。
 * 当然在最坏的情况下，仍可能是相邻的两个数进行了交换。因此快速排序的最差时间复杂度和冒泡排序是一样的都是O(n^2)，
 * 它的平均时间复杂度为O(NlogN)。其实快速排序是基于一种叫做“二分”的思想。
 *
 * 排序耗时：
 * cost time = 158ms
 * cost time = 0s
 */
public class QuickSortDemo {

    public static void main(String[] args) {
//        int[] array = new int[]{9, 6, 8, 7, 0, 1, 10, 4, 2};
//        System.out.println(Arrays.toString(array));
//        System.out.println("------------------------");
////        quickSort(array, 0, array.length - 1);
//        quickSort(array, array.length);
//        System.out.println(Arrays.toString(array));

        //测试效率
        int[] array = SortUtils.generateRandomArray(1000000, 10000);
        System.out.println(array[0] + "->" + array[array.length - 1]);
        System.out.println("------------------------");
        StopWatch stopWatch = StopWatch.createStarted();
//        quickSort(array, 0, array.length - 1);
        quickSort(array, array.length);
        stopWatch.stop();
        System.out.println(array[0] + "->" + array[array.length - 1]);
        System.out.println("cost time = " + stopWatch.getTime() + "ms");
        System.out.println("cost time = " + stopWatch.getTime(TimeUnit.SECONDS) + "s");

    }

    //arr 需要排序的数组
    //low 开始时最左边的索引=0
    //high 开始时最右边的索引=arr.length-1
    public static void quickSort(int[] arr, int low, int high) {
        int i, j, temp, t;
        if (low > high) {
            return;
        }
        i = low;//左边哨兵的索引
        j = high;//右边哨兵的索引
        //temp就是基准位
        temp = arr[low];//以最左边为  基准位

        while (i < j) {
            //TODO 先看右边，依次往左递减 这一点非常重要（即基准值设置在一侧，那么先手开始就要从另外一侧）
            //TODO 如果不这样做就不能保证一侧的数比基准值小，另一侧都比基准值大
            //移动下标，先从右往左找第一个小于基准位的数，
            //直到找到第一个后跳出while循环
            //这里如果要逆序的话，需要改变下面两个while循环的比较运算符
            while (temp <= arr[j] && i < j) {
                j--;
            }
            //再看左边，依次往右递增。步骤和上面类似
            while (temp >= arr[i] && i < j) {
                i++;
            }
            int z, y;
            //如果满足条件则交换
            if (i < j) {
                //z、y 都是临时参数，用于存放 左右哨兵 所在位置的数据
                z = arr[i];
                y = arr[j];

                // 左右哨兵 交换数据（互相持有对方的数据）
                arr[i] = y;
                arr[j] = z;
            }
        }
        //这时跳出了"while(i<j)"循环，说明 i=j 左右在同一位置
        //最后将基准为与i和j相等位置的数字交换
        arr[low] = arr[i];//或 arr[low] = arr[j];
        arr[i] = temp;//或 arr[j] = temp;


        //i=j
        //这时 左半数组<(i或j所在索引的数)<右半数组
        //也就是说(i或j所在索引的数)已经确定排序位置， 所以就不用再排序了，
        // 只要用相同的方法 分别处理  左右数组就可以了
        //递归调用左半数组
        quickSort(arr, low, j - 1);
        //递归调用右半数组
        quickSort(arr, j + 1, high);
    }


    /**
     * 快速排序
     * 排序耗时：
     * cost time = 97ms
     * cost time = 0s
     * @param a
     * @param n
     */
    public static void quickSort(int[] a, int n) {
        quickSortRecursive(a, 0, n - 1);
    }

    /**
     * 快速排序递归函数，p,r为下标。
     *
     * @param a
     * @param p
     * @param r
     */
    public static void quickSortRecursive(int[] a, int p, int r) {
        if (p >= r) {
            return;
        }
        // 获取分区点
        int q = partition(a, p, r);
        quickSortRecursive(a, p, q - 1);
        quickSortRecursive(a, q + 1, r);
    }

    public static int partition(int[] a, int p, int r) {
        int pivot = a[r];
        int i = p;
        for (int j = p; j < r; j++) {
            if (a[j] < pivot) {
                int t = a[i];
                a[i] = a[j];
                a[j] = t;
                i++;
            }
        }
        int t = a[i];
        a[i] = a[r];
        a[r] = t;
        return i;
    }

}
