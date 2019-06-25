package com.yiban.javaBase.dev.IO.file_io;

import org.junit.Assert;
import sun.misc.Cleaner;
import sun.nio.ch.DirectBuffer;
import sun.nio.ch.FileChannelImpl;

import java.io.*;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Scanner;

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
//        testMappedByteBuffer();

//        for (TestMappedByteBuffer.Tester tester : TestMappedByteBuffer.testers) {
//            tester.runTest();
//        }

        testMappedByteBufferRead();
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

    /**
     * 1/ MappedByteBuffer使用虚拟内存，因此分配(map)的内存大小不受JVM的-Xmx参数限制，但是也是有大小限制的。
     * 2/ 如果当文件超出1.5G限制时，可以通过position参数重新map文件后面的内容。
     * 3/ MappedByteBuffer在处理大文件时的确性能很高，但也存在一些问题，如内存占用、文件关闭不确定，被其打开的文件只有在垃圾回收的才会被关闭，而且这个时间点是不确定的。
     */
    public static void testMappedByteBuffer() {
        ByteBuffer byteBuffer = ByteBuffer.allocate(1 * 1024 * 1024);
        byte[] bytes = new byte[1 * 1024 * 1024];
        File file = null;
        FileInputStream fis = null;
        FileOutputStream fos = null;
        FileChannel inChannel = null;
        FileChannel outChannel = null;
        MappedByteBuffer inMappedByteBuffer = null;
        try {
            fis = new FileInputStream("d://template_pro_201810_38.log");
            fos = new FileOutputStream("d://template_pro_201810_38_bak.log");
            inChannel = fis.getChannel();
            outChannel = fos.getChannel();
            long timeStar = 0L;
            long timeEnd = 0L;
            /**
             * 测试两种读的效率
             */
//            timeStar = System.currentTimeMillis();
//            //普通读(Read time :15ms)
////            inChannel.read(byteBuffer);
////            byteBuffer.clear();
//            //Memory Mapped Files 读 (Read time :11ms)
////            MappedByteBuffer mbb = inChannel.map(FileChannel.MapMode.READ_ONLY, 0, inChannel.size());
//            System.out.println("file size = " + (int)inChannel.size() / 1024 / 1024 + "m");
//            timeEnd = System.currentTimeMillis();
//            System.out.println("Read time :" + (timeEnd - timeStar) + "ms");

            /**
             * 测试两种写方式的效率
             */
            timeStar = System.currentTimeMillis();
            //普通写(Write time :285ms)
//            while (inChannel.read(byteBuffer) != -1) {
//                byteBuffer.flip();
//                outChannel.write(byteBuffer);
//                byteBuffer.clear();
//            }
            //Memory Mapped Files 写 ()
            file = new File("d://template_pro_201810_38.log");
            RandomAccessFile source = new RandomAccessFile(file, "r");
            inChannel = source.getChannel();
            outChannel = new RandomAccessFile("d://template_pro_201810_38_bak.log", "rw").getChannel();
            long size = inChannel.size();
            inMappedByteBuffer = inChannel.map(FileChannel.MapMode.READ_ONLY, 0, size);
            MappedByteBuffer outMappedByteBuffer = outChannel.map(FileChannel.MapMode.READ_WRITE, 0, size);
            /**
             * 下面两种方法都可以实现写入文件 但是貌似第一个更快一些
             */
            //Write time :221ms
            while (inMappedByteBuffer.hasRemaining()) {
                ByteBuffer buffer = inMappedByteBuffer.get(bytes);
                outMappedByteBuffer.put(buffer);
            }
            //Write time :340ms
//            for (int i = 0; i < size; i++) {
//                byte b = inMappedByteBuffer.get(i);
//                outMappedByteBuffer.put(i, b);
//            }
            timeEnd = System.currentTimeMillis();
            System.out.println("Write time :" + (timeEnd - timeStar) + "ms");
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (inChannel != null) {
                    inChannel.close();
                }
                if (outChannel != null) {
                    outChannel.close();
                }
                if (fis != null) {
                    fis.close();
                }
                if (fos != null) {
                    fos.close();
                }
                /**
                 * MappedByteBuffer对象会一直持有文件句柄，直到GC时释放，所以是无法立刻删除其关联的文件的
                 * 要想删除，则需要调用cleanHandle方法
                 */
                cleanHandle(inMappedByteBuffer);
                file.delete();
                Assert.assertEquals(false, file.exists());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * clean handle method
     *
     * @param obj process object
     */
    private static void cleanHandle(final Object obj) {
        // validate method parameter
        if (obj == null) {
            return;
        }

        AccessController.doPrivileged(new PrivilegedAction<Object>() {
            @Override
            public Object run() {
                try {
                    Method cleaner = obj.getClass().getMethod("cleaner",
                            new Class[0]);
                    if (cleaner != null) {
                        cleaner.setAccessible(true);
                        ((sun.misc.Cleaner) cleaner.invoke(obj, new Object[0]))
                                .clean();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
        });
    }

    static class TestMappedByteBuffer {
        private static int len = 0x2FFFFFFF;//768MB
        private static byte[] bytes = new byte[1 * 1024 * 1024];

        private abstract static class Tester {
            private String name;

            public Tester(String name) {
                this.name = name;
            }

            public void runTest() {
                System.out.print(name + " : ");
                long start = System.currentTimeMillis();
                test();
                System.out.println(System.currentTimeMillis() - start + " ms");
            }

            protected abstract void test();
        }

        private static Tester[] testers = {
                //这个过程贼慢
                new Tester("stream RW") {
                    @Override
                    protected void test() {
                        try {
                            FileInputStream fis = new FileInputStream(new File("d://template_pro_201810_38.log"));
                            DataInputStream dis = new DataInputStream(fis);
                            FileOutputStream fos = new FileOutputStream("d://aaa.txt");
                            DataOutputStream dos = new DataOutputStream(fos);
                            byte b = 0;
                            //读取是一个byte一个byte的读
                            for (int i = 0; i < len; i++) {
                                dos.writeByte(b);
                                dos.flush();
                            }
                            //写也是一个byte一个byte的写
                            while (dis.read() != -1){
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        } finally {
                            //TODO 关闭流 这里为了省事不写了
                        }
                    }
                },
                new Tester("Mapped RW") {
                    @Override
                    protected void test() {
                        FileChannel inChannel = null;
                        FileChannel outChannel = null;
                        try {
                            inChannel = FileChannel.open(Paths.get("d://template_pro_201810_38.log"), StandardOpenOption.READ, StandardOpenOption.WRITE);
                            File file = new File("d://bbb.txt");
                            if (!file.exists()) {
                                file.createNewFile();
                            }
                            outChannel = FileChannel.open(file.toPath(), StandardOpenOption.READ, StandardOpenOption.WRITE);
                            MappedByteBuffer inMappedByteBuffer = inChannel.map(FileChannel.MapMode.READ_WRITE, 0, inChannel.size());
                            MappedByteBuffer outMappedByteBuffer = outChannel.map(FileChannel.MapMode.READ_WRITE, 0, len);
                            for (int i = 0; i < len; i++) {
                                outMappedByteBuffer.put((byte) 0);
                            }
                            while (inMappedByteBuffer.hasRemaining()) {
                                inMappedByteBuffer.get();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        } finally {
                        }
                    }
                },
                new Tester("Mapped PRIVATE") {
                    @Override
                    protected void test() {
                        FileChannel inChannel = null;
                        FileChannel outChannel = null;
                        try {
                            inChannel = FileChannel.open(Paths.get("d://template_pro_201810_38.log"), StandardOpenOption.READ, StandardOpenOption.WRITE);
                            File file = new File("d://ccc.txt");
                            if (!file.exists()) {
                                file.createNewFile();
                            }
                            outChannel = FileChannel.open(file.toPath(), StandardOpenOption.READ, StandardOpenOption.WRITE);
                            MappedByteBuffer inMappedByteBuffer = inChannel.map(FileChannel.MapMode.PRIVATE, 0, inChannel.size());
                            MappedByteBuffer outMappedByteBuffer = outChannel.map(FileChannel.MapMode.PRIVATE, 0, len);
                            for (int i = 0; i < len; i++) {
                                outMappedByteBuffer.put((byte) 0);
                            }
                            while (inMappedByteBuffer.hasRemaining()) {
                                inMappedByteBuffer.get();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        } finally {
                        }
                    }
                }
        };

    }


    public static void testMappedByteBufferRead() {
        File file = new File("d://template_pro_201810_38.log");
        long len = file.length();
        byte[] bytes = new byte[(int) len];
        try {
            MappedByteBuffer mappedByteBuffer = new RandomAccessFile(file, "r").getChannel().map(FileChannel.MapMode.READ_ONLY, 0, len);
            for (int offset = 0; offset < len; offset++) {
                byte b = mappedByteBuffer.get();
                bytes[offset] = b;
            }

            Scanner scanner = new Scanner(new ByteArrayInputStream(bytes)).useDelimiter(" ");
            while (scanner.hasNext()) {
                System.out.print(scanner.next() + " ");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
        }
    }

}
