package com.yiban.javaBase.dev.IO.net_io.BIO;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * BIO模式创建的客户端
 *
 * @auther WEI.DUAN
 * @date 2018/1/24
 * @website http://blog.csdn.net/dwshmilyss
 */
public class Client {
    private static int DEFAULT_SERVER_PORT = 12345;
    private static String DEFAULT_SERVER_IP = "localhost";

    public static void send(String expression) {
        send(DEFAULT_SERVER_PORT, expression);
    }

    private static void send(int port, String expression) {
        System.out.println("表达式为：" + expression);
        Socket client = null;
        BufferedReader in = null;
        PrintWriter out = null;
        String result = null;
        try {
            client = new Socket();
            client.connect(new InetSocketAddress(DEFAULT_SERVER_IP, DEFAULT_SERVER_PORT));
            out = new PrintWriter(client.getOutputStream());
            out.println(expression);
            out.flush();
            in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            System.out.println("结果为：" + in.readLine());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //一些必要的释放工作
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (out != null) {
                out.close();
            }
            if (client != null) {
                try {
                    client.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
