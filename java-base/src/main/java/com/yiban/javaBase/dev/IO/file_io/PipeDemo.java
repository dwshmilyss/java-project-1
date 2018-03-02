package com.yiban.javaBase.dev.IO.file_io;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Pipe;

/**
 * 管道是2个线程之间的单向数据连接。Pipe有一个source通道和一个sink通道。数据会被写到sink通道，从source通道读取。
 *
 * @auther WEI.DUAN
 * @date 2017/5/4
 * @website http://blog.csdn.net/dwshmilyss
 */
public class PipeDemo {
    public static void main(String[] args) {
        try {
            Pipe pipe = Pipe.open();
            Pipe.SinkChannel sinkChannel = pipe.sink();
            String newData = "New String";
            ByteBuffer buf = ByteBuffer.allocate(10);
            buf.clear();
            buf.put(newData.getBytes());
            buf.flip();
            sinkChannel.write(buf);
            Pipe.SourceChannel sourceChannel = pipe.source();
            int bytesRead = sourceChannel.read(buf);
            System.out.println("Read " + bytesRead);
            while (bytesRead != -1) {
                buf.flip();
                System.out.print((char) buf.get());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
