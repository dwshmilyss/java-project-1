package com.yiban.javaBase.dev.algorithm.bloom_filter;


import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

/**
 * 布隆过滤器demo
 *
 * @auther WEI.DUAN
 * @date 2017/5/8
 * @website http://blog.csdn.net/dwshmilyss
 */
public class BloomFilterDemo {
    //30位，表示2^2^30种字符
    static int DEFAULT_LEN = 1 << 30;
    //要用质数
    static int[] seeds = {3, 5, 7, 11, 17, 31};
    static BitSet bitset = new BitSet(DEFAULT_LEN);
    static MyHash[] myHash = new MyHash[seeds.length];


    public static void main(String[] args) {
//        String str = "791909235@qq.com";
//
//        //生成一次就够了
//        for (int i = 0; i < seeds.length; i++) {
//            myHash[i] = new MyHash(DEFAULT_LEN, seeds[i]);
//        }
//        bitset.clear();
//        for (int i = 0; i < myHash.length; i++) {
//            bitset.set(myHash[i].myHash(str), true);
//        }
//        boolean flag = containsStr(str);
//        //System.out.println("========================");
//        System.out.println(flag);

        //
        Test1.test();
        System.out.println("====================");
        new Test3().test();
    }

    private static boolean containsStr(String str) {
        // TODO Auto-generated method stub
        if (null == str)
            return false;
        for (int i = 0; i < seeds.length; i++) {
            if (bitset.get(myHash[i].myHash(str)) == false)
                return false;
        }
        return true;
    }


    static class MyHash {
        int len;
        int seed;

        public MyHash(int len, int seed) {
            super();
            this.len = len;
            this.seed = seed;
        }

        public int myHash(String str) {
            int len = str.length();
            int result = 0;
            //这的len就是str的len，不是成员变量的len
            for (int i = 0; i < len; i++) {
                //System.out.println(seed+"oooooooooooo");
                result = result * seed + str.charAt(i);
                //System.out.println(result);
                //长度就是1<<24，如果大于这个数 感觉结果不准确
                //<0就是大于了0x7ffffff
                if (result > (1 << 30) || result < 0) {
                    //System.out.println("-----"+(1<<30));
                    System.out.println(result + "myHash数据越界！！！");
                    break;
                }
            }
            return (len - 1) & result;
        }
    }


    /**
     * 使用guava生成布隆过滤器
     */
    static class Test1{
        private static int size = 1000000;
        private static BloomFilter<Integer> bloomFilter = BloomFilter.create(Funnels.integerFunnel(),size,0.01D);

        public static void test(){
            for (int i = 0; i < size; i++) {
                bloomFilter.put(i);
            }

            long startTime = System.nanoTime(); // 获取开始时间
            //判断这一百万个数中是否包含29999这个数
            if (bloomFilter.mightContain(29999)) {
                System.out.println("命中了");
            }
            long endTime = System.nanoTime();   // 获取结束时间
            System.out.println("程序运行时间： " + (endTime - startTime) + "纳秒");
        }
    }


    /**
     * 使用guava生成布隆过滤器
     */
    static class Test2{
        private static int size = 1000000;
        //定义错误率0.01
        private static BloomFilter<Integer> bloomFilter = BloomFilter.create(Funnels.integerFunnel(),size,0.01);

        public static void test(){
            for (int i = 0; i < size; i++) {
                bloomFilter.put(i);
            }
            List<Integer> list = new ArrayList<Integer>(1000);
            // 故意取10000个不在过滤器里的值，看看有多少个会被认为在过滤器里
            for (int i = size + 10000; i < size + 20000; i++) {
                if (bloomFilter.mightContain(i)) {
                    list.add(i);
                }
            }
            System.out.println("误判的数量：" + list.size());
        }
    }

    static class Test3{
        /**
         * 要插入的数据量
         */
        public static final int size = 1000000;
        public static final double fpp = 0.01D;

        /**
         * bit数组长度
         */
        private long numBits = optimalNumOfBits(size, fpp);
        /**
         * hash函数数量
         * hash函数越多 检索越慢  但误判率越低 ，反之 检索越快  但误判率越高
         *
         */
        private int numHashFunctions = optimalNumOfHashFunctions(size, numBits);

        /**
         * 计算hash函数个数 方法来自guava
         * @param size 数据个数
         * @param numBits 位数组长度
         * @return
         */
        private int optimalNumOfHashFunctions(long size, long numBits) {
            return Math.max(1, (int) Math.round((double) numBits / size * Math.log(2)));
        }

        /**
         * 计算bit数组长度 方法来自guava
         * @param size 数据个数
         * @param fpp 误判率
         * @return
         */
        private long optimalNumOfBits(long size, double fpp) {
            if (fpp == 0) {
                fpp = Double.MIN_VALUE;
            }
            return (long) (-size * Math.log(fpp) / (Math.log(2) * Math.log(2)));
        }

        private BloomFilter<Integer> bloomFilter = BloomFilter.create(Funnels.integerFunnel(),size,fpp);
        public void test(){
            for (int i = 0; i < size; i++) {
                bloomFilter.put(i);
            }

            long startTime = System.nanoTime(); // 获取开始时间
            //判断这一百万个数中是否包含29999这个数
            if (bloomFilter.mightContain(29999)) {
                System.out.println("命中了");
            }
            long endTime = System.nanoTime();   // 获取结束时间
            System.out.println("程序运行时间： " + (endTime - startTime) + "纳秒");
        }
    }
}
