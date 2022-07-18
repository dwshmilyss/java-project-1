package com.yiban.javaBase.dev.IO.file_io;

import org.apache.commons.lang3.time.StopWatch;
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
import java.util.concurrent.TimeUnit;

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

        testMappedByteBuffer();
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
        File file = null;
        byte[] bytes = new byte[1024];
        FileInputStream fis = null;
        FileOutputStream fos = null;
        FileChannel inChannel = null;
        FileChannel outChannel = null;
        MappedByteBuffer inMappedByteBuffer = null;
        try {
            //1. InputStream和OutputStream
            fis = new FileInputStream("d:/logs/rs_g1_gc.log");
            fos = new FileOutputStream("d:/logs/rs_g1_gc.log.tmp");
            inChannel = fis.getChannel();
            outChannel = fos.getChannel();
//            StopWatch stopWatch = StopWatch.createStarted();
            org.springframework.util.StopWatch stopWatch1 = new org.springframework.util.StopWatch("tast group");
            stopWatch1.start("task1");
//            普通读写3m的文件(cost time:4ms)
            while (inChannel.read(byteBuffer) != -1) {
                byteBuffer.flip();
//                while (byteBuffer.hasRemaining()) {
//                    System.out.print((char) byteBuffer.get());
//                }
                //这里如果用byteBuffer写入文件就不能用上面的打印，因为上面的byteBuffer.get()应该是把数据从缓存中取出了，这里再用byteBuffer就取不到数据了
                outChannel.write(byteBuffer);
                byteBuffer.clear();
            }

            System.out.println("file size = " + (int) inChannel.size() / 1024 / 1024 + "m");
            stopWatch1.stop();

//            Memory Mapped Files 读写
            file = new File("d:/logs/rs_g1_gc.log");
            System.out.println("file.length() = " + file.length()/1024/1024 + "mb");
            RandomAccessFile source = new RandomAccessFile(file, "r");
            inChannel = source.getChannel();
            outChannel = new RandomAccessFile("d:/logs/rs_g1_gc.log.tmp1", "rw").getChannel();
            FileChannel outChannel1 = new RandomAccessFile("d:/logs/rs_g1_gc.log.tmp2", "rw").getChannel();
            long size = inChannel.size();
            inMappedByteBuffer = inChannel.map(FileChannel.MapMode.READ_ONLY, 0, size);
            MappedByteBuffer outMappedByteBuffer = outChannel.map(FileChannel.MapMode.READ_WRITE, 0, size);
            MappedByteBuffer outMappedByteBuffer1 = outChannel1.map(FileChannel.MapMode.READ_WRITE, 0, size);
            stopWatch1.start("task2");
            //3ms(这种方式最快，上面的次之，下面的最慢)
            while (inMappedByteBuffer.hasRemaining()) {
                ByteBuffer buffer = inMappedByteBuffer.get(bytes);
                outMappedByteBuffer.put(buffer);
            }
            stopWatch1.stop();
            stopWatch1.start("task3");
            //12ms
            for (int i = 0; i < size; i++) {
                byte b = inMappedByteBuffer.get(i);
                outMappedByteBuffer1.put(i, b);
            }
            stopWatch1.stop();
            String result = stopWatch1.prettyPrint();
            System.err.println(result);

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
                 * MappedByteBuffer 对象会一直持有文件句柄，直到GC时释放，所以是无法立刻删除其关联的文件的
                 * 要想删除，则需要调用cleanHandle方法先清除MappedByteBuffer对象
                 * 而且需要先关闭上面的输入输出流，否则也无法删除
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
     * 清除 MappedByteBuffer 所占用的对象
     * 如果是用MappedByteBuffer映射了物理文件，若要立刻删除文件，则这部必须在删除文件动作前调用
     * 否则只能等到GC回收了MappedByteBuffer对象的引用，文件才会被删除
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
                            while (dis.read() != -1) {
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
