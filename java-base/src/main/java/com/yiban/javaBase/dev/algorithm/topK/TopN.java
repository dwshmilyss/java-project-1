package com.yiban.javaBase.dev.algorithm.topK;

import java.util.*;

/**
 * 后台需要实现对于字段top K排序。需要同时考虑效率和利用成熟代码，尽快实现，
 * 首先将<字段名,字段出现的次数>作为K-V存到map中，然后需要对map进行统计找到出现最多的几个字段。后台需要实现对于字段top K排序。需要同时考虑效率和利用成熟代码，尽快实现，首先将字段名，
 * 字段出现的次数作为K-V存到map中，然后需要对map进行统计找到出现最多的几个字段。
 * 后台需要实现对于字段top K排序。需要同时考虑效率和利用成熟代码，尽快实现，
 * 首先将字段名，字段出现的次数作为K-V存到map中，
 * 然后需要对map进行统计找到出现最多的几个字段。
 * 采用平衡二叉树对map中出现的次数进行统计，我们是java平台，
 * 因此考虑采用成熟的代码直接实现了平衡二叉树。首先想到的就是TreeSet。
 * <p/>
 * 其实这个有更直接的办法 就是使用TreeMap 上面的方法有点垃圾
 *
 * @auther WEI.DUAN
 * @date 2018/1/9
 * @website http://blog.csdn.net/dwshmilyss
 */
public class TopN {
    int m_size;
    TreeSet<TopData> m_topSet = new TreeSet<>();
    Map<String, Integer> m_topMap = new HashMap<>();

    public TopN(int m_size) {
        this.m_size = m_size;
    }

    public static void main(String[] args) {
        int minCount = 0xFFFFFFFF;
        System.out.println(minCount);
        List<String> list = new ArrayList<>();
        list.add("abc");
        list.add("abc");
        list.add("abc");
        list.add("abd");
        list.add("abd");
        list.add("abd");
        list.add("abd");
        list.add("abe");
        list.add("abe");

        TopN topN = new TopN(3);
        for (int i = 0; i < list.size(); i++) {
            topN.insertString(list.get(i));
        }

        Set<TopData> set = topN.getResult();
        Iterator<TopData> it = set.iterator();

        while (it.hasNext()) {
            TopData topData = it.next();
            System.out.println(topData.getName() + " " + topData.getCount());
        }
    }

    /**
     * 把要统计的字符串都放到一个Map里
     *
     * @param str
     */
    public void insertString(String str) {
        if (m_topMap.containsKey(str)) {
            m_topMap.put(str, m_topMap.get(str) + 1);
        } else {
            m_topMap.put(str, 1);
        }
    }

    /**
     * 将map中的数据top统计到TreeSet中
     */
    protected void mapToSet() {
        Iterator<Map.Entry<String, Integer>> iterator = m_topMap.entrySet().iterator();
        int temp;
        int minCount = 0xFFFFFFFF;//-1
        while (iterator.hasNext()) {
            Map.Entry<String, Integer> entry = iterator.next();
            temp = entry.getValue();
            if (minCount == 0xFFFFFFFF) {//第一次运行
                minCount = temp;
            }
            //首先填满m_topSet
            if (m_topSet.size() < m_size) {
                m_topSet.add(new TopData(entry.getKey(), entry.getValue()));
                if (temp < minCount) {
                    minCount = temp;//更新最低次数
                }
            } else if (temp > minCount) {// m_topSet已经填满，并且该次数比最低次数高
                m_topSet.remove(m_topSet.last());//先删除m_topSet中的最低次数的实例
                m_topSet.add(new TopData(entry.getKey(), entry.getValue()));
                minCount = m_topSet.last().getCount();//更新最低次数
            }
        }
    }

    //获取排序结果，结果是递减的
    public Set<TopData> getResult() {
        mapToSet();
        return m_topSet;
    }

}

class TopData implements Comparable<TopData> {
    private String name;//字段名
    private int count;//字段出现的次数

    public TopData(String name, int count) {
        this.name = name;
        this.count = count;
    }

    public String getName() {

        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCount() {
        return this.count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    @Override
    public int compareTo(TopData o) {
        return o.getCount() - this.getCount();
    }
}
