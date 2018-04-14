package com.yiban.javaBase.dev.IO.net_io.AIO.server;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

/**
 * 服务端消息处理类
 *
 * @auther WEI.DUAN
 * @date 2018/1/25
 * @website http://blog.csdn.net/dwshmilyss
 */
public class AcceptHandler implements CompletionHandler<AsynchronousSocketChannel, AsyncServerHandler> {
    @Override
    public void completed(AsynchronousSocketChannel channel, AsyncServerHandler serverHandler) {
        //继续接收其他客户端的请求
        Server.clientCount++;
        System.out.println("连接的客户端数 = " + Server.clientCount);
        serverHandler.channel.accept(serverHandler, this);
        //创建新的缓冲区
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        //异步读 第三个参数是接收消息后的回调业务Handler
        channel.read(buffer, buffer, new ReadHandler(channel));
    }

    @Override
    public void failed(Throwable exc, AsyncServerHandler attachment) {
        exc.printStackTrace();
        attachment.countDownLatch.countDown();
    }
}
