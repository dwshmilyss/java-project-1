package com.yiban.hbase.test;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class Test {
    @org.junit.Test
    public void test1() {
        System.out.println(false ^ true);

        Long start_m = Long.parseLong("1150992000000");
        Random r = new Random();
        int day = r.nextInt(5);
        System.out.println("day = " + day);
        String start_m_str = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date(start_m));
        System.out.println("start_m_str = " + start_m_str);
        start_m = start_m + 86400000 * day;
//        String shijiancuo = start_m + "";
//        long lt = new Long(shijiancuo);
        Date datetwo = new Date(start_m);
        String start = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(datetwo);
        System.out.println("start = " + start);
    }
}
