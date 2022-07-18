package com.yiban.javaBase.dev.collections.queue;



import lombok.extern.log4j.Log4j2;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 用DelayQueue实现一个cache
 *
 * @auther WEI.DUAN
 * @date 2017/5/3
 * @website http://blog.csdn.net/dwshmilyss
 */
@Log4j2
public class CacheDemo {

    // 测试入口函数
    public static void main(String[] args) throws Exception {
        Cache<Integer, String> cache = new Cache<Integer, String>();
        cache.put(1, "aaaa", 5, TimeUnit.SECONDS);
        cache.put(1, "bbbb", 3, TimeUnit.SECONDS);

        Thread.sleep(1000 * 2);
        {
            String str = cache.get(1);
            System.out.println(str);
        }

        Thread.sleep(1000 * 2);
        {
            String str = cache.get(1);
            System.out.println(str);
        }
    }

    static class DelayItem<T> implements Delayed {
        //Sequence number to break scheduling ties, and in turn to guarantee FIFO order among tied entries.
        private static final AtomicLong sequencer = new AtomicLong(0);
        private long sequenceNumber = 0;
        private long time;
        private T item;

        public DelayItem(T submit, long timeout) {
            this.time = System.nanoTime() + timeout;
            this.item = submit;
            this.sequenceNumber = sequencer.getAndIncrement();
        }

        public T getItem() {
            return this.item;
        }

        @Override
        public long getDelay(TimeUnit unit) {
            long d = unit.convert(time - System.nanoTime(), TimeUnit.NANOSECONDS);
            return d;
        }

        @Override
        public int compareTo(Delayed other) {
            if (other == this) // compare zero ONLY if same object
                return 0;
            if (other instanceof DelayItem) {
                DelayItem x = (DelayItem) other;
                long diff = time - x.time;
                if (diff < 0)
                    return -1;
                else if (diff > 0)
                    return 1;
                else if (sequenceNumber < x.sequenceNumber)
                    return -1;
                else
                    return 1;
            }
            long d = (getDelay(TimeUnit.NANOSECONDS) - other.getDelay(TimeUnit.NANOSECONDS));
            return (d == 0) ? 0 : ((d < 0) ? -1 : 1);
        }

        @Override
        public int hashCode() {
            return item.hashCode();
        }

        @Override
        public boolean equals(Object object) {
            if (object instanceof DelayItem) {
                return object.hashCode() == hashCode() ? true : false;
            }
            return false;
        }
    }

    static class Cache<K, V> {
        private ConcurrentMap<K, V> cacheObjMap = new ConcurrentHashMap();

        private DelayQueue<DelayItem<K>> q = new DelayQueue();

        private Thread daemonThread;

        public Cache() {
            //启动一个daemon thread检查过期的缓存
            Runnable daemonTask = new Runnable() {
                public void run() {
                    daemonCheck();
                }
            };

            daemonThread = new Thread(daemonTask);
            daemonThread.setDaemon(true);
            daemonThread.setName("Cache Daemon");
            daemonThread.start();
        }

        private void daemonCheck() {

            log.info("cache service started.");

            for (; ; ) {
                try {
                    //取出过期队列中已经过期的元素，take()按插入时间从早到晚依次取
                    DelayItem<K> delayItem = q.take();
                    if (delayItem != null) {
                        // 超时对象处理
                        K key = delayItem.getItem();
                        //从缓存中移除
                        cacheObjMap.remove(key); // compare and remove
                    }
                } catch (InterruptedException e) {
                    log.info(e.getMessage(), e);
                    break;
                }
            }
            log.info("cache service stopped.");
        }

        // 添加缓存对象
        public void put(K key, V value, long time, TimeUnit unit) {
            V oldValue = cacheObjMap.put(key, value);
            //把过期时间转换成纳秒
            long nanoTime = TimeUnit.NANOSECONDS.convert(time, unit);
            DelayItem<K> tmpItem = new DelayItem(key, nanoTime);
            //如果这个key已被缓存过
            if (oldValue != null) {
                System.out.println("move before = " + q.size());
                //从队列中移除
                q.remove(tmpItem);
                System.out.println("move end = " + q.size());
            }
            //放入延时队列中
            q.put(tmpItem);
        }

        public V get(K key) {
            return cacheObjMap.get(key);
        }

    }

}
