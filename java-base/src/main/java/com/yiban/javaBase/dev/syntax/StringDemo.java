package com.yiban.javaBase.dev.syntax;

import org.apache.commons.lang3.StringUtils;
import org.intellij.lang.annotations.Language;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Arrays;

/**
 * string demo
 * String.intern() 如果常量池中没有 则放入常量池 并返回常量池中的地址
 * 注意 这个方法在jdk1.6和jdk1.7里是不一样的
 * 因为jdk1.2~1.6 常量池和方法区属于非堆 使用永久代实现的
 * 而jdk1.7开始，常量池被移到了java堆中 所以返回的地址会不同
 * jdk1.8 则被移动到了元空间
 *
 * @auther WEI.DUAN
 * @date 2017/11/15
 * @website http://blog.csdn.net/dwshmilyss
 */
public class StringDemo implements Serializable {


    /**
     * slf4j logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(StringDemo.class);
    private static final long serialVersionUID = -3537307121344283322L;

    public StringDemo() {
    }

    public static void main(String[] args) {
        String s = new String("1");
        s = s.intern();
        String s2 = "1";
        System.out.println(s == s2);

        String s1 = new String(new byte[10], Charset.forName("utf-8"));
        System.out.println("s1 = " + s1);

        String s3 = "abc";
        CharSequence cs = s3.subSequence(1,2);
        System.out.println(cs.getClass());
        if (s1 == null) {

        }
        if (s1 != null) {

        }

        @Language("JSON") String s4 = "{\"NAME\": \"DW\"}";
        @Language("JSON") String s5 = "";
    }

    /**
     * 测试String.intern()
     */
    public static void test1() {
        //new String 只在堆中生成对象数据，不会放入常量池，所以两个地址明显不同（一个是对象地址，一个是常量池地址），返回false
        //这里如果没有调用append()也是返回false 可能append就会把字符串放入常量池
        //如果是java关键字("java"和"java1"返回结果就不同) 返回false  因为默认在常量池中已经存在。所以调用append的时候其实并没有放入常量池，还是返回的对象的地址
        String str1 = new StringBuilder("ja").append("va").toString();
        //这里会放入常量池，因为都是常量池地址 所以返回true。如果是jdk1.6 因为
//        String str1 = "bbb";
        System.out.println(str1.intern() == str1);
    }


    public static void test2(String a, String b) {
        LOGGER.info(" " + "a = [" + a + "], b = [" + b + "]");
    }


}
