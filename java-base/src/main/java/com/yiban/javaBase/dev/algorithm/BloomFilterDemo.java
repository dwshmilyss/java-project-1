package com.yiban.javaBase.dev.algorithm;

import java.util.BitSet;

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
        String str = "791909235@qq.com";

        //生成一次就够了
        for (int i = 0; i < seeds.length; i++) {
            myHash[i] = new MyHash(DEFAULT_LEN, seeds[i]);
        }
        bitset.clear();
        for (int i = 0; i < myHash.length; i++) {
            bitset.set(myHash[i].myHash(str), true);
        }
        boolean flag = containsStr(str);
        //System.out.println("========================");
        System.out.println(flag);

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
}
