package com.yiban.javaBase.dev.syntax;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;

/**
 * @auther WEI.DUAN
 * @date 2019/3/27
 * @website http://blog.csdn.net/dwshmilyss
 */
public class LocalDateDemo {
    public static void main(String[] args) {
        LocalDate today = LocalDate.now();
        // 取当前日期
        System.out.println(today);
        // 根据年月日取日期
        System.out.println(LocalDate.of(2018, 12, 25));
        // 根据字符串
        System.out.println(LocalDate.parse("2014-02-28"));
        // 取本月第1天
        System.out.println(today.with(TemporalAdjusters.firstDayOfMonth()));
        // 取本月第2天
        System.out.println(today.withDayOfMonth(2));
        // 取本月最后一天
        LocalDate lastDayOfThisMonth = today.with(TemporalAdjusters.lastDayOfMonth());
        System.out.println(lastDayOfThisMonth);
        //取下个月的第一天
        System.out.println(lastDayOfThisMonth.plusDays(1));
        //取2017年1月第一个周一
        System.out.println(LocalDate.parse("2017-01-01").with(TemporalAdjusters.firstInMonth(DayOfWeek.MONDAY)));
    }
}