package com.yiban.javaBase.dev.IO.net_io.BIO;

import com.yiban.javaBase.dev.IO.net_io.utils.ServerHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 同步阻塞式IO创建的Server端
 *
 * @auther WEI.DUAN
 * @date 2018/1/24
 * @website http://blog.csdn.net/dwshmilyss
 */
public class Server {
    //默认端口号
    private static int DEFAULT_PORT = 12345;
    //单例的ServerSocket
    private static ServerSocket serverSocket;
    // 线程池(懒汉模式)
    private static ExecutorService executorService = Executors.newFixedThreadPool(60);

    //启动服务端
    public static void start() throws Exception {
        start(DEFAULT_PORT);
    }

    public synchronized static void start(int port) throws IOException {
        if (serverSocket != null) return;
        try {
            //启动ServerSocket
            serverSocket = new ServerSocket(port);
            System.out.println("服务端已启动，端口号：" + port);
            while (true) {
                //针对每一个连接都启动一个Thread,返回的是客户端scoket，可以获取到客户端的IP和端口
                Socket clientSocket = serverSocket.accept();//阻塞
                System.out.println(clientSocket.getRemoteSocketAddress() + " connect!" + System.currentTimeMillis());
                //这里可以优化，用线程池
//                new Thread(new ServerHandler(clientSocket)).start();
                /**
                 * 使用线程池的好处就是可以控制后端的最大线程数量，防止资源被耗尽，但是在高并发的场景中，会造成很多连接等待。
                 */
                executorService.execute(new ServerHandler(clientSocket));
            }
        } finally {
            if (serverSocket != null) {
                System.out.println("服务端已关闭。");
                serverSocket.close();
                serverSocket = null;
            }
        }
    }
}
