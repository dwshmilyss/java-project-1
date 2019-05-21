package com.yiban.javaBase.dev.sort;

import java.util.*;

/**
 * treemap
 *
 * @auther WEI.DUAN
 * @date 2018/1/9
 * @website http://blog.csdn.net/dwshmilyss
 */
public class TreemapDemo {
    public static void sortByValue() {
        Map<String, Integer> map = new TreeMap<>();
        map.put("a", 40);
        map.put("d", 60);
        map.put("b", 55);
        map.put("c", 43);

        List<Map.Entry<String, Integer>> list = new ArrayList<Map.Entry<String, Integer>>(map.entrySet());

        Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
            //升序排序
            @Override
            public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
                return o2.getValue().compareTo(o1.getValue());
            }
        });

        for (Map.Entry<String, Integer> e : list) {
            System.out.println(e.getKey() + ":" + e.getValue());
        }
    }

    public static void main(String[] args) {
        sortByValue();
    }
}
