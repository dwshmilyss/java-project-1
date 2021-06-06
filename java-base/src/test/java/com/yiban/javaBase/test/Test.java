package com.yiban.javaBase.test;


import com.alibaba.fastjson.JSONArray;
import com.yiban.javaBase.dev.concurrent.fork_join.SortTask;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.math3.exception.MathArithmeticException;
import org.intellij.lang.annotations.Language;
import org.junit.Assert;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import sun.misc.Unsafe;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.net.URI;
import java.net.URLConnection;
import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static junit.framework.TestCase.assertTrue;

/**
 * Created by duanwei on 2017/3/7.
 */
public class Test {
    private static final double SPLIT_SLOP = 1.1;   // 10% slop


    private static Unsafe theUnsafe;
    private static sun.misc.Unsafe UNSAFE;
    private static int a;
    private int b;

    //使用方法
    private static Unsafe getUnsafeInstance() throws SecurityException,
            NoSuchFieldException, IllegalArgumentException,
            IllegalAccessException {
        Field theUnsafeInstance = Unsafe.class.getDeclaredField("theUnsafe");
        theUnsafeInstance.setAccessible(true);
        return (Unsafe) theUnsafeInstance.get(Unsafe.class);

    }

    //    static
//    {
//        try {
    ////通过这样的方式获得Unsafe的实力会抛出异常信息，因为在unsafe的源码中会有对安全性的检查
//            UNSAFE = sun.misc.Unsafe.getUnsafe();
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//    }
    public static void main(String[] args) throws Exception {
        System.out.println(5 & 3);
        a = 1;
        a = 2;
        a = 3;
        a = 4;
        //for循环还可以这样写，b不用定义类型，应该是推断出来的
//        for (int a = 1,b = a;;){
//            System.out.println(b);
//        }

        Class<?> k = Test.class;
        System.out.println(getUnsafeInstance().objectFieldOffset(k.getDeclaredField("a")));

        Integer a = new Integer(0);
        Integer b = new Integer(1);
        System.out.println(a == b);
        System.out.println(a);
        System.out.println(a == (a = b));
        System.out.println(a);
//        System.out.println(UNSAFE.objectFieldOffset(k.getDeclaredField("a")));


        Calendar curDate = Calendar.getInstance();
        TimeZone timeZone = curDate.getTimeZone();
        System.out.println(timeZone.getID());
        System.out.println(timeZone.getDisplayName());
        Calendar tommorowDate = new GregorianCalendar(curDate.get(Calendar.YEAR),
                curDate.get(Calendar.MONTH),
                curDate.get(Calendar.DATE) + 1, 0, 0, 0);
        int val = (int) (tommorowDate.getTimeInMillis() - curDate.getTimeInMillis()) / 1000;
        System.out.println(val);

        System.out.println(curDate.getTimeInMillis());

    }

    @org.junit.Test
    public void test() {
        int temp = 50;
        loop:
        for (int i = 0; i < 100; i++) {
            System.out.println("i = " + i);
            for (int j = 0; j < 100; j++) {
                if (temp == j) {
                    break loop;
                }
                System.out.println("j = " + j);
            }
        }
    }

    @org.junit.Test
    public void run() throws InterruptedException {
        ForkJoinPool forkJoinPool = new ForkJoinPool();
        Random rnd = new Random();
        int SIZE = 10000;
        long[] array = new long[SIZE];
        for (int i = 0; i < SIZE; i++) {
            array[i] = rnd.nextInt();
        }
        forkJoinPool.submit(new SortTask(array));

        forkJoinPool.shutdown();
        forkJoinPool.awaitTermination(1000, TimeUnit.SECONDS);

        for (int i = 1; i < SIZE; i++) {
            assertTrue(array[i - 1] < array[i]);
        }
    }

    @org.junit.Test
    public void test1() {

//        System.out.println(Math.abs("console-consumer-90932".hashCode()) % 50);
        System.out.println("abc".charAt(0) + 1);
    }

    @org.junit.Test
    public void test2() {
        String str = "{\"aa\":\"bb\",\"cc\": \"dd\"}";
        String str1 = "select * from aa where id = 1 and uname = \"bbb\"";
        //language=HTML
        String str2 = "<html>\n" +
                " <a href=\"aaa\" content=\"bbb\"></a>\n" +
                "</html>";
        @Language("MySQL") String sql = "select * from  aaa";
        @Language("JSON") String json = "{\"name\": \"dw\",\"age\": 18}";
        if (true) {

        }
        System.out.println("aaa");
    }

    @org.junit.Test
    public void test3() {
        System.out.println(UUID.randomUUID().toString().replace("-", "").length());
    }

    @org.junit.Test
    public void test4() {
        Assert.assertEquals(1, 1);
        System.out.println("haha");
    }

    @org.junit.Test
    public void test5() {
//        for (Map.Entry entry : System.getenv().entrySet()) {
//            System.out.println("entry = " + entry);
//        }
        String regex = "[0-9]{4}";
        System.out.println(Pattern.matches(regex, "1122"));
    }

    @org.junit.Test
    public void test6() {
        AtomicInteger atomicInteger = new AtomicInteger(1);
        int flag = 100;
        for (int i = 0; i < 10; i++) {
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    System.out.println(Thread.currentThread().getName());
//                    for (int j = 0; j < 20; j++) {
//                        int uid = atomicInteger.incrementAndGet();
//                        System.out.println(Thread.currentThread().getName() + " = " + uid);
//                        if (uid < flag) {
//                        }else {
////                            synchronized (Test.class){
////
////                            }
//                        }
//                    }
                }
            };
            Thread thread = new Thread(runnable);
            thread.start();
        }
    }

    @org.junit.Test
    public void test7() {
        //这里的16进制代表的是ASCII码
        System.out.println(new String(new byte[]{0x61, 0x62}));

        URI uri = URI.create("http://www.baidu.com");
        URLConnection urlConnection = null;
        try {
            urlConnection = uri.toURL().openConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            InputStream inputStream = urlConnection.getInputStream();
            InputStreamReader isr = new InputStreamReader(inputStream);
            char[] c = new char[1024];
            while (isr.read(c) != -1) {
                System.out.print(c);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @org.junit.Test
    public void test8() {
        long time = 1554780426;
        String temp = "1011100101011000001000100001010";
        String res = addZeroForNum(temp, 36);
        System.out.println("res = " + res + ",len = " + res.length());
        System.out.println(1554780426);
        System.out.println(Long.parseLong(temp, 2));
        System.out.println(Long.parseLong(res, 2));
        System.out.println(temp.length());


        assert StringUtils.isEmpty("");
    }

    @org.junit.Test
    public void test9() {
        SortedMap<Integer, String> sortedMap = new TreeMap<Integer, String>();
        sortedMap.put(1, "a");
        sortedMap.put(2, "b");
        sortedMap.put(3, "c");
        sortedMap.put(4, "d");
        sortedMap.put(5, "e");

        SortedMap<Integer, String> subMap = sortedMap.tailMap(2);
        for (Map.Entry<Integer, String> entry : subMap.entrySet()) {
            System.out.println(entry.getKey() + " " + entry.getValue());
        }

        System.out.println("-------------------------");

        for (Iterator<Map.Entry<Integer, String>> iterator = subMap.entrySet().iterator(); iterator.hasNext(); ) {
            Map.Entry<Integer, String> entry = iterator.next();
            System.out.println(entry.getKey() + " " + entry.getValue());
        }
    }

    @org.junit.Test
    public void test10() {
        int i = -1, j = -2;
        j = i++;
        System.out.println("j = " + j + ",i = " + i);
//        for (; ; ) {
//            if (++i == 2) {
//                break;
//            }
//            System.out.println(i);
//        }
    }

    @org.junit.Test
    public void test11() {
        int i = 0;
        int j = 0;
        boolean a = true;
        boolean b;
        b = a & (0 < (i+=1));
        b = a && (0 < (i+=2));
        b = a | (0 < (j+=1));
        b = a || (0 < (j+=2));
        //这里输出i=3 j=1，i=3很好理解，j=1是因为||运算，因为前面的a已经是ture，根据if机制，那么后面的表达式就不执行了
        System.out.println("i = " + i + " j = " + j);
    }

    @org.junit.Test
    public void test12() {
        try {
            int age = 5 / 0;
        } catch (ArithmeticException e) {
            System.out.println("1");
        } catch (Exception e) {
            System.out.println(2);
        }
    }

    public static String addZeroForNum(String str, int strLength) {
        int strLen = str.length();
        if (strLen < strLength) {
            while (strLen < strLength) {
                StringBuffer sb = new StringBuffer();
                sb.append("0").append(str);// 左补0
                // sb.append(str).append("0");//右补0
                str = sb.toString();
                strLen = str.length();
            }
        }
        return str;
    }
}
