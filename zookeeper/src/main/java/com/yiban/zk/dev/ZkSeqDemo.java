package com.yiban.zk.dev;

import com.github.zkclient.IZkChildListener;
import com.github.zkclient.ZkClient;
import org.apache.zookeeper.data.Stat;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @auther WEI.DUAN
 * @date 2019/3/23
 * @website http://blog.csdn.net/dwshmilyss
 */
public class ZkSeqDemo {
    public static final String SEQ_ZNODE = "/seq";


    public static void createNode() {
        ZkClient zkClient = new ZkClient("10.21.3.77:2181",50000);
        zkClient.createPersistent(SEQ_ZNODE);
        zkClient.close();
    }

    public static void main(String[] args) {
//        createNode();
        final ExecutorService service = Executors.newFixedThreadPool(20);

        ZkClient zkClient = new ZkClient("10.21.3.77:2181",50000);
//        zkClient.createPersistent(LOCK_ZNODE);
//        zkClient.close();

//        for (int i = 0; i < 10; i++) {
//            service.execute(new Task2("[Concurrent-" + i + "]"));
//        }
        String selfZnode = zkClient.createEphemeralSequential(LOCK_ZNODE + "/loc", new byte[0]);
        service.shutdown();
    }

    public static class Task implements Runnable{
        private String taskName;
        public Task(String taskName) {
            this.taskName = taskName;
        }
        @Override
        public void run() {
            // 连接zk服务器
            ZkClient zkClient = new ZkClient("10.21.3.77:2181",50000);
            Stat stat = zkClient.writeData(SEQ_ZNODE,new byte[0],-1);
            int versionAsSeq = stat.getVersion();
            System.out.println(taskName + ",versionAsSeq = " + versionAsSeq);
            zkClient.close();
        }
    }


    //提前创建好锁对象的结点"/lock" CreateMode.PERSISTENT
    public static final String LOCK_ZNODE = "/lock";
    //分布式锁实现分布式seq生成
    public static class Task2 implements Runnable, IZkChildListener {

        private final String taskName;

        private final ZkClient zkClient;

        private final String lockPrefix = "/loc";

        private final String selfZnode;

        public Task2(String taskName) {
            this.taskName = taskName;
            zkClient = new ZkClient("10.21.3.77:2181", 30000, 50000);
            selfZnode = zkClient.createEphemeralSequential(LOCK_ZNODE + lockPrefix, new byte[0]);
        }

        @Override
        public void run() {

            createSeq();
        }

        private void createSeq() {
            Stat stat = new Stat();
            byte[] oldData = zkClient.readData(LOCK_ZNODE, stat);
            byte[] newData = update(oldData);
            zkClient.writeData(LOCK_ZNODE, newData);
            System.out.println(taskName + selfZnode + " obtain seq=" + new String(newData));
        }

        private byte[] update(byte[] currentData) {
            String s = new String(currentData);
            int d = Integer.parseInt(s);
            d = d + 1;
            s = String.valueOf(d);
            return s.getBytes();
        }

        public void handleChildChange(String parentPath,
                                      List<String> currentChildren) throws Exception {
            // TODO Auto-generated method stub

        }

    }
}