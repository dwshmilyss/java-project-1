package com.yiban.javaBase.dev.IO.net_io.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * @auther WEI.DUAN
 * @date 2018/1/24
 * @website http://blog.csdn.net/dwshmilyss
 */
public class ServerHandler implements Runnable {
    private Socket socket;

    public ServerHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        BufferedReader in = null;
        PrintWriter out = null;
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            String expression;
            String result;
            while (true) {
                //通过BufferedReader读取一行
                //如果已经读到输入流尾部，返回null，退出循环
                //如果得到非空值，就尝试运行任务(计算表达式)并返回
                if ((expression = in.readLine()) != null) {
                    System.out.println("服务端收到的消息是：" + expression);
                    try {
                        result = Calculator.Instance.cal(expression).toString();
                    } catch (Exception e) {
                        result = "计算错误：" + e.getMessage();
                    }
                    System.out.println("result = " + result);
                    //发送的数据一定要有换行和flush()
                    out.println(result);
                    out.flush();
                } else {
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            //一些必要的释放工作
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                in = null;
            }
            if (out != null) {
                out.close();
                out = null;
            }
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                socket = null;
            }
        }

    }
}
