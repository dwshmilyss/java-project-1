package com.yiban.javaBase.dev.collections.array;

import org.apache.commons.lang3.ArrayUtils;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Map;

/**
 * array demo
 *
 * @auther WEI.DUAN
 * @date 2017/10/23
 * @website http://blog.csdn.net/dwshmilyss
 */
public class ArrayDemo {
    // Test routine for resizeArray().
    public static void main(String[] args) {
        int[] a = {1, 2, 3};
        a = (int[]) resizeArray(a, 5);
        a[3] = 4;
        a[4] = 5;
        for (int i = 0; i < a.length; i++) {
            System.out.println(a[i]);
        }
        arrayToMap();
    }

    /**
     * java API中数组的大小是不能改变的。但是利用反射可以改变数组的大小
     * 本质上也不是改变原数组，只是生成一个新数组，把原数组中的元素拷贝过来
     * Reallocates an array with a new size, and copies the contents
     * of the old array to the new array.
     *
     * @param oldArray the old array, to be reallocated.
     * @param newSize  the new array size.
     * @return A new array with the same contents.
     */
    private static Object resizeArray(Object oldArray, int newSize) {
        int oldSize = java.lang.reflect.Array.getLength(oldArray);
        Class elementType = oldArray.getClass().getComponentType();
        Object newArray = java.lang.reflect.Array.newInstance(
                elementType, newSize);
        int preserveLength = Math.min(oldSize, newSize);
        if (preserveLength > 0){
            System.arraycopy(oldArray, 0, newArray, 0, preserveLength);
        }
        System.out.println(oldArray == newArray);
        return newArray;
    }

    public static void arrayToMap() {
        String[][] countries = {{"United States", "New York"}, {"United Kingdom", "London"},
                {"Netherland", "Amsterdam"}, {"Japan", "Tokyo"}, {"France", "Paris"}};

        Map countryCapitals = ArrayUtils.toMap(countries);

        System.out.println("Capital of Japan is " + countryCapitals.get("Japan"));
        System.out.println("Capital of France is " + countryCapitals.get("France"));

        ArrayList list = new ArrayList();
        list.get(0);
        LinkedList linkedList = new LinkedList();
        linkedList.get(10);
    }
}


