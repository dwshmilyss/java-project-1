package com.yiban.javaBase.dev.algorithm.consistent_hash.demo1;

import java.util.SortedMap;
import java.util.TreeMap;

/**
 * 不带虚拟节点的
 * Created by Administrator on 2018/1/28 0028.
 */
public class ConsistentHashingWithoutVirtualNode {
    //待添加入Hash环的服务器列表
    private static String[] servers = {"192.168.0.0:111", "192.168.0.1:111",
            "192.168.0.2:111", "192.168.0.3:111", "192.168.0.4:111"};

    //key表示服务器的hash值，value表示服务器的IP地址
    private static SortedMap<Integer, String> sortedMap = new TreeMap<Integer, String>();

    //程序初始化，将所有的服务器放入sortedMap中
    static {
        for (int i = 0; i < servers.length; i++) {
            //对每个服务器进行hash，获取hash值
            int hash = HashUtils.hash_FNV1_32(servers[i]);
            System.out.println("服务器:" + servers[i] + "的hash值为:" + hash);
            sortedMap.put(hash, servers[i]);
        }
    }

    //给定一个Key，计算应当路由到的服务器结点，因为按顺时针获取，所以应该获取到的服务器节点的hash值都要比该key的hash值大
    private static String getServer(String key) {
        //得到该key的hash值
        int hash = HashUtils.hash_FNV1_32(key);
        //得到大于该Hash值的所有server
        SortedMap<Integer, String> subMap = sortedMap.tailMap(hash);
        if (subMap.isEmpty()) {
            //如果没有比该key的hash值大的，则从第一个node开始。这样可以虚拟成一个圆圈。
            Integer i = sortedMap.firstKey();
            //返回对应的服务器
            return sortedMap.get(i);
        } else {
            //第一个node就是顺时针过去离key的hash值最近的那个服务器(即集合中大于hash(key)的最小的那个服务器)
            Integer i = subMap.firstKey();
            //返回对应的服务器
            return subMap.get(i);
        }
    }

    /**
     * 服务器:192.168.0.0:111的hash值为:575774686
     * 服务器:192.168.0.1:111的hash值为:8518713
     * 服务器:192.168.0.2:111的hash值为:1361847097
     * 服务器:192.168.0.3:111的hash值为:1171828661
     * 服务器:192.168.0.4:111的hash值为:1764547046
     * [太阳]的hash值为1977106057, 被路由到结点[192.168.0.1:111] (因为太阳的hash值比所有服务器的hash值都大，所以按圆圈被路由到第一个Node)
     * [月亮]的hash值为1132637661, 被路由到结点[192.168.0.3:111]
     * [星星]的hash值为880019273, 被路由到结点[192.168.0.3:111]
     *
     * @param args
     */
    public static void main(String[] args) {
        String[] keys = {"太阳", "月亮", "星星"};
        for (int i = 0; i < keys.length; i++)
            System.out.println("[" + keys[i] + "]的hash值为" + HashUtils.hash_FNV1_32(keys[i])
                    + ", 被路由到结点[" + getServer(keys[i]) + "]");
    }
}
