package com.yiban.javaBase.dev.IO;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;

/**
 * some method test for NIO
 *
 * @auther WEI.DUAN
 * @date 2017/11/22
 * @website http://blog.csdn.net/dwshmilyss
 */
public class NIOMethodDemo {
    public static final int SIZE = 1024;

    public static void main(String[] args) {
        testRead();
    }

    public static void testWrite() {
        String fileName = "out.txt";
        FileOutputStream fos = null;
        try {
            //这个路径如果是eclipse就是src目录下，如果是idea 那么就是src/resources目录
            System.out.println(NIOMethodDemo.class.getClassLoader().getResource(fileName).getPath());
            fos = new FileOutputStream(new File(NIOMethodDemo.class.getClassLoader().getResource(fileName).getPath()));
            FileChannel channel = fos.getChannel();
            ByteBuffer src = Charset.forName("utf8").encode("你好你好你好你好你好");
            // 字节缓冲的容量和limit会随着数据长度变化，不是固定不变的
            System.out.println("初始化容量和limit：" + src.capacity() + ","
                    + src.limit());
            int length = 0;

            while ((length = channel.write(src)) != 0) {
                /*
                 * 注意，这里不需要clear，将缓冲中的数据写入到通道中后 第二次接着上一次的顺序往下读
                 */
                System.out.println("写入长度:" + length);
            }
            channel.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    public static void testRead() {
        String fileName = "out.txt";
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(new File(NIOMethodDemo.class.getClassLoader().getResource(fileName).getPath()));
            FileChannel channel = fis.getChannel();
            int capacity = 1024;
            ByteBuffer bf = ByteBuffer.allocate(capacity);
            System.out.println("限制是：" + bf.limit() + "容量是：" + bf.capacity()
                    + "位置是：" + bf.position());
            int len = -1;
            while ((len = channel.read(bf)) != -1) {
                /**
                 * 注意，读取后，将位置置为0，将limit置为容量, 以备下次读入到字节缓冲中，从0开始存储
                 */
                byte[] bytes = bf.array();
                System.out.write(bytes, 0, len);
                System.out.println();
                System.out.println("限制是：" + bf.limit() + "容量是：" + bf.capacity()
                        + "位置是：" + bf.position());
                bf.clear();
            }
            channel.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
