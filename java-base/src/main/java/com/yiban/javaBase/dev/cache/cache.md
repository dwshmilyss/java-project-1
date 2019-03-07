#### 应对缓存击穿的解决方案
- 互斥锁
```aidl
我们最先想到的应该是加锁获取缓存。也就是当获取的value值为空时（这里的空表示缓存过期），先加锁，然后从数据库加载并放入缓存，最后释放锁。如果其他线程获取锁失败，则睡眠一段时间后重试。下面使用Redis的setnx来实现分布式锁
String get(String key) {
    String value = redis.get(key);  
    if (value  == null) {  
        if (redis.setnx(key_mutex, "1")) {  
            // 3 min timeout to avoid mutex holder crash  
            redis.expire(key_mutex, 3 * 60)  
            value = db.get(key);  
            redis.set(key, value);  
            redis.delete(key_mutex);  
        } else {  
            //其他线程休息50毫秒后重试  
            Thread.sleep(50);
            get(key);
        }
    }  
}
```
---
- 缓存永不过期
```aidl
真正的缓存过期时间不由Redis控制，而是由程序代码控制。当获取数据时发现缓存超时时，就需要发起一个异步请求去加载数据。这种策略的优点就是不会产生死锁等现象，但是有可能会造成缓存不一致的现象
String get(final String key) {
    V v = redis.get(key);
    String value = v.getValue();
    long timeout = v.getTimeout();
    if (v.timeout <= System.currentTimeMillis()) {
        // 异步更新后台异常执行
        threadPool.execute(new Runnable() {
            public void run() {
                String keyMutex = "mutex:" + key;
                if (redis.setnx(keyMutex, "1")) {
                    // 3 min timeout to avoid mutex holder crash
                    redis.expire(keyMutex, 3 * 60);
                    String dbValue = db.get(key);
                    redis.set(key, dbValue);
                    redis.delete(keyMutex);
                }
            }
        });
    }
    return value;
}
```