package com.yiban.javaBase.dev.syntax;

import java.util.regex.Pattern;

/**
 * @auther WEI.DUAN
 * @date 2018/10/30
 * @website http://blog.csdn.net/dwshmilyss
 */
public class RegexDemo {
    public static void main(String[] args) {
        String regex = "^\\{.*\\}$";
        String in = " {\"product\":\"ymm\",\"platform\":\"mobile\",\"module\":\"ymm\",\"action\":\"praise_article\",\"description\":\"\u6587\u7ae0\u70b9\u8d5e\",\"src_obj\":{\"uid\":21253208},\"dst_obj\":{\"articleId\":431952,\"authorId\":21253290},\"time\":1538323200} ";
        String in1 = "ition\":418}}";
        boolean flag = Pattern.matches(regex,in);
        System.out.println(flag);
    }
}
