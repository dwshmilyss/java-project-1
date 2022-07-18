package com.yiban.javaBase.test;

import com.yiban.javaBase.dev.redis.RedisPool;
import org.junit.Test;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * @auther WEI.DUAN
 * @date 2019/3/7
 * @website http://blog.csdn.net/dwshmilyss
 */
public class RedisTest {
    @Test
    public void test(){
        Jedis jedis = RedisPool.getJedis();
        System.out.println("value = " + jedis.ttl("bb"));
    }
}