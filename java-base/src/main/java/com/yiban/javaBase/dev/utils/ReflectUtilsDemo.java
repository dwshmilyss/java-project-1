package com.yiban.javaBase.dev.utils;

import com.yiban.javaBase.dev.annotations.lombok.Test;
import com.yiban.javaBase.dev.reflect.Demo1;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.reflect.ConstructorUtils;
import org.apache.commons.lang3.reflect.FieldUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

/**
 * 反射相关的工具类demo
 */
public class ReflectUtilsDemo {
    public static void main(String[] args) throws Exception {
//        testClassUtils();
//        testConstructorUtils();
        testFieldUtils();

    }

    public static void testClassUtils() {
        //获取String.class的所有实现的接口
        System.out.println(ClassUtils.getAllInterfaces(String.class));
        // 获取String类所有父类
        System.out.println(ClassUtils.getAllSuperclasses(String.class));

        // 获取String类所在的包名
        System.out.println(ClassUtils.getPackageName(String.class));

        // 获取String类简单类名
        System.out.println(ClassUtils.getShortClassName(String.class));

        // 判断是否可以转型
        System.out.println("String to Object -> " + ClassUtils.isAssignable(String.class, Object.class));
        //这个不可以 因为父类不能直接赋值给子类 即String str = Object
        System.out.println("Object to String -> " + ClassUtils.isAssignable(Object.class, String.class));

        // 判断是否有内部类
        System.out.println("String是否有内部类：" + ClassUtils.isInnerClass(String.class));
        System.out.println("com.yiban.javaBase.dev.reflect.Demo1是否有内部类：" + ClassUtils.isInnerClass(Demo1.class));
    }

    /**
     * ConstructorUtils是关于构造函数的工具类
     * @throws InvocationTargetException
     * @throws NoSuchMethodException
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    public static void testConstructorUtils() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        // 获取参数为String的构造函数
        ConstructorUtils.getAccessibleConstructor(Thread.class, String.class);

        // 执行参数为String的构造函数
        Thread thread = (Thread) ConstructorUtils.invokeConstructor(Thread.class,
                "Hello");
    }

    /**
     * FieldUtils是关于字段和成员变量的工具类
     * 注意这里的Thread只是为了语法，如果要运行，需要另外选择一个类
     * @throws IllegalAccessException
     */
    public static void testFieldUtils() throws IllegalAccessException {
        Thread thread = new Thread("hello");

        // 以下两个方法完全一样,都能获取共有或私有变量,因为第三个参数都设置了不检查
        //getDeclaredField可以获取类本身的属性成员（包括private、public、protected）
        Field private_tid = FieldUtils.getDeclaredField(Thread.class, "tid", true);
        System.out.println("private_tid = " + private_tid.get(thread));
        //getField 仅能获取类(及其父类可) public属性成员
        Field private1_tid = FieldUtils.getField(Thread.class, "tid", true);
        System.out.println("private1_tid = " + private1_tid.get(thread));

        // 读取私有或公共变量的值
        System.out.println("tid = " + FieldUtils.readField(thread, "tid", true));

        // 读取静态变量(静态变量不能是private)
        System.out.println("MAX_PRIORITY = " + FieldUtils.readStaticField(Thread.class, "MAX_PRIORITY"));

        // 写入私有或共有变量
        FieldUtils.writeField(thread, "tid", 12, true);

        // 写入静态变量(静态变量不能是private，所以这里会报错)
        FieldUtils.writeStaticField(Thread.class, "threadInitNumber", 10);
    }
}
