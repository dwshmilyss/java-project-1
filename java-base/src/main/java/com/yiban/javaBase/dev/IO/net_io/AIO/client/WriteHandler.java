package com.yiban.javaBase.dev.IO.net_io.AIO.client;


import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.CountDownLatch;

/**
 * @auther WEI.DUAN
 * @date 2018/1/25
 * @website http://blog.csdn.net/dwshmilyss
 */
public class WriteHandler implements CompletionHandler<Integer, ByteBuffer> {
    private AsynchronousSocketChannel clientChannel;
    private CountDownLatch countDownLatch;

    public WriteHandler(AsynchronousSocketChannel clientChannel, CountDownLatch countDownLatch) {
        this.clientChannel = clientChannel;
        this.countDownLatch = countDownLatch;
    }

    @Override
    public void completed(Integer result, ByteBuffer attachment) {
        //完成全部数据的写入
        if (attachment.hasRemaining()) {
            clientChannel.write(attachment, attachment, this);
        } else {
            //读取数据
            ByteBuffer readBuffer = ByteBuffer.allocate(1024);
            clientChannel.read(readBuffer, readBuffer, new ReadHandler(clientChannel, countDownLatch));
        }
    }

    @Override
    public void failed(Throwable exc, ByteBuffer attachment) {

    }
}
