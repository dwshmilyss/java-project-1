package com.yiban.javaBase.dev.IO.net_io.BIO;

import java.util.Random;

/**
 * @auther WEI.DUAN
 * @date 2018/1/24
 * @website http://blog.csdn.net/dwshmilyss
 */
public class TestClient {
    public static void main(String[] args) {
//        //首先启动服务端
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    ServerNormal.start();
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        }).start();
//        //暂停1秒后在让客户端启动
//        try {
//            Thread.sleep(1000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
        char operators[] = {'+', '-', '*', '/'};
        Random random = new Random(System.currentTimeMillis());

        String expression = random.nextInt(10) + "" + operators[random.nextInt(4)] + "" + random.nextInt(10);
        Client.send(expression);
//        try {
//            Thread.currentThread().sleep(random.nextInt(1000));
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
    }
}
