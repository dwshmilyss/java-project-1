package com.yiban.javaBase.dev.cache;

import com.yiban.javaBase.dev.redis.RedisPool;
import redis.clients.jedis.Jedis;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 测试一些缓存的使用场景
 *
 * @auther WEI.DUAN
 * @date 2019/3/7
 * @website http://blog.csdn.net/dwshmilyss
 */
public class CacheDemo {
    //互斥锁
    static ReentrantLock lock = new ReentrantLock();
    static Jedis jedis = RedisPool.getJedis();

    /**
     * 避免缓存击穿的解决方案
     * 缓存失效是造成缓存击穿的主要原因
     * 缓存击穿，是指一个key非常热点，在不停的扛着大并发，大并发集中对这一个点进行访问，当这个key在失效的瞬间，持续的大并发就穿破缓存，直接请求数据库，就像在一个屏障上凿开了一个洞。
     * 有几种解决方式：
     * 1. 后台定时刷新缓存
     * 2. 设计多级缓存
     * 3. 主动检查缓存过期时间 如果发现过期 主动更新
     * 4. 加锁
     * 下面的方法就是最佳的加锁实现（伪代码）
     */
    public static List<String> getData() throws InterruptedException {
        List<String> result = new ArrayList<>();
        //从缓存中读取数据
        result = getDataFromCache();
        //如果缓存中没有
        if (result.isEmpty()){
            //尝试获取锁
            if (lock.tryLock()) {
                //如果拿到锁了
                try {
                    //从数据库中查询数据
                    result = getDataFromDB();
                    //并写入缓存
                    setDataToCache(result);
                } finally {
                    lock.unlock();//释放锁
                }
            }else { //如果没有拿到锁
                //先看看其他线程是否已经写了缓存了
                result = getDataFromCache();
                //如果还没有缓存数据
                if (result.isEmpty()) {
                    //休息一下
                    Thread.sleep(100);
                    //重试
                    return getData();
                }
            }
        }
        return result;
    }

    /**
     * 从数据库中获取数据
     * @return
     */
    private static List<String> getDataFromDB() {
        return new ArrayList<>();
    }

    /**
     * 从缓存中获取数据（伪代码）
     */
    public static List<String> getDataFromCache(){
        return new ArrayList<>();
    }

    /**
     * 将数据写入缓存（伪代码）
     */
    public static void setDataToCache(List<String> data){
    }

    /**
     * 高并发的情况下还可以使用分布式锁来 例如redis的setnx()
     * 这个方法的实现和上面使用java的ReentrantLock一模一样
     */
    public static String getKeyByRedis(String key) throws InterruptedException {

        //首先从缓存中获取
        String value = "";
        value = jedis.get(key);
        //如果在缓存中没有找到
        if (value == ""){
            //上锁，如果上锁成功
            if (jedis.setnx("flag","1") == 1){
                //设置锁的超时时间为3分钟
                jedis.expire("flag", 3 * 60);
                //TODO 从DB中获取数据
                //然后写入到缓存中
                jedis.set(key,value);
                //删除锁
                jedis.del("flag");
            }else { // 如果上锁失败
                //先休息一下
                Thread.sleep(100);
                //然后重试
                return getKeyByRedis(key);
            }
        }
        return value;
    }

    /**
     * 还有一种方法就是缓存永不“过期”（其实就是手动检查缓存是否失效 如果失效 立即从DB中检索并更新缓存）
     * @param key
     * @return
     */
    public static String getKeyByNoExpire(final String key) {
        String value = "";
        value = jedis.get(key);
        ThreadPoolExecutor threadPool = new ThreadPoolExecutor(0,Integer.MAX_VALUE,60L, TimeUnit.SECONDS, new SynchronousQueue<Runnable>(), new ThreadPoolExecutor.DiscardOldestPolicy());
        //获取key的剩余时间（秒）
        long timeout = jedis.ttl(key);
        if (timeout <= 0){
            // 异步更新后台异常执行
            threadPool.execute(new Runnable() {
                public void run() {
                    String keyMutex = "mutex:" + key;
                    if (jedis.setnx(keyMutex, "1") == 1) {
                        // 3 min timeout to avoid mutex holder crash
                        jedis.expire(keyMutex, 3 * 60);
                        //TODO 获取DB中的数据
                        String dbValue = "";
//                        dbValue = db.get(key);
                        jedis.set(key, dbValue);
                        jedis.del(keyMutex);
                    }
                }
            });
        }
        return value;
    }
}