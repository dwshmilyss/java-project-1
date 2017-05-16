package com.yiban.javaBase.dev.collections.map;

import java.util.Map;
import java.util.TreeMap;

/**
 * 红黑树:
 *   1、每个节点都只能是红色或者黑色
 *   2、根节点是黑色
 *   3、每个叶节点（NIL节点，空节点）是黑色的。
 *   4、如果一个结点是红的，则它两个子节点都是黑的。也就是说在一条路径上不能出现相邻的两个红色结点。
 *   5、从任一节点到其每个叶子的所有路径都包含相同数目的黑色节点。
 * @auther WEI.DUAN
 * @create 2017/4/23
 * @blog http://blog.csdn.net/dwshmilyss
 */
public class TreeMapTest {
    public static void main(String[] args){
        Map<String,String> treeMap = new TreeMap();
        treeMap.put("b","b1");
        treeMap.put("a","a1");
        treeMap.put("d","d1");
        treeMap.put("c","c1");

        for (String key:
             treeMap.keySet()) {
            System.out.println(treeMap.get(key));
        }

//        Map<String,String> sortedMap = new SortedMap<String, String>() {
//        }

    }  
}
