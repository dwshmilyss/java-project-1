package com.yiban.javaBase.dev.collections.map;

import java.util.*;

/**
 * 红黑树:
 * 1、每个节点都只能是红色或者黑色
 * 2、根节点是黑色
 * 3、每个叶节点（NIL节点，空节点）是黑色的。
 * 4、如果一个结点是红的，则它两个子节点都是黑的。也就是说在一条路径上不能出现相邻的两个红色结点。
 * 5、从任一节点到其每个叶子的所有路径都包含相同数目的黑色节点。
 *
 * @auther WEI.DUAN
 * @create 2017/4/23
 * @blog http://blog.csdn.net/dwshmilyss
 */
public class TreeMapDemo {
    public static void main(String[] args) {
//        testSortByKey();
        testSortByValue();
    }

    /**
     * Map结构按key排序（TreeMap）
     */
    private static void testSortByKey() {
        Map<String, String> treeMap = new TreeMap();
        treeMap.put("b", "b1");
        treeMap.put("a", "a1");
        treeMap.put("d", "d1");
        treeMap.put("c", "c1");
        System.out.println("=========排序======== ");
        for (String key : treeMap.keySet()) {
            System.out.println(key + " = " + treeMap.get(key));
        }

        //这里如果要比较key 传入自定义的比较器即可
        //如果比较string 这里是多此一举  因为string本身就实现了Comparable
        Map<String, String> map = new TreeMap<String, String>(new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return o1.compareToIgnoreCase(o2);
            }
        });

        System.out.println("=========排序======== ");
        map.put("KFC", "kfc");
        map.put("WNBA", "wnba");
        map.put("NBA", "nba");
        map.put("CBA", "cba");
        for (String key : map.keySet()) {
            System.out.println(key + " = " + map.get(key));
        }
    }


    private static void testSortByValue() {
        //按value排序
        Map<String,Integer> hashMap = new HashMap<>();
        hashMap.put("b",2);
        hashMap.put("a",1);
        hashMap.put("d",4);
        hashMap.put("e",4);
        hashMap.put("c",3);

        //把数据放到一个ArrayList里面
        List<Map.Entry<String,Integer>> entryList = new ArrayList<>(hashMap.entrySet());
        Collections.sort(entryList, new Comparator<Map.Entry<String, Integer>>() {
            @Override
            public int compare(Map.Entry<String, Integer> entry1, Map.Entry<String, Integer> entry2) {
                return entry1.getValue().compareTo(entry2.getValue());
            }
        });
        //创建另外一个map来保存排序后的数据
        Map<String,Integer> tempMap = new HashMap<>();
        for (Iterator<Map.Entry<String, Integer>> iter = entryList.iterator();iter.hasNext();){
            Map.Entry<String, Integer> entry = iter.next();
            tempMap.put(entry.getKey(), entry.getValue());
        }
        System.out.println("================ ");
        for (String key : tempMap.keySet()) {
            System.out.println(key + " = " + tempMap.get(key));
        }
        //或者直接输出，不需要再用另一个map来保存
        for (Map.Entry<String, Integer> e : entryList) {
            System.out.println(e.getKey() + ":" + e.getValue());
        }
    }
}
