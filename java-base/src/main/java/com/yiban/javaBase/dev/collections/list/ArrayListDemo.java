package com.yiban.javaBase.dev.collections.list;

import java.lang.reflect.Field;
import java.util.ArrayList;

/**
 * @auther WEI.DUAN
 * @date 2019/3/13
 * @website http://blog.csdn.net/dwshmilyss
 * ArrayList
 *  如果调用空的构造函数 初始容量是0 elementData 是一个空数组。只有在add了一个元素之后，elementData才会初始化为默认的10个长度
 *  如果传了参数  那么elementData的长度就是参数值
 *  当ArrayList的长度超过 elementData 的容量时，按 elementData 容量的1.5倍扩容
 */
public class ArrayListDemo {
    public static void main(String[] args) {
        ArrayList<String> list = new ArrayList<>(20);
        int length = getCapacity(list);
        int size = list.size();
        System.out.println("容量： " + length);
        System.out.println("大小： " + size);

        list.add("a");

        System.out.println("======================");
        int length1 = getCapacity(list);
        int size1 = list.size();
        System.out.println("容量： " + length1);
        System.out.println("大小： " + size1);

        for (int i = 0; i < 20; i++) {
            list.add("a");
            System.out.println("======================");
            int len = getCapacity(list);
            int s = list.size();
            System.out.println("容量： " + len);
            System.out.println("大小： " + s);
        }
    }

    /**
     * 获取ArrayList中Object[] elementData的长度
     * @return
     */
    public static int getCapacity(ArrayList list)  {
        Class clazz = list.getClass();
        int length = 0;
        Field field;
        try {
            field = clazz.getDeclaredField("elementData");
            field.setAccessible(true);
            Object[] objects = (Object[]) field.get(list);
            length = objects.length;
        } catch (Exception e){
            e.printStackTrace();
        }
        return length;
    }
}