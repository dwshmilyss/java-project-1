package com.yiban.javaBase.test;

import com.alibaba.dubbo.common.io.Bytes;
import com.google.common.base.Strings;
import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * @auther WEI.DUAN
 * @date 2019/5/22
 * @website http://blog.csdn.net/dwshmilyss
 */
public class Test1 {
    @Test
    public void test() {
//        System.out.println(Integer.MAX_VALUE);
//        System.out.println(1l << 31);
//        System.out.println("--------------");
//        System.out.println(1 << 30);
//        System.out.println("--------------");
//        System.out.println(Integer.MIN_VALUE);
//        System.out.println(1L << 6);
//        System.out.println(63 >> 6);
//        System.out.println(1 | 1 << 1);
        System.out.println(Math.abs("test_8_3_g2".hashCode()) % 8);
    }

    @Test
    public void test1() {
        List<String> list = new ArrayList<>(1000000);
        long stratTime = System.nanoTime();
        int len = list.size();
        for (int i = 0; i < len; i++) {

        }
        //未优化list耗时：4811
        long endTime = System.nanoTime();
        System.out.println("优化list耗时：" + (endTime - stratTime));
    }

    @Test
    public void test2() {
        System.out.println(1 >> 2);
        int x = 2, y = 3;
        x = x ^ y;
        y = x ^ y;
        x = x ^ y;
        System.out.println("x = " + x + ",y=" + y);
    }

    @Test
    public void test3() {
        List<String> list = null;
        String[] list1 = {};
        for (String string : list1) {
            System.out.println(string);
        }
    }

    @Test
    public void test4() {
        System.out.println(fromHexString("7a726f77"));
    }


    /**
     * 16进制直接转换成为字符串
     * @param hexString 16进制字符串
     * @return String （字符集：UTF-8）
     * @explain
     */
    public String fromHexString(String hexString) {
        // 用于接收转换结果
        String result = "";
        // 转大写
        hexString = hexString.toUpperCase();
        // 16进制字符
        String hexDigital = "0123456789ABCDEF";
        // 将16进制字符串转换成char数组
        char[] hexs = hexString.toCharArray();
        // 能被16整除，肯定可以被2整除
        byte[] bytes = new byte[hexString.length() / 2];
        int n;

        for (int i = 0; i < bytes.length; i++) {
            n = hexDigital.indexOf(hexs[2 * i]) * 16 + hexDigital.indexOf(hexs[2 * i + 1]);
            bytes[i] = (byte) (n & 0xff);
        }
        // byte[]--&gt;String
        try {
            result = new String(bytes, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return result;

    }

    @Test
    public void test5() {
        int num = 'a';
        System.out.println(num);
        System.out.println("abc".hashCode());

    }
}