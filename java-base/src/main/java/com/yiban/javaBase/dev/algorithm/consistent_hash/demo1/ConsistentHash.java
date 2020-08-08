package com.yiban.javaBase.dev.algorithm.consistent_hash.demo1;

import java.util.*;

public class ConsistentHash<T> {
    /**
     * 节点的复制因子，虚拟节点个数 = 实际节点个数*numberOfReplaces
     * 虚拟节点的生成算法就是：原节点.toString()+i (i是从0到numberOfReplaces-1)
     */
    private int numberOfReplaces;
    /**
     * 映射的圆 大小为2^32
     */
    private SortedMap<Long, T> circle = new TreeMap<>();

    public ConsistentHash(int numberOfReplaces, Collection<T> nodes) {
        this.numberOfReplaces = numberOfReplaces;
        for (T node : nodes) {
            add(node);
        }
    }

    public static void main(String[] args) {
        //待添加入Hash环的服务器列表
        String[] servers = {"192.168.0.0:111", "192.168.0.1:111", "192.168.0.2:111",
                "192.168.0.3:111", "192.168.0.4:111"};

        ConsistentHash<String> consistentHash = new ConsistentHash<String>(2, Arrays.asList(servers));

        consistentHash.add("192.168.0.5:111");
        System.out.println("hash circle size : " + consistentHash.getSize());

        consistentHash.testBalance();

        System.out.println("key = test1,hash = " +HashUtils.hash_MD5("test1") + ",node = " + consistentHash.get("test1"));
        System.out.println("key = test2,hash = " +HashUtils.hash_MD5("test2") + ",node = " + consistentHash.get("test2"));
        System.out.println("key = test3,hash = " +HashUtils.hash_MD5("test3") + ",node = " + consistentHash.get("test3"));
        System.out.println("key = test4,hash = " +HashUtils.hash_MD5("test4") + ",node = " + consistentHash.get("test4"));
        System.out.println("key = test5,hash = " +HashUtils.hash_MD5("test5") + ",node = " + consistentHash.get("test5"));
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
            String virtualNodeName = node.toString() + "&&VN" + String.valueOf(i);
            circle.put(HashUtils.hash_MD5(virtualNodeName), node);
        }
    }

    /**
     * 移除一个机器节点
     *
     * @param node
     */
    public void remove(T node) {
        for (int i = 0; i < numberOfReplaces; i++) {
            circle.remove(HashUtils.hash_MD5(node.toString() + i));
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
        //获取key的hash
        long hash = HashUtils.hash_MD5(key.toString());
        //如果圆中没有找到该key的hash对应的机器节点
        if (!circle.containsKey(hash)) {
            /**
             * 顺时针查找
             * tailMap(fromKey) 返回其键大于或等于fromKey的部分视图
             */
            SortedMap<Long, T> tailMap = circle.tailMap(hash);
            //firstKey返回第一个key，此处正好对应顺时针，即对应视图中的第一个Key
            /**
             * 如果有大于该key的hash值的子map，那么返回子map的第一个节点
             * 如果没有子map，所以返回原map的第一个节点，这样就能形成一个圆。
             */
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
        System.out.println("=============直接获取的hash=============");
        for (Long hash: sets) {
            System.out.println("key = " + hash + ",value = " + circle.get(hash));
        }
        SortedSet<Long> sortedSet = new TreeSet<>(sets);//对key进行排序
        System.out.println("===========排序后的==============");
        for (Long hashCode : sortedSet) {
            System.out.println("key = " + hashCode + ",value = " + circle.get(hashCode));
        }
        System.out.println("-- 相邻两个hashCode的差 ----");
        /**
         * MD5算法生成的相邻的两个hashCode的差值
         */
        Iterator<Long> it1 = sortedSet.iterator();
        Iterator<Long> it2 = sortedSet.iterator();
        if (it2.hasNext())
            it2.next();
        long keyPre, keyAfter;
        while (it1.hasNext() && it2.hasNext()) {
            keyPre = it1.next();
            keyAfter = it2.next();
            System.out.println("subtraction = " + (keyAfter - keyPre));
        }
    }
}
