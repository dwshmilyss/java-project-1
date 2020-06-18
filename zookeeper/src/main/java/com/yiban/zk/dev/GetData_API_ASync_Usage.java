package com.yiban.zk.dev;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * 测试getData()的异步操作
 *
 * @auther WEI.DUAN
 * @date 2020/4/8
 * @website http://blog.csdn.net/dwshmilyss
 */
public class GetData_API_ASync_Usage implements Watcher {
    public static CountDownLatch connectedSemaphore = new CountDownLatch(1);
    private static ZooKeeper zookeeper;
    public static final String SERVER_ADDRESS = "10.21.3.73:2181,10.21.3.74:2181,10.21.3.75:2181,10.21.3.76:2181,10.21.3.77:2181";

    @Override
    public void process(WatchedEvent event) {
        if (Event.KeeperState.SyncConnected == event.getState()) {
            if (Event.EventType.None == event.getType() && null == event.getPath()) {
                connectedSemaphore.countDown();
            } else if (event.getType() == Event.EventType.NodeDataChanged) {
                try {
                    zookeeper.getData( event.getPath(), true, new MyDataCallback(), null );
                } catch (Exception e) {}
            }
        }
    }

    public static void main(String[] args) throws Exception {
        String path = "/zk-book";
        zookeeper = new ZooKeeper(GetData_API_ASync_Usage.SERVER_ADDRESS, 5000, new GetData_API_ASync_Usage());
        connectedSemaphore.await();
        zookeeper.create(path, "123".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
        zookeeper.getData(path, true, new MyDataCallback(), null);
        zookeeper.setData(path, "123".getBytes(), -1);
        Thread.sleep(Integer.MAX_VALUE);
        //这里会输出两次 第二次的Mzxid会+1 即数据被修改过
        //0, /zk-book, 123
        //128849019615,128849019615,0
        //0, /zk-book, 123
        //128849019615,128849019616,1
    }
}

class MyDataCallback implements AsyncCallback.DataCallback {

    @Override
    public void processResult(int rc, String path, Object ctx, byte[] data, Stat stat) {
        System.out.println(rc + ", " + path + ", " + new String(data));
        System.out.println(stat.getCzxid() + "," + stat.getMzxid() + "," + stat.getVersion());
    }
}
