package com.yiban.javaBase.dev.algorithm;

/**
 * @auther WEI.DUAN
 * @date 2019/10/21
 * @website http://blog.csdn.net/dwshmilyss
 *
 * 给定一个非负整数数组，你最初位于数组的第一个位置。
 * 数组中的每个元素代表你在该位置可以跳跃的最大长度。
 * 你的目标是使用最少的跳跃次数到达数组的最后一个位置。
 *
 * 输入: [2,3,1,1,4]
 * 输出: 2
 * 解释: 跳到最后一个位置的最小跳跃数是 2。
 * 从下标为 0 跳到下标为 1 的位置，跳 1 步，然后跳 3 步到达数组的最后一个位置。
 *
 */
public class JumpGameDemo {


    public static void main(String[] args) {
        int[] nums = {2, 3, 1, 1, 4, 2, 1};
        System.out.println(jump(nums));
        System.out.println(jump1(nums));
    }

    /**
     * 遍历算法
     * @param nums
     * @return
     */
    public static int jump(int[] nums){
        int right = nums.length - 1;
        int sum = 0;
        while(right > 0){
            // 寻找离 last 最远，且能够到达 last 的点
            int cur = right - 1;
            for(int i = right - 2; i >= 0; i--){
                // 是否能够达到 last 这个点
                if( i + nums[i] >= right){
                    cur = i;
                }
            }
            right = cur;
            sum++;
        }
        return sum;
    }

    /**
     * 贪心算法
     * @param nums
     * @return
     */
    public static int jump1(int[] nums) {
        if(nums.length < 2)
            return 0;

        int sum = 0;
        int end = 0; // 能跳到的最远距离
        int max = 0; // 下一步可以跳到的最远距离
        for(int i = 0; i < nums.length - 1; i++){
            max = Math.max(max, i + nums[i]);
            // 更新当前点
            if(i == end){
                end = max;
                sum++;
            }
        }

        return sum;
    }
}