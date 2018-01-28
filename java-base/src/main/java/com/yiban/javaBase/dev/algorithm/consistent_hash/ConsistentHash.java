package com.yiban.javaBase.dev.algorithm.consistent_hash;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class ConsistentHash<T> {
    private int numberOfReplaces;//节点的复制因子，虚拟节点个数 = 实际节点个数*numberOfReplaces
    private SortedMap<Long, T> circle = new TreeMap<>();

    public ConsistentHash(int numberOfReplaces, Collection<T> nodes) {
        this.numberOfReplaces = numberOfReplaces;
        for (T node : nodes) {
            add(node);
        }
    }

    /**
     * 添加一个机器节点
     *
     * @param node
     */
    public void add(T node) {
        for (int i = 0; i < numberOfReplaces; i++) {
            /**
             * 对于一个实际的机器节点，对应numberOfReplaces个虚拟节点
             * 不同的虚拟节点有不同的hash值，但都对应同一个实际的机器节点
             * 虚拟节点一般是均匀分布在环上，数据顺时针存在离自己最近的那个虚拟节点上
             */
            circle.put(HashUtils.hash(node.toString() + i), node);
        }
    }

    /**
     * 移除一个机器节点
     *
     * @param node
     */
    public void remove(T node) {
        for (int i = 0; i < numberOfReplaces; i++) {
            circle.remove(HashUtils.hash(node.toString() + i));
        }
    }

    /**
     * 根据key获取一个机器节点
     *
     * @param key
     * @return
     */
    public T get(Object key) {
        if (circle.isEmpty()) {
            return null;
        }
        //获取机器节点对应的hash码
        long hash = HashUtils.hash(key.toString());
        if (!circle.containsKey(hash)) {
            /**
             * tailMap(fromKey) 返回其键大于或等于fromKey的部分视图
             * 这里如果找不到hash对应的key，那就返回一个大于等于该hash的数据视图
             */
            SortedMap<Long, T> tailMap = circle.tailMap(hash);
            //firstKey返回第一个key，此处正好对应顺时针，即对应视图中的第一个Key
            hash = tailMap.isEmpty() ? circle.firstKey() : tailMap.firstKey();
        }
        return circle.get(hash);
    }

    /**
     * 获取一共有多少虚拟节点
     *
     * @return
     */
    public int getSize() {
        return circle.size();
    }

    public void testBalance() {
        Set<Long> sets = circle.keySet();//获取环中所有虚拟节点所有的Key
        SortedSet<Long> sortedSet = new TreeSet<>(sets);//对key进行排序
        for (Long hashCode : sortedSet) {
            System.out.println("hashCode = " + hashCode);
        }
        System.out.println("-- 相邻两个hashCode的差 ----");
        /**
         * MD5算法生成的相邻的两个hashCode的差值
         */
        Iterator<Long> it1 = sortedSet.iterator();
        Iterator<Long> it2 = sortedSet.iterator();
        if (it2.hasNext())
            it2.next();
        long keyPre,keyAfter;
        while (it1.hasNext() && it2.hasNext()){
            keyPre = it1.next();
            keyAfter = it2.next();
            System.out.println("subtraction = "+(keyAfter - keyPre));
        }
    }

    public static void main(String[] args) {
        Set<String> nodes = new HashSet<>();
        nodes.add("A");
        nodes.add("B");
        nodes.add("C");

        ConsistentHash<String> consistentHash = new ConsistentHash<String>(2,nodes);

        consistentHash.add("D");
        System.out.println("hash circle size : " + consistentHash.getSize());
//        System.out.println("location of each node are follows : ");
//        consistentHash.testBalance();
        /**
         * hash circle size : 8
         D
         D
         D
         B
         B
         */
        System.out.println(consistentHash.get("test1"));
        System.out.println(consistentHash.get("test2"));
        System.out.println(consistentHash.get("test3"));
        System.out.println(consistentHash.get("test4"));
        System.out.println(consistentHash.get("test5"));
    }
}
