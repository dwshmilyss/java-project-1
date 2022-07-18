package com.yiban.javaBase.dev.IO.net_io.AIO.client;

/**
 * @auther WEI.DUAN
 * @date 2018/1/25
 * @website http://blog.csdn.net/dwshmilyss
 */
public class Client {
    private static String DEFAULT_IP = "localhost";
    private static int DEFAULT_PORT = 12345;
    private static AsyncClientHandler clientHandler;

    public static void start() {
        start(DEFAULT_IP, DEFAULT_PORT);
    }

    private static synchronized void start(String ip, int port) {
        if (clientHandler != null) return;
        clientHandler = new AsyncClientHandler(ip, port);
        new Thread(clientHandler, "Client").start();
    }

    public static boolean sendMsg(String msg) {
        if ("q".equals(msg)) return false;
        clientHandler.sendMsg(msg);
        return true;
    }

    public static void main(String[] args) {
        Client.start();
    }
}
