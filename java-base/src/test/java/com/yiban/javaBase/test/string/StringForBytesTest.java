package com.yiban.javaBase.test.string;

import java.nio.charset.Charset;

/**
 * @auther WEI.DUAN
 * @date 2019/2/28
 * @website http://blog.csdn.net/dwshmilyss
 */
public class StringForBytesTest {
    @org.junit.Test
    public void test() throws Exception {
        String str = "钓鱼岛是中国的";
        String str1 = new String(str);
        String str_UTF_8 = new String(str.getBytes(),"UTF-8");
        String str_UTF = new String(str.getBytes(),Charset.forName("UTF-8"));
        String str_unicode = new String(str.getBytes(), Charset.forName("unicode"));
        String str_GBK = new String(str.getBytes(),"GBK");
        String str_GB2312 = new String(str.getBytes(),"GB2312");
        String str_GB18030 = new String(str.getBytes(),"GB18030");
        System.out.println("str1 = " + str1.getBytes().length);
        System.out.println("str_UTF_8 = " + str_UTF_8.getBytes().length);
        System.out.println("str_UTF = " + str_UTF.getBytes().length);
        System.out.println("str_GBK = " + str_GBK.getBytes().length);
        System.out.println("str_GB2312 = " + str_GB2312.getBytes().length);
        System.out.println("str_GB18030 = " + str_GB18030.getBytes().length);
        System.out.println("str_unicode = " + str_unicode.getBytes().length);
    }
}