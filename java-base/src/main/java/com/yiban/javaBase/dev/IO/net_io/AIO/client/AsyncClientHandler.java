package com.yiban.javaBase.dev.IO.net_io.AIO.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.CountDownLatch;

/**
 * @auther WEI.DUAN
 * @date 2018/1/25
 * @website http://blog.csdn.net/dwshmilyss
 */
public class AsyncClientHandler implements CompletionHandler<Void, AsyncClientHandler>, Runnable {
    private AsynchronousSocketChannel clientChannel;
    private String ip;
    private int port;
    private CountDownLatch countDownLatch;

    public AsyncClientHandler(String ip, int port) {
        this.ip = ip;
        this.port = port;
        try {
            clientChannel = AsynchronousSocketChannel.open();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        //创建一个线程等待
        countDownLatch = new CountDownLatch(1);
        //发起异步连接操作，回调函数就是这个类本身，如果连接成功则回调completed()
        clientChannel.connect(new InetSocketAddress(ip, port), this, this);
        //等待
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        try {
            clientChannel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 向服务端发送消息
     *
     * @param msg
     */
    public void sendMsg(String msg) {
//        ByteBuffer outbuffer = ByteBuffer.wrap(msg.getBytes());
//        clientChannel.write(outbuffer,outbuffer,new WriteHandler(clientChannel,countDownLatch));
        byte[] request = msg.getBytes();
        ByteBuffer writeBuffer = ByteBuffer.allocate(request.length);
        writeBuffer.put(request);
        writeBuffer.flip();
        //异步写
        clientChannel.write(writeBuffer, writeBuffer, new WriteHandler(clientChannel, countDownLatch));
    }

    /**
     * 连接服务器成功 意味着TCP三次握手完成
     *
     * @param result
     * @param attachment
     */
    @Override
    public void completed(Void result, AsyncClientHandler attachment) {
        System.out.println("客户端成功连接服务器。。。。");
    }

    @Override
    public void failed(Throwable exc, AsyncClientHandler attachment) {
        System.out.println("连接服务器失败。。。");
        exc.printStackTrace();
        try {
            clientChannel.close();
            //等待线程-1
            countDownLatch.countDown();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
