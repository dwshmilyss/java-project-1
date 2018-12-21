package com.yiban.javaBase.dev.collections.map;

import io.netty.util.collection.IntObjectHashMap;
import io.netty.util.collection.IntObjectMap;

/**
 * @auther WEI.DUAN
 * @date 2018/12/19
 * @website http://blog.csdn.net/dwshmilyss
 */
public class IntObjectHashMapDemo {
    public static void main(String[] args) {
        IntObjectMap<String> intObjectMap = new IntObjectHashMap<>(100);
        intObjectMap.put(1,"aa");
        intObjectMap.put(2,"bb");
        intObjectMap.put(3,"cc");

        for (int key : intObjectMap.keySet()) {
            System.out.println(key + " : " + intObjectMap.get(key));
        }
    }
}