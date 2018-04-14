package com.yiban.javaBase.dev.IO.net_io.AIO.server;

import com.yiban.javaBase.dev.IO.net_io.utils.Calculator;

import javax.script.ScriptException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

/**
 * 服务端处理读取消息
 *
 * @auther WEI.DUAN
 * @date 2018/1/25
 * @website http://blog.csdn.net/dwshmilyss
 */
public class ReadHandler implements CompletionHandler<Integer, ByteBuffer> {
    //读取半包消息和发送应答
    private AsynchronousSocketChannel channel;

    public ReadHandler(AsynchronousSocketChannel channel) {
        this.channel = channel;
    }

    //读取到消息后处理
    @Override
    public void completed(Integer result, ByteBuffer attachment) {
        //flip
        attachment.flip();
        //根据缓冲区中数据长度创建byte[]
        byte[] message = new byte[attachment.remaining()];
        attachment.get(message);
        try {
            String expression = new String(message, "UTF-8");
            System.out.println("服务端收到消息：" + expression);

            String calResult = null;
            try {
                calResult = Calculator.Instance.cal(expression).toString();
            } catch (ScriptException e) {
                calResult = "计算错误：" + e.getMessage();
            }
            //向客户端发消息
            doWrite(calResult);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private void doWrite(String result) {
        byte[] bytes = result.getBytes();
        ByteBuffer writeBuffer = ByteBuffer.allocate(bytes.length);
        writeBuffer.put(bytes);
        writeBuffer.flip();
        //异步写数据，参数和读数据一样
        channel.write(writeBuffer, writeBuffer, new CompletionHandler<Integer, ByteBuffer>() {
            @Override
            public void completed(Integer result, ByteBuffer attachment) {
                //如果没有发送完，就继续直到发送完成
                if (writeBuffer.hasRemaining()) {
                    channel.write(writeBuffer, writeBuffer, this);
                } else {
                    //创建新的buffer
                    ByteBuffer readBuffer = ByteBuffer.allocate(1024);
                    //异步读，第三个参数为接收消息后的回调的业务handler
                    channel.read(readBuffer, readBuffer, new ReadHandler(channel));
                }
            }

            @Override
            public void failed(Throwable exc, ByteBuffer attachment) {
                try {
                    channel.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void failed(Throwable exc, ByteBuffer attachment) {
        try {
            this.channel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
