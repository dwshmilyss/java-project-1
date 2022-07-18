package com.yiban.javaBase.dev.collections.list;

import java.util.LinkedList;

/**
 * demo
 *
 * @auther WEI.DUAN
 * @date 2019/10/10
 * @website http://blog.csdn.net/dwshmilyss
 */
public class LinkedListDemo {
    public static void main(String[] args) {
        LinkedList<String> list = new LinkedList<String> ();
        list.add(0, "a");
        list.add(1, "b");
        list.add(2, "c");
        System.out.println("list = " + list.size());
        System.out.println(list.get(1));
    }
}