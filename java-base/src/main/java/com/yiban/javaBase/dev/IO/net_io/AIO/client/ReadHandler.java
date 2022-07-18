package com.yiban.javaBase.dev.IO.net_io.AIO.client;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.CountDownLatch;

/**
 * @auther WEI.DUAN
 * @date 2018/1/25
 * @website http://blog.csdn.net/dwshmilyss
 */
public class ReadHandler implements CompletionHandler<Integer, ByteBuffer> {
    private AsynchronousSocketChannel clientChannel;
    private CountDownLatch countDownLatch;

    public ReadHandler(AsynchronousSocketChannel clientChannel, CountDownLatch countDownLatch) {
        this.clientChannel = clientChannel;
        this.countDownLatch = countDownLatch;
    }

    @Override
    public void completed(Integer result, ByteBuffer buffer) {
        buffer.flip();
        byte[] bytes = new byte[buffer.remaining()];
        buffer.get(bytes);
        String body;
        try {
            body = new String(bytes, "UTF-8");
            System.out.println("客户端收到的消息为：" + body);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void failed(Throwable exc, ByteBuffer attachment) {
        System.out.println("读取数据失败。。。");
        try {
            clientChannel.close();
            countDownLatch.countDown();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
