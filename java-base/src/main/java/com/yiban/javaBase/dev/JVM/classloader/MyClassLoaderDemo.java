package com.yiban.javaBase.dev.JVM.classloader;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;

/**
 * define classloader by myself
 * 自定义类加载器
 * 1、要继承ClassLoader
 * 2、要实现findClass函数
 *
 * @auther WEI.DUAN
 * @create 2017/11/18
 * @blog http://blog.csdn.net/dwshmilyss
 */
public class MyClassLoaderDemo extends ClassLoader {

    public MyClassLoaderDemo() {

    }

    public MyClassLoaderDemo(ClassLoader parent) {
        super(parent);
    }

    public static void main(String[] args) {
        MyClassLoaderDemo mcl = new MyClassLoaderDemo();
        Object obj = null;
        try {
            //note:这里一定要用全限定名
            Class<?> clazz = Class.forName("People", true, mcl);
            obj = clazz.newInstance();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        System.out.println(obj);
        System.out.println(obj.getClass().getClassLoader());//打印出我们的自定义类加载器

    }

    //
    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        //记住 不能用当前项目里编译好的这个People类，因为在当前应用下，所以不会用自己定义的类加载器加载
        //而只会用AppClassLoader
        File file = new File("H:/People.class");
        try {
            byte[] bytes = getClassBytes(file);
            //defineClass方法可以把二进制流字节组成的文件转换为一个java.lang.Class
            Class<?> c = this.defineClass(name, bytes, 0, bytes.length);
            return c;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return super.findClass(name);
    }

    private byte[] getClassBytes(File file) throws Exception {
        // 这里要读入.class的字节，因此要使用字节流
        FileInputStream fis = new FileInputStream(file);
        FileChannel fc = fis.getChannel();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        WritableByteChannel wbc = Channels.newChannel(baos);
        ByteBuffer by = ByteBuffer.allocate(1024);

        while (true) {
            int i = fc.read(by);
            if (i == 0 || i == -1)
                break;
            by.flip();
            wbc.write(by);
            by.clear();
        }
        fis.close();
        return baos.toByteArray();
    }
}
