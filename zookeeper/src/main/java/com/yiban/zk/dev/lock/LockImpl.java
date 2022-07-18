package com.yiban.zk.dev.lock;

import com.github.zkclient.ZkClient;
import com.github.zkclient.ZkClientUtils;
import com.yiban.zk.dev.MyZooKeeper;
import com.yiban.zk.dev.ZKOperate;
import org.apache.log4j.Logger;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.data.Stat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class LockImpl implements Lock {
    private static final long WAIT_TIME = 60;
    Logger logger = Logger.getLogger(LockImpl.class);

    private static final String LOCK_PREFIX = "/test/lock/seq-";
    private static final String LOCK = "/test/lock";

    AtomicInteger lockCount = new AtomicInteger(0);

    @Override
    public boolean lock() throws Exception {
        //实现可重入锁
        synchronized (this) {
            Thread thread = null;
            if (lockCount.get() == 0) {
                thread = Thread.currentThread();
                lockCount.incrementAndGet();
            } else {
                //如果是当前已经持有锁的线程重入，那么直接返回true（获取到锁）
                if (thread.equals(Thread.currentThread())) {
                    lockCount.incrementAndGet();
                    return true;
                }
            }
        }
        try {
            boolean locked = false;
            locked = tryLock();
            //拿到锁直接返回true
            if (locked) {
                return true;
            }
            //没有拿到就一直尝试获取锁，直到拿到锁
            while (!locked) {
                locked = tryLock();
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 尝试加锁
     * （1）创建临时顺序节点，并且保存自己的节点路径
     * （2）判断是否是第一个，如果是第一个，则加锁成功。如果不是，就找到前一个Znode节点，并且保存其路径到prior_path。
     *
     * @return
     */
    private boolean tryLock() throws Exception {
        ZKOperate zkOperate = ZKOperate.getInstance();
        //先创建根节点(代表一把分布式锁)
        // 如果根节点不存在
        boolean isRootZnodeExists = zkOperate.isExists("/test/lock", false);
        //如果根节点不存在 则创建
        if (!isRootZnodeExists) {
            boolean flag = zkOperate.createZNode("/test/lock", "", false, 1);
            //如果跟节点创建失败，直接抛出异常
            if (!flag) {
                throw new Exception("root znode create fail");
            }
        }

        //根节点创建成功之后，就要创建临时序列的Znode来代表加锁
        String lockedPath = zkOperate.createZNode1(LOCK_PREFIX, "", false, 3);
        if (lockedPath.isEmpty()) {
            throw new Exception("distributed lock Znode create fail");
        }
        //截取一下子节点的路径，用于后期的比较和查找，截取最后一级
        String lockedShortPath = getShorPath(lockedPath);
        //获取等待的子节点列表
        List<String> waiters = getWaiters();
        //获取等待的子节点列表，判断自己是否第一个
        if (checkLocked(waiters, lockedShortPath)) {
            return true;
        }
        //如果不是第一个，那么判断自己拍在第几
        int index = Collections.binarySearch(waiters, lockedShortPath);
        //可能由于网络抖动，获取到的子节点列表里可能已经没有自己了
        if (index < 0) {
            throw new Exception("节点没有找到：" + lockedShortPath);
        }
        //如果自己没有获得锁，则要监听前一个节点
        String priorPath = LOCK + "/" + waiters.get(index - 1);
        await(priorPath);
        return false;
    }

    /**
     * 如果没有获取到锁，那么进入等待的处理流程
     *
     * @param priorPath
     * @throws Exception
     */
    private void await(String priorPath) throws Exception {
        if (null == priorPath) {
            throw new Exception("prior_path error");
        }
        final CountDownLatch latch = new CountDownLatch(1);
        //首先添加一个watcher监听，而监听的地址正是上面一步返回的priorPath成员
        // 这里，仅仅会监听自己前一个节点的变动，而不是父节点下所有节点的变动。
        // 然后，调用latch.await，进入等待状态，等到latch.countDown()被唤醒。
        Watcher watcher = new Watcher() {
            @Override
            public void process(WatchedEvent watchedEvent) {
                Event.EventType eventType = watchedEvent.getType();
                //只观测删除节点事件
                if (eventType == Event.EventType.NodeDeleted) {
                    System.out.println("监听到的变化 watchedEvent = " + watchedEvent);
                    logger.info("[WatchedEvent]节点删除");
                    latch.countDown();
                }
            }
        };
        //订阅比自己次小顺序节点的删除事件
        MyZooKeeper.zooKeeper.getData(priorPath, watcher, new Stat());
        //等待
        latch.await(WAIT_TIME, TimeUnit.SECONDS);
    }


    private boolean checkLocked(List<String> waiters, String lockedShortPath) {
        //节点按照编号，升序排列
        Collections.sort(waiters);
        // 如果是第一个，代表自己已经获得了锁
        if (lockedShortPath.equals(waiters.get(0))) {
            logger.info("成功的获取分布式锁,节点为{" + lockedShortPath + "}");
            return true;
        }
        return false;
    }

    private String getShorPath(String lockedPath) {
        return lockedPath.substring(lockedPath.lastIndexOf("/") + 1);
    }

    /**
     * 获取竞争锁的节点IP
     *
     * @return
     */
    private List<String> getWaiters() {
        List<String> waiters = new ArrayList<>();
        ZKOperate zkOperate = ZKOperate.getInstance();
        waiters = zkOperate.getChild("/test/lock", false);
        return waiters;
    }

    /**
     * 解锁
     * @return
     */
    @Override
    public boolean unlock(Thread thread,String lockedPath) {
        //这一步有必要吗
        if (!thread.equals(Thread.currentThread())) {
            return false;
        }

        int newLockCount = lockCount.decrementAndGet();

        if (newLockCount < 0) {
            throw new IllegalMonitorStateException("Lock count has gone negative for lock: " + lockedPath);
        }

        if (newLockCount != 0) {
            return true;
        }
        ZKOperate zkOperate = ZKOperate.getInstance();
        //删除加锁的临时顺序节点
        try {
            if (zkOperate.isExists(lockedPath,false)) {
                zkOperate.deteleZNode(lockedPath,-1);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public static void main(String[] args) {
        LockImpl lockImpl = new LockImpl();
        System.out.println(lockImpl.getShorPath("/test/lock/seq-000000000"));
    }
}
