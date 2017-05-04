package com.yiban.javaBase.dev.nio;

import java.io.*;
import java.nio.channels.FileChannel;

/**
 * 利用nio实现文件copy
 *
 * @auther WEI.DUAN
 * @date 2017/5/4
 * @website http://blog.csdn.net/dwshmilyss
 */
public class FileCopyDemo {


    public static void copyByNIO(File src, File dest) {
        FileInputStream fi = null;
        FileOutputStream fo = null;
        FileChannel in = null;
        FileChannel out = null;
        try {
            fi = new FileInputStream(src);
            fo = new FileOutputStream(dest);
            in = fi.getChannel();//得到对应的文件通道
            out = fo.getChannel();//得到对应的文件通道
            in.transferTo(0, in.size(), out);//连接两个通道，并且从in通道读取，然后写入out通道
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                fi.close();
                in.close();
                fo.close();
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public static void copyByIO(File src, File dest) {
        FileInputStream fileInputStream = null;
        FileOutputStream fileOutputStream = null;
        byte[] buffer = new byte[1024];
        int byteRead = 0; // 读取的字节数
        try {
            fileInputStream = new FileInputStream(src);
            fileOutputStream = new FileOutputStream(dest);
            while ((byteRead = fileInputStream.read(buffer)) != -1){
                fileOutputStream.write(buffer, 0, byteRead);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                fileInputStream.close();
                fileOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
