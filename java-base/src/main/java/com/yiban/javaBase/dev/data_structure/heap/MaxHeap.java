package com.yiban.javaBase.dev.data_structure.heap;

/**
 * 大顶堆
 * 堆（或二叉堆），类似于完全二叉树，除叶子节点外，每个节点均拥有左子树和右子树，同时左子树和右子树也是堆。
 * 小顶堆：父节点的值 <= 左右孩子节点的值
 * 大顶堆：父节点的值 >= 左右孩子节点的值
 * <p>
 * 对于数组中的第 i 个节点（从0开始），有如下规律：
 * 1、如果父节点存在，则它的父节点是 (i - 1) / 2； 比如3的父亲是(3-1)/2=1
 * 2、如果左孩子存在，则它的左孩子是 2 * i + 1； 比如1的左孩子是2*1+1=3
 * 3、如果右孩子存在，则右孩子是 2 * i + 2；比如1的右孩子是2*1+2=4
 *
 * @auther WEI.DUAN
 * @date 2019/6/4
 * @website http://blog.csdn.net/dwshmilyss
 */
public class MaxHeap {
    public static void print(int[] arr) {
        for (int i = 0; i < arr.length; i++) {
            System.out.print(arr[i] + " ");
        }
        System.out.println();
    }

    public static void create_heap(int[] arr) {
        /**
         * 因为一开始如果把数组看成一棵二叉树的话，那么数组长度/2 之后的元素就是叶子节点，叶节点没有孩子，所以无需调整
         * 又因为下标从0开始，所以这里要从arr.length/2-1 开始计算
         * 比如原始数组为[19, 17, 20, 18, 16, 21]，长度为6，除以2再减1 刚好是20的下标
         */
        for (int i = arr.length / 2 - 1; i >= 0; i--) {
            fix_down(arr, i, arr.length - 1);
        }
    }

    /**
     * 不断调整堆元素 使其符合大顶堆的性
     *
     * @param arr
     * @param i
     * @param end
     */
    public static void fix_down(int[] arr, int i, int end) {
        // 当前节点的左孩子
        int child = (i << 1) + 1;
        //定义一个变量保存当前的值 用于交换
        int temp = arr[i];

        //全部比较完结束
        while (child <= end) {
            // 选出两个孩子较大的那个 arr[child]是左孩子 arr[child+1]是右孩子
            if (child < end && arr[child + 1] > arr[child]) {
                child++;
            }
            if (temp < arr[child]) {
                // 孩子节点与当前节点替换
                arr[i] = arr[child];
                i = child;
                child = (i << 1) + 1;
            } else {
                break;
            }
        }
        arr[i] = temp;
    }


    public static void head_sort(int[] arr) {
        // 取出堆顶元素，与最后一个元素交换，调整堆
        for (int i = arr.length - 1; i >= 0; i--) {
            // 最后一个元素
            int temp = arr[i];
            arr[i] = arr[0];
            arr[0] = temp;
            //注意这里是i-1 即刚刚交换过的最后一个元素不参与调整
            fix_down(arr, 0, i - 1);

        }
    }

    public static void main(String[] args) {
        // Test case 1
        int[] arr = {22, 21, 17, 20, 18, 16, 19};
        create_heap(arr); // 创建堆
        head_sort(arr);
        print(arr); // 16 17 18 19 20 21
    }
}