package com.yiban.javaBase.dev.JVM.classloader;

import com.yiban.javaBase.dev.JVM.classloader.test.IC;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * @auther WEI.DUAN
 * @create 2017/11/18
 * @blog http://blog.csdn.net/dwshmilyss
 * <p/>
 * URLClassLoader的作用主要是加载外部jar中的class
 */
public class URLClassLoaderDemo {
    public static void main(String[] args) {
        System.out.println(URLClassLoaderDemo.class.getClassLoader());
        System.out.println(Thread.currentThread().getContextClassLoader());

        System.out.println("========================");

        load("H:/test.jar", "com.yiban.javaBase.dev.JVM.classloader.test.C");
    }


    private static void load(String dir, String cname) {
        String jarName = "file:" + dir;
        System.out.println(jarName);
        try {
            File file = new File(jarName);
            URL url = file.toURI().toURL();
            URLClassLoader loader = new URLClassLoader(new URL[]{url});
            Class aClass = loader.loadClass(cname);
            //利用Java反射机制创建实例测试方法
            IC ic = (IC) aClass.newInstance();
            ic.action();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
