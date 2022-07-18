package com.yiban.javaBase.dev.utils;

import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.math.NumberUtils;

/**
 * NumberUtils 数字相关的工具类
 */
public class NumberUtilsDemo {
    public static void main(String[] args) {
        testNumberUtils();
    }

    public static void testNumberUtils() {
        String str = "12.7";
        /*
         * org.apache.commons.lang.NumberUtils已经被弃用,
         * 注意要引入org.apache.commons.lang.math.NumberUtils
         */

        // 判断字符串是否为整数
        NumberUtils.isDigits(str);

        // 判断字符串是否为数字
        NumberUtils.isNumber(str);
        NumberUtils.isCreatable(str);

        // 获取参数中最大的值,支持传入数组
        NumberUtils.max(10, 20, 30);

        // 获取参数中最小的值,支持传入数组
        NumberUtils.min(10, 20, 30);

        // 将字符串转换为int类型,支持float,long,short等数值类型
        NumberUtils.toInt(str);

        // 通过字符串创建BigDecimal类型 ,支持int,float,long等数值
        NumberUtils.createBigDecimal(str);
    }

    public static void testRandomUtils() {
        /*
         * RandomUtils帮助我们产生随机数,不止是数字类型 ,
         * 连boolean类型都可以通过RandomUtils产生
         */
        RandomUtils.nextBoolean();
        RandomUtils.nextDouble();
        RandomUtils.nextLong();
        // 注意这里传入的参数不是随机种子,而是在0~1000之间产生一位随机数
        RandomUtils.nextInt(0,1000);
    }
}
