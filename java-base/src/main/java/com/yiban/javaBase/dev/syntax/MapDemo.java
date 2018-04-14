package com.yiban.javaBase.dev.syntax;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * map demo
 *
 * @auther WEI.DUAN
 * @date 2017/11/16
 * @website http://blog.csdn.net/dwshmilyss
 */
public class MapDemo {


    /**
     * HashMap 初始化默认16(必须是2的整数倍)，每次resize就double长度(增加一倍的capacity)
     */
    static final int MAXIMUM_CAPACITY = 1 << 30;
    /**
     * Logger
     */
    private static final Logger logger = LoggerFactory.getLogger(MapDemo.class);
    public static Map<Integer, Integer> map = new HashMap<>();

    static {
        map.put(2, 20);
        map.put(1, 10);
        map.put(3, 30);
    }

    public static void main(String[] args) {
        System.out.println(tableSizeFor(16));
    }

    static final int tableSizeFor(int cap) {
        int n = cap - 1;
        n |= n >>> 1;
        n |= n >>> 2;
        n |= n >>> 4;
        n |= n >>> 8;
        n |= n >>> 16;
        return (n < 0) ? 1 : (n >= MAXIMUM_CAPACITY) ? MAXIMUM_CAPACITY : n + 1;
    }

    /**
     * Map接口提供了三种collection，每一种都可以转成List:
     * 1,key set
     * 2,valule set
     * 3,key-value set
     */
    public static void test1() {
        //key set to list
        List<Integer> keyList = new ArrayList<>(map.keySet());
        //value set to list
        List<Integer> valueList = new ArrayList<>(map.values());
        //key-value to list
        List<Map.Entry<Integer, Integer>> kvList = new ArrayList<>(map.entrySet());
    }

    /**
     * 效率最高的迭代
     */
    public static void test2() {
        //1 foreach
        for (Map.Entry entry : map.entrySet()) {
            //TODO
            int key = (int) entry.getKey();
            int value = (int) entry.getValue();
        }
        //2 iterator
        Iterator itr = map.entrySet().iterator();
        while (itr.hasNext()) {
            Map.Entry entry = (Map.Entry) itr.next();
            int key = (int) entry.getKey();
            int value = (int) entry.getValue();
        }
    }

    /**
     * 按key排序
     */
    public static void test3() {
        //按key排序，把getKey换成getValue就可以按value排序
        ArrayList<Map.Entry<Integer, Integer>> list = new ArrayList<>(map.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<Integer, Integer>>() {
            @Override
            public int compare(Map.Entry<Integer, Integer> o1, Map.Entry<Integer, Integer> o2) {
                return o1.getKey().compareTo(o2.getKey());
            }
        });

        //也可以用TreeMap
        SortedMap<Integer, Integer> sortedMap = new TreeMap<>(new Comparator<Integer>() {
            @Override
            public int compare(Integer k1, Integer k2) {
                return k1.compareTo(k2);
            }
        });
        sortedMap.putAll(map);
    }

    /**
     * 不可变的map
     */
    public static void test4() {
        //correct
        Map<Integer, Integer> map1 = new HashMap<>();
        map1.put(8, 9);
        map1.put(88, 99);
        map1 = Collections.unmodifiableMap(map1);

        //wrong（加了final只是不能再new 但是还是可以修改其中的元素的）
        final Map<Integer, Integer> map2 = new HashMap<>();
        map1.put(8, 9);
        map1.put(88, 99);

        //创建一个空的不可变的map
        Map map3 = Collections.emptyMap();
    }
}
