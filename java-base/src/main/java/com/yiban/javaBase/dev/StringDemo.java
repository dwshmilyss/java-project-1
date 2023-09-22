package com.yiban.javaBase.dev;

import scala.reflect.internal.Trees;

import java.nio.file.Path;
import java.nio.file.Paths;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;

/**
 * @Description: jclasslib插件的使用：菜单栏view -> show bytecode with jclasslib
 * @Date: 2023/8/28
 * @Auther: David.duan
 * @Param null:
 **/
public class StringDemo {
    private int d = 0;
    public static final int c = 15;

    /**
     * @Description: TODO
     * @Date: 2023/8/28
     * @Auther: David.duan
     *
     * 0 aload_0  把局部变量表中的this加载到操作数栈
     * 1 iload_1
     * 2 iload_2
     * 3 iadd
     * 4 putfield #2 <com/yiban/javaBase/dev/StringDemo.d : I>
     * 7 return
     **/
    public void add(int a, int b) {
        d = a + b;
    }

    /**
     * @Description: TODO
     * @Date: 2023/8/28
     * @Auther: David.duan
    0 iconst_4
    1 istore_1
    2 iconst_3
    3 istore_1
    4 bipush 8
    6 istore_2
    7 iload_1
    8 iload_2
    9 iadd
    10 istore_3
    11 return
     **/
    public void test() {
        int i = 15;
//        i = 3;
        int j = 8;
//        int k = c + j;
        int k = i + j;
    }
}
