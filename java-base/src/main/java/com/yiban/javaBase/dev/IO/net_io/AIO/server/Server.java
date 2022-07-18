package com.yiban.javaBase.dev.IO.net_io.AIO.server;

/**
 * AIO
 * NIO2.0引入了新的异步通道的概念。并提供了异步文件通道和异步套接字通道的实现。对应UNIX中的事件驱动模型，它不需要selector对注册通道一直进行轮询，
 * 而且立即返回后等待系统后续的通知即实现异步读写
 *
 * @auther WEI.DUAN
 * @date 2018/1/25
 * @website http://blog.csdn.net/dwshmilyss
 */
public class Server {
    //标识连接的客户端个数
    public volatile static long clientCount = 0;
    private static int DEFAULT_PORT = 12345;
    private static AsyncServerHandler asyncServerHandler;

    public static void start() {
        start(DEFAULT_PORT);
    }

    private static synchronized void start(int port) {
        if (asyncServerHandler != null) {
            return;
        }
        asyncServerHandler = new AsyncServerHandler(port);
        new Thread(asyncServerHandler, "Server").start();
    }

    public static void main(String[] args) {
        Server.start();
    }
}
