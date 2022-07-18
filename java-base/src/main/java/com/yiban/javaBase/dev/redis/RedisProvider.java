package com.yiban.javaBase.dev.redis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.exceptions.JedisConnectionException;

import java.util.ResourceBundle;

/**
 * provider
 *
 * @auther WEI.DUAN
 * @date 2017/5/25
 * @website http://blog.csdn.net/dwshmilyss
 */
public class RedisProvider {
    protected static final Logger LOG = LoggerFactory.getLogger(RedisProvider.class);
    protected static JedisPool jedispool;

    static {
        ResourceBundle bundle = ResourceBundle.getBundle("redis");
        if (bundle == null) {
            throw new IllegalArgumentException(
                    "[redis.properties] is not found!");
        }

        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxTotal(Integer.valueOf(bundle.getString("redis.pool.maxActive")));
        jedisPoolConfig.setMaxIdle(Integer.valueOf(bundle.getString("redis.pool.maxIdle")));
        jedisPoolConfig.setMinIdle(Integer.valueOf(bundle.getString("redis.pool.minIdle")));
        jedisPoolConfig.setMaxWaitMillis(Long.valueOf(bundle.getString("redis.pool.maxWait")));
        jedisPoolConfig.setTestOnBorrow(Boolean.valueOf(bundle.getString("redis.pool.testOnBorrow")));
        jedisPoolConfig.setTestOnReturn(Boolean.valueOf(bundle.getString("redis.pool.testOnReturn")));
        jedispool = new JedisPool(jedisPoolConfig, bundle.getString("redis.ip"),Integer.valueOf(bundle.getString("redis.port")), Integer.valueOf(bundle.getString("redis.timeout")));
    }

    public static Jedis getJedis() {
        Jedis jedis;
        try {
            jedis = jedispool.getResource();
        } catch (JedisConnectionException jce) {
            ExceptionUtil.getTrace(jce);
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                ExceptionUtil.getTrace(e);
            }
            jedis = jedispool.getResource();
        }
        return jedis;
    }

    public static void returnResource(JedisPool pool, Jedis jedis) {
        if (jedis != null) {
            jedis.close();
        }
    }
}
