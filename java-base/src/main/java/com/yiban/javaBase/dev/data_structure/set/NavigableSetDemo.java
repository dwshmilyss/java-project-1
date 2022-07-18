package com.yiban.javaBase.dev.data_structure.set;

import java.util.NavigableSet;
import java.util.TreeSet;

public class NavigableSetDemo {
    public static void main(String[] args) {
        //TreeSet实现了NavigableSet接口
        NavigableSet<String> sortedTreeSet = new TreeSet<>();
        sortedTreeSet.add("aa");
        sortedTreeSet.add("bb");
        sortedTreeSet.add("cc");
        sortedTreeSet.add("dd");
        sortedTreeSet.add("ee");

        System.out.println("找到大于等于cc的最小值 : " + sortedTreeSet.ceiling("cc"));
        System.out.println("返回大于cc的最小值 = " + sortedTreeSet.higher("cc"));
        System.out.println("------------------------------");

        System.out.println("返回小于等于cc的最大值 = " + sortedTreeSet.floor("cc"));
        System.out.println("返回小于cc的最大值 = " + sortedTreeSet.lower("cc"));
        System.out.println("------------------------------");

        System.out.println("返回Set的逆序 : " + sortedTreeSet.descendingSet());
        System.out.println("------------------------------");

        System.out.println("返回小于cc的元素 = " + sortedTreeSet.headSet("cc"));
        System.out.println("返回小于等于cc的元素 = " + sortedTreeSet.headSet("cc", true));
        System.out.println("------------------------------");

        System.out.println("移除第一个元素 = " + sortedTreeSet.pollFirst());
        System.out.println("移除最后一个元素 = " + sortedTreeSet.pollLast());
        System.out.println("sortedTreeSet = " + sortedTreeSet);
        System.out.println("------------------------------");

        /**
         * 注意 subSet()方法最终是由TreeMap的subMap()实现的
         * 该方法调用如下：
         *               return new AscendingSubMap<>(this,
         *                                      fromStart:false, fromKey, fromInclusive,
         *                                      toEnd:false, toKey,   toInclusive);
         *  注意fromStart和toEnd两个参数，都是false，所以如果要截取的区间参数是第一个和最后一个
         *  那么即使都把后面的布尔参数设为true，也是不包含的。
         */
        System.out.println("返回某个区间(bb,dd)的元素 = " + sortedTreeSet.subSet("bb","dd"));//默认是("bb",true,"dd",false)
        //等价于sortedTreeSet.subSet("bb","dd") 该方法的默认参数就是subSet(fromElement, true, toElement, false).
        System.out.println("返回某个区间(bb,dd]的元素 = " + sortedTreeSet.subSet("bb",true,"dd",false));
        System.out.println("返回某个区间(bb,dd]的元素 = " + sortedTreeSet.subSet("bb",false,"dd",true));
        System.out.println("返回某个区间(bb,dd)的元素 = " + sortedTreeSet.subSet("bb",true,"dd",true));
        System.out.println("返回某个区间(bb,dd)的元素 = " + sortedTreeSet.subSet("bb", false, "dd", false));
        System.out.println("------------------------------");

        System.out.println("返回元素大于cc的元素视图,包括cc = " + sortedTreeSet.tailSet("cc"));//返回元素大于cc的元素视图,包括cc:[cc,dd]
        System.out.println("返回元素大于等于cc的元素视图，不包括cc = " + sortedTreeSet.tailSet("cc", false));//返回元素大于等于cc的元素视图,不包括cc:[dd]

        System.out.println("------------------------------");
        System.out.println("返回一个升序的迭代器 = " + sortedTreeSet.iterator());//返回set上的升序排序的迭代器
        System.out.println("返回一个降序的迭代器 = " + sortedTreeSet.descendingIterator());//返回set上的降序排序的迭代器
    }
}
