package com.yiban.javaBase.dev.IO.file_io;
import	java.nio.ByteBuffer;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;

/**
 * 利用nio实现文件copy
 *
 * @auther WEI.DUAN
 * @date 2017/5/4
 * @website http://blog.csdn.net/dwshmilyss
 */
public class FileCopyDemo {
    private static final Logger LOGGER = LoggerFactory.getLogger(FileCopyDemo.class);

    /**
     * 不考虑多线程优化，单线程文件复制最快的方法是(文件越大该方法越有优势，一般比常用方法快30+%):
     *
     * @param src
     * @param dest
     */
    public static void copyByNIO(File src, File dest) {
        if (!dest.exists()) {
            try {
                dest.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        FileInputStream fis = null;
        FileOutputStream fos = null;
        FileChannel inChannel = null;
        FileChannel outChannel = null;
        try {
            fis = new FileInputStream(src);
            fos = new FileOutputStream(dest);

            inChannel = fis.getChannel();//得到对应的文件通道
            outChannel = fos.getChannel();//得到对应的文件通道

//            inChannel.transferTo(0, inChannel.size(), outChannel);//连接两个通道，并且从in通道读取，然后写入out通道

            inChannel.transferTo(0, inChannel.size(), Channels.newChannel(fos));//连接两个通道，并且从in通道读取，然后写入out通道
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                fis.close();
                inChannel.close();
                fos.close();
                outChannel.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 如果需要监测复制进度，可以用第二快的方法(留意buffer的大小，对速度有很大影响):
     *
     * @param source
     * @param target
     */
    public static void nioBufferCopy(File source, File target) {
        if (!target.exists()) {
            try {
                target.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        FileChannel inChannel = null;
        FileChannel outChannel = null;
        FileInputStream inStream = null;
        FileOutputStream outStream = null;
        try {
            inStream = new FileInputStream(source);
            outStream = new FileOutputStream(target);
            inChannel = inStream.getChannel();
            outChannel = outStream.getChannel();
            ByteBuffer buffer = ByteBuffer.allocate(10);
            while (inChannel.read(buffer) != -1) {
                //切换模式（由读模式切换为写模式）
                //Buffer有两种模式，写模式和读模式。在读模式下调用flip()之后，Buffer从读模式变成写模式。
                buffer.flip();
                //下面这句话不要也可以 只是打印出buffer里面的内容而已 和读取写入数据无关
//                while (buffer.hasRemaining()) {
//                    System.out.print((char) buffer.get());
//                }
                //写入数据
                outChannel.write(buffer);
                //清空缓存
                buffer.clear();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                inStream.close();
                inChannel.close();
                outChannel.close();
                outChannel.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public static void copyByIO(File src, File dest) {
        InputStream inputStream = null;
        OutputStream outputStream = null;
        byte[] buffer = new byte[1024];
        int byteRead; // 读取的字节数
        try {
//            inputStream = new FileInputStream(src);
//            outputStream = new FileOutputStream(dest);
            //或者用BufferedInputStream包装一下
            BufferedInputStream is = new BufferedInputStream(new FileInputStream(src));
            BufferedOutputStream os = new BufferedOutputStream(new FileOutputStream(dest));
            while ((byteRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, byteRead);
            }
            while ((byteRead = is.read(buffer)) != -1) {
                outputStream.write(buffer, 0, byteRead);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                inputStream.close();
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 复制单个文件
     *
     * @param srcFileName  待复制的文件名
     * @param destFileName 目标文件名
     * @param overlay      如果目标文件存在，是否覆盖
     * @return 如果复制成功返回true，否则返回false
     */
    public static boolean copyFile(String srcFileName, String destFileName, boolean overlay) {
        File srcFile = new File(srcFileName);

        // 判断源文件是否存在
        if (!srcFile.exists()) {
            LOGGER.error("源文件：" + srcFileName + "不存在！");
            return false;
        }
        if (!srcFile.isFile()) {
            LOGGER.error("复制文件失败，源文件：" + srcFileName + "不是一个文件！");
            return false;
        }

        // 判断目标文件是否存在
        File destFile = new File(destFileName);
        if (destFile.exists()) {
            // 如果目标文件存在并允许覆盖
            if (overlay) {
                // 删除已经存在的目标文件，无论目标文件是目录还是单个文件
                new File(destFileName).delete();
            }
        } else {
            // 如果目标文件所在目录不存在，则创建目录
            if (!destFile.getParentFile().exists()) {
                // 目标文件所在目录不存在
                if (!destFile.getParentFile().mkdirs()) {
                    // 复制文件失败：创建目标文件所在目录失败
                    return false;
                }
            }
        }

        // 复制文件
        int byteread = 0; // 读取的字节数
        InputStream in = null;
        OutputStream out = null;

        try {
            in = new FileInputStream(srcFile);
            out = new FileOutputStream(destFile);
            byte[] buffer = new byte[1024];

            while ((byteread = in.read(buffer)) != -1) {
                out.write(buffer, 0, byteread);
            }
            return true;
        } catch (FileNotFoundException e) {
            return false;
        } catch (IOException e) {
            return false;
        } finally {
            try {
                if (out != null)
                    out.close();
                if (in != null)
                    in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 复制整个目录的内容
     *
     * @param srcDirName  待复制目录的目录名
     * @param destDirName 目标目录名
     * @param overlay     如果目标目录存在，是否覆盖
     * @return 如果复制成功返回true，否则返回false
     */
    public static boolean copyDirectory(String srcDirName, String destDirName,
                                        boolean overlay) {
        // 判断源目录是否存在
        File srcDir = new File(srcDirName);
        if (!srcDir.exists()) {
            LOGGER.error("复制目录失败：源目录" + srcDirName + "不存在！");
            return false;
        } else if (!srcDir.isDirectory()) {
            LOGGER.error("复制目录失败：" + srcDirName + "不是目录！");
            return false;
        }

        // 如果目标目录名不是以文件分隔符结尾，则加上文件分隔符
        if (!destDirName.endsWith(File.separator)) {
            destDirName = destDirName + File.separator;
        }
        File destDir = new File(destDirName);
        // 如果目标文件夹存在
        if (destDir.exists()) {
            // 如果允许覆盖则删除已存在的目标目录
            if (overlay) {
                new File(destDirName).delete();
            } else {
                LOGGER.error("复制目录失败：目的目录" + destDirName + "已存在！");
                return false;
            }
        } else {
            // 创建目的目录
            System.out.println("目的目录不存在，准备创建。。。");
            if (!destDir.mkdirs()) {
                System.out.println("复制目录失败：创建目的目录失败！");
                return false;
            }
        }

        boolean flag = true;
        File[] files = srcDir.listFiles();
        for (int i = 0; i < files.length; i++) {
            // 如果是文件 调用复制文件的方法直接复制
            if (files[i].isFile()) {
                flag = copyFile(files[i].getAbsolutePath(),
                        destDirName + files[i].getName(), overlay);
                if (!flag)
                    break;
            } else if (files[i].isDirectory()) {//如果是目录，就递归
                flag = copyDirectory(files[i].getAbsolutePath(),
                        destDirName + files[i].getName(), overlay);
                if (!flag)
                    break;
            }
        }
        if (!flag) {
            LOGGER.error("复制目录" + srcDirName + "至" + destDirName + "失败！");
            return false;
        } else {
            return true;
        }
    }


    @Test
    public void testFileNIO() throws Exception {
//        System.out.println("123456789".getBytes().length);
//        RandomAccessFile randomAccessFile = new RandomAccessFile("/Users/duanwei/test_nio.txt", "rw");
//        FileChannel inChannel = randomAccessFile.getChannel();
//        ByteBuffer buffer = ByteBuffer.allocate(10);
//        int bytesReaded = inChannel.read(buffer);
//        System.out.println(buffer.clear());
//        System.out.println(buffer.hasRemaining());
//        System.out.println("bytesReaded = " + bytesReaded);
//        buffer.put("1234567890".getBytes());
//        buffer.flip();
//        inChannel.write(buffer);
//        System.out.println(buffer.clear());
//
//        inChannel.close();
//        randomAccessFile.close();
        FileCopyDemo.nioBufferCopy(new File("/Users/duanwei/test_nio.txt"),new File("/Users/duanwei/test_nio_copy.txt "));
    }

}
