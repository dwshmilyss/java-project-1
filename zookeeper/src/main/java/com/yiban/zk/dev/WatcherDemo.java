package com.yiban.zk.dev;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.util.List;

public class WatcherDemo {
    private static ZooKeeper zk;
    private static String zkAddress = "localhost:2181";  // ZooKeeper 服务器地址

    public static void main(String[] args) throws Exception {
        zk = new ZooKeeper(zkAddress, 3000, new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                System.out.println("Watcher triggered, event: " + event);
                // 重新设置 watch 监听子节点变化，实现无限次监听
//                setWatchOnChildren("/myNode");//监听子节点
                setWatchOnNode("/myNode");//监听当前节点
            }
        });

        // 初始调用设置 watch
//        setWatchOnChildren("/myNode");
        setWatchOnNode("/myNode");

        // 主线程等待，不让程序退出
        Thread.sleep(Long.MAX_VALUE);
    }

    private static void setWatchOnChildren(String path) {
        // 获取子节点并设置 watch
        zk.getChildren(path, true, new AsyncCallback.ChildrenCallback() {
            @Override
            public void processResult(int rc, String path, Object ctx, List<String> children) {
                if (rc == KeeperException.Code.OK.intValue()) {
                    System.out.println("Children of " + path + ": " + children);
                } else {
                    System.out.println("Error retrieving children");
                }
                // 子节点发生变化后，继续设置 watch
            }
        }, null);
    }

    private static void setWatchOnNode(String path) {
        // 获取节点数据并设置 watch
        zk.getData(path, true, new AsyncCallback.DataCallback() {
            @Override
            public void processResult(int rc, String path, Object ctx, byte[] data, Stat stat) {
                if (rc == KeeperException.Code.OK.intValue()) {
                    System.out.println("Node data: " + new String(data));
                } else {
                    System.out.println("Error retrieving node data");
                }
                // 事件触发后，继续设置 watch
            }
        }, null);
    }

}
