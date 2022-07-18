package com.yiban.javaBase.dev.IO.net_io.AIO.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.util.concurrent.CountDownLatch;

/**
 * @auther WEI.DUAN
 * @date 2018/1/25
 * @website http://blog.csdn.net/dwshmilyss
 */
public class AsyncServerHandler implements Runnable {
    public CountDownLatch countDownLatch;
    public AsynchronousServerSocketChannel channel;

    public AsyncServerHandler(int port) {
        //创建服务端通道
        try {
            channel = AsynchronousServerSocketChannel.open();
            //绑定端口
            channel.bind(new InetSocketAddress(port));
            System.out.println("服务端已启动，端口：" + port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        /**
         * CountDownLatch的作用：在完成一组正在执行的操作之前，允许当前的线程一直阻塞
         * 此处，让线程在此阻塞，防止服务端执行完成后退出
         */
        countDownLatch = new CountDownLatch(1);
        //用于接收客户端的连接
        channel.accept(this, new AcceptHandler());
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
