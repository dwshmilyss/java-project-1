package com.yiban.javaBase.dev.annotations.example_1;

import java.lang.reflect.Method;

/**
 * @author david.duan
 * @packageName com.yiban.javaBase.dev.annotations.example_1
 * @className MyAnnotationTest
 * @date 2024/9/27
 * @description
 */
public class MyAnnotationTest {
    public static void main(String[] args) throws NoSuchMethodException {
        Method method = MyClass.class.getMethod("myMethod");
        if (method.isAnnotationPresent(MyAnnotation.class)) {
            MyAnnotation myAnnotation = method.getAnnotation(MyAnnotation.class);
            System.out.println("Annotation value: " + myAnnotation.value());
        }
    }
}
