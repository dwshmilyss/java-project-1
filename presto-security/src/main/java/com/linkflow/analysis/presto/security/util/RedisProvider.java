package com.linkflow.analysis.presto.security.util;

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
    protected static final Logger logger = LoggerFactory.getLogger(RedisProvider.class);
    protected static JedisPool jedispool;

    static {
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        System.out.println(Config.configs.get("spring.redis.pool.max-active"));
        jedisPoolConfig.setMaxTotal(Integer.valueOf(Config.configs.get("spring.redis.pool.max-active").toString()));
        jedisPoolConfig.setMaxIdle(Integer.valueOf(Config.configs.get("spring.redis.pool.max-idle").toString()));
        jedisPoolConfig.setMinIdle(Integer.valueOf(Config.configs.get("spring.redis.pool.min-idle").toString()));
        jedisPoolConfig.setMaxWaitMillis(Long.valueOf(Config.configs.get("spring.redis.pool.max-wait").toString()));
        String host = Config.configs.get("spring.redis.host").toString();
        int port = Integer.valueOf(Config.configs.get("spring.redis.port").toString());
        int timeout = Integer.valueOf(Config.configs.get("spring.redis.timeout").toString());
        String password = Config.configs.get("spring.redis.password").toString();
        int database = Integer.valueOf(Config.configs.get("spring.redis.database").toString());
        jedispool = new JedisPool(jedisPoolConfig, host, port, timeout, password, database);
    }

    public static Jedis getJedis() {
        Jedis jedis = null;
        try {
            jedis = jedispool.getResource();
        } catch (JedisConnectionException ex) {
            ex.printStackTrace();
            logger.error("jredis get resource error " + ex.getMessage());
        }
        return jedis;
    }

    public static void returnResource(JedisPool pool, Jedis jedis) {
        if (jedis != null) {
            jedis.close();
        }
    }
}
