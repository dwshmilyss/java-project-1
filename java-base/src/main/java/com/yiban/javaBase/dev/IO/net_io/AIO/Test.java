package com.yiban.javaBase.dev.IO.net_io.AIO;

import com.yiban.javaBase.dev.IO.net_io.AIO.client.Client;
import com.yiban.javaBase.dev.IO.net_io.AIO.server.Server;

import java.util.Scanner;

/**
 * @auther WEI.DUAN
 * @date 2018/1/25
 * @website http://blog.csdn.net/dwshmilyss
 */
public class Test {
    public static void main(String[] args) {
        //运行服务端
        Server.start();
        //避免客户端先于服务端启动
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //启动客户端
        Client.start();
        System.out.println("请输入要计算的表达式：");
        Scanner scanner = new Scanner(System.in);
        while (Client.sendMsg(scanner.nextLine())) ;
    }
}
