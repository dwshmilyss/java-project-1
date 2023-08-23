package com.yiban.spring.spring_core.dev.cache;

import com.google.common.collect.Maps;
import org.springframework.context.annotation.Bean;

import javax.annotation.Resource;
import java.util.Map;

/**
 * 定义一个缓存管理器，这个管理器负责实现缓存逻辑，支持对象的增加、修改和删除，支持值对象的泛型。
 *
 * @auther WEI.DUAN
 * @date 2019/11/1
 * @website http://blog.csdn.net/dwshmilyss
 */
public class CacheContext<T> {
    private Map<String, T> cache = Maps.newConcurrentMap();

    public T getKey(String key) {
        return cache.get(key);
    }

    public void addOrUpdateKey(String key,T value) {
        cache.put(key, value);
    }

    public void evictCache(String key) {
        if (cache.containsKey(key)) {
            cache.remove(key);
        }
    }

    public void evictCache() {
        cache.clear();
    }
}