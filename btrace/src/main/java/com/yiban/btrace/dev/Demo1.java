package com.yiban.btrace.dev;

import java.util.Random;

/**
 * @auther WEI.DUAN
 * @date 2018/11/30
 * @website http://blog.csdn.net/dwshmilyss
 */
public class Demo1 {
    public static void main(String[] args) throws Exception {
        //CaseObject object = new CaseObject();
        while (true) {
            Random random = new Random();
            execute(random.nextInt(4000));

            //object.execute(random.nextInt(4000));
        }



    }
    public static Integer execute(int sleepTime) {
        try {
            Thread.sleep(sleepTime);
        } catch (Exception e) {
        }
        System.out.println("sleep time is=>"+sleepTime);
        return 0;
    }
}