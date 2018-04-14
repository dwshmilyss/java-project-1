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
    protected static int EXPIRE = 130;

    static {
        ResourceBundle bundle = ResourceBundle.getBundle("redis");
        if (bundle == null) {
            throw new IllegalArgumentException(
                    "[redis.properties] is not found!");
        }

        EXPIRE = Integer.valueOf(bundle.getString("redis.expire"));

        JedisPoolConfig jedisconfig = new JedisPoolConfig();
//        jedisconfig.setMaxTotal(Integer.valueOf(bundle
//                .getString("redis.pool.maxActive")));
        jedisconfig.setMaxIdle(Integer.valueOf(bundle
                .getString("redis.pool.maxIdle")));
//        jedisconfig.setMaxWaitMillis(Long.valueOf(bundle
//                .getString("redis.pool.maxWait")));
        jedisconfig.setTestOnBorrow(Boolean.valueOf(bundle
                .getString("redis.pool.testOnBorrow")));
        jedisconfig.setTestOnReturn(Boolean.valueOf(bundle
                .getString("redis.pool.testOnReturn")));
        jedispool = new JedisPool(jedisconfig, bundle.getString("redis.ip"),
                Integer.valueOf(bundle.getString("redis.port")), 100000);
    }

    public static Jedis getJedis() {
        Jedis jedis = null;
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
            pool.returnResource(jedis);
        }
    }
}
