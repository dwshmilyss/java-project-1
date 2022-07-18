package com.yiban.javaBase.dev.collections.map;

import sun.security.util.BitArray;

import java.util.Map;
import java.util.WeakHashMap;

/**
 * 用短时间内就过期的缓存时最好使用weakHashMap，它包含了一个自动调用的方法expungeStaleEntries，这样就会在值被引用后直接执行这个隐含的方法，将不用的键清除掉。
 *
 * @auther WEI.DUAN
 * @create 2017/4/23
 * @blog http://blog.csdn.net/dwshmilyss
 */
public class WeakHashMapTest {
    static Map wMap = new WeakHashMap();

    public static void init() {
        wMap.put("1", "ding");
        wMap.put("2", "job");
    }

    public static void testWeakHashMap() {
        System.out.println("first get:" + wMap.get("1"));
        try {
            Thread.sleep(20000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("next get:" + wMap.get("1"));
    }

    public static void main(String[] args) {
//        init();
//        testWeakHashMap();

        BitArray bitArray = new BitArray(10);
        for (int i = 0; i < 10; i++) {
            System.out.println(bitArray.get(i));
            bitArray.set(i, true);
        }
        System.out.println(" ------------------- ");
        for (int i = 0; i < 10; i++) {
            System.out.println(bitArray.get(i));
        }
    }
}
