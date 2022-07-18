package com.linkflow.analysis.presto.security.util;

import redis.clients.jedis.Jedis;

import java.util.*;

/**
 * redis
 *
 * @auther WEI.DUAN
 * @date 2017/5/25
 * @website http://blog.csdn.net/dwshmilyss
 */
public class JRedisUtil extends RedisProvider {
    volatile static boolean flag = false;

    public static String set(String key, String value, int expire) {
        Jedis jedis = null;
        String rtn = null;
        try {
            jedis = getJedis();
            rtn = jedis.setex(key, expire, value);
        } catch (Exception e) {
            logger.error("set key error:" + e.getMessage());
        } finally {
            returnResource(jedispool, jedis);
        }
        return rtn;
    }


    /**
     * Get the value of the specified key.
     *
     * @param key
     * @return
     */
    public static String get(String key) {
        Jedis jedis = null;
        String rtn = null;
        try {
            jedis = getJedis();
            rtn = jedis.get(key);
        } catch (Exception e) {
            logger.error("get key error:" + e.getMessage());
        } finally {
            returnResource(jedispool, jedis);
        }
        return rtn;
    }

    /**
     * Get the values of all the specified keys
     *
     * @param keys
     * @return
     */
    public static List<String> mget(String... keys) {
        Jedis jedis = null;
        List<String> rtn = new ArrayList<String>();
        try {
            jedis = getJedis();
            rtn = jedis.mget(keys);
        } catch (Exception e) {
            logger.error("mget key error:" + e.getMessage());
        } finally {
            returnResource(jedispool, jedis);
        }
        return rtn;
    }

    /**
     * Set the the respective keys to the respective values.
     *
     * @param keysvalues
     * @return
     */
    public static String mset(String... keysvalues) {
        Jedis jedis = null;
        String rtn = null;
        try {
            jedis = getJedis();
            rtn = jedis.mset(keysvalues);
        } catch (Exception e) {
            logger.error("mset key error:" + e.getMessage());
        } finally {
            returnResource(jedispool, jedis);
        }
        return rtn;
    }

    /**
     * Return all the fields and associated values in a hash.
     *
     * @param key
     * @return
     */
    public static Map<String, String> hgetall(String key) {
        Jedis jedis = null;
        Map<String, String> rtn = new HashMap();
        try {
            jedis = getJedis();
            rtn = jedis.hgetAll(key);
        } catch (Exception e) {
            logger.error("hgetall key error:" + e.getMessage());
            
        } finally {
            returnResource(jedispool, jedis);
        }
        return rtn;
    }

    /**
     * Set the specified hash field to the specified value.
     * @param key
     * @param field
     * @param value
     * @return
     */
    public static Long hset(String key, String field, String value,int expire) {
        Jedis jedis = null;
        Long rtn = null;
        try {
            jedis = getJedis();
            rtn = jedis.hset(key, field, value);
            jedis.expire(key, expire);
        } catch (Exception e) {
            logger.error("hset key error:" + e.getMessage());
            
        } finally {
            returnResource(jedispool, jedis);
        }
        return rtn;
    }

    /**
     * 以map形式存放对象.
     *
     * @param key
     * @param field
     * @param obj
     * @return
     */
    public static long setObject(String key, String field, Object obj) {
        Jedis jedis = null;
        Long rtn = null;
        try {
            jedis = getJedis();
            rtn = jedis.hset(key.getBytes(), field.getBytes(),
                    ObjectsTranscoder.getObjectsTranscoder().serialize(obj));
        } catch (Exception e) {
            logger.error("get key error:" + e.getMessage());
            
        } finally {
            returnResource(jedispool, jedis);
        }
        return rtn;
    }

    /**
     * 获取对象.
     * @param key
     * @param field
     * @return
     */
    public static Object getObject(String key, String field) {
        Jedis jedis = null;
        byte[] rtn = null;
        try {
            jedis = getJedis();
            rtn = jedis.hget(key.getBytes(), field.getBytes());
        } catch (Exception e) {
            logger.error("getObject error:" + e.getMessage());
        } finally {
            returnResource(jedispool, jedis);
        }
        return ObjectsTranscoder.getObjectsTranscoder().deserialize(rtn);
    }

    public static void addObject(String key, Object obj, int expire) {
        Jedis jedis = null;
        try {
            jedis = getJedis();
            jedis.sadd(key.getBytes(), ObjectsTranscoder.getObjectsTranscoder()
                    .serialize(obj));
            jedis.expire(key.getBytes(), expire);
        } catch (Exception e) {
            logger.error("addObject error:" + e.getMessage());
        } finally {
            returnResource(jedispool, jedis);
        }
    }

    public static List<Object> getAllObject(String key) {
        List<Object> list = new ArrayList<Object>();
        Jedis jedis = null;
        try {
            jedis = getJedis();
            Set<byte[]> set = jedis.smembers(key.getBytes());
            if (set != null && !set.isEmpty()) {
                Iterator<byte[]> it = set.iterator();
                for (; it.hasNext(); ) {
                    byte[] b = it.next();
                    Object obj = ObjectsTranscoder.getObjectsTranscoder()
                            .deserialize(b);
                    list.add(obj);
                }
            }
        } catch (Exception e) {
            logger.error("getAllObject error:" + e.getMessage());
        } finally {
            returnResource(jedispool, jedis);
        }
        return list;
    }

    public static void delAllObject(String key) {
        Jedis jedis = null;
        try {
            jedis = getJedis();
            jedis.del(key.getBytes());
        } catch (Exception e) {
            logger.error("delAllObject error:" + e.getMessage());
        } finally {
            returnResource(jedispool, jedis);
        }
    }

    public static Long hset(String key, String field, String value) {
        Jedis jedis = null;
        Long rtn = null;
        try {
            jedis = getJedis();
            rtn = jedis.hset(key, field, value);
        } catch (Exception e) {
            logger.error("hset error:" + e.getMessage());
        } finally {
            returnResource(jedispool, jedis);
        }
        return rtn;
    }

    public static void hdel(String key) {
        Jedis jedis = null;
        try {
            jedis = getJedis();
            jedis.del(key);
        } catch (Exception e) {
            logger.error("hdel error:" + e.getMessage());
        } finally {
            returnResource(jedispool, jedis);
        }
    }

    public static void flush() {
        Jedis jedis = null;
        jedis = getJedis();
        jedis.flushAll();
    }

    public static String hget(String key, String field) {
        Jedis jedis = null;
        String rtn = null;
        try {
            jedis = getJedis();
            rtn = jedis.hget(key, field);
        } catch (Exception e) {
            logger.error("hget error:" + e.getMessage());
        } finally {
            returnResource(jedispool, jedis);
        }
        return rtn;
    }

    public static long hdel(String key, String[] field) {
        Jedis jedis = null;
        Long rtn = null;
        try {
            jedis = getJedis();
            rtn = jedis.hdel(key, field);
        } catch (Exception e) {
            logger.error("hdel error:" + e.getMessage());
        } finally {
            returnResource(jedispool, jedis);
        }
        return rtn;
    }

    public static long mdel(String[] key) {
        Jedis jedis = null;
        Long rtn = null;
        try {
            jedis = getJedis();
            rtn = jedis.del(key);
        } catch (Exception e) {
            logger.error("mdel error:" + e.getMessage());
        } finally {
            returnResource(jedispool, jedis);
        }
        return rtn;
    }

    /**
     * 设置分布式锁
     *
     * @param key
     * @param value
     * @return
     */
    public static long setLock(String key, String value, int expire) {
        Jedis jedis = null;
        Long rtn = null;
        try {
            jedis = getJedis();
            rtn = jedis.setnx(key, value);
            jedis.expire(key, expire);
        } catch (Exception e) {
            logger.error("setLock error:" + e.getMessage());
        } finally {
            returnResource(jedispool, jedis);
        }
        return rtn;
    }

    /**
     * 释放锁
     *
     * @param key
     * @return
     */
    public static long delLock(String key) {
        Jedis jedis = null;
        Long rtn = null;
        try {
            jedis = getJedis();
            rtn = jedis.del(key);
        } catch (Exception e) {
            logger.error("delLock error:" + e.getMessage());
            
        } finally {
            returnResource(jedispool, jedis);
        }
        return rtn;
    }

    /**
     * 存储子调用链的list
     * @param dateKey
     * @param cid
     */
    public static void memoryCid(String dateKey, String cid, int expire) {
        Jedis jedis = null;
        try {
            jedis = getJedis();
            jedis.sadd(dateKey, cid);
            jedis.expire(dateKey, expire);
        } catch (Exception e) {
            logger.error("memoryCid error:" + e.getMessage());
        } finally {
            returnResource(jedispool, jedis);
        }
    }

    /**
     * 获取调用链list
     * @param dateKey
     * @return
     */
    public static Set<String> getAllCids(String dateKey) {
        Jedis jedis = null;
        Set<String> set = null;
        try {
            jedis = getJedis();
            set = jedis.smembers(dateKey);
        } catch (Exception e) {
            logger.error("getAllCids error:" + e.getMessage());
        } finally {
            returnResource(jedispool, jedis);
        }
        return set;
    }

    public static long incrBy(String dateKey, long offset) {
        Jedis jedis = null;
        long value = 0;
        try {
            jedis = getJedis();
            value = jedis.incrBy(dateKey, offset);
        } catch (Exception e) {
            logger.error("incrBy error:" + e.getMessage());
        } finally {
            returnResource(jedispool, jedis);
        }
        return value;
    }

    public static void main(String[] args) {
//        final String key = "aa";
//        ExecutorService es = Executors.newFixedThreadPool(10);
//        for (int j = 0; j < 10; j++) {
//            es.execute(() -> {
//                long temp = 0;
//                for (int i = 0; i < 1000; i++) {
//                    temp = incrBy(key, 2);
//                    if (!flag) {
//                        System.out.println(Thread.currentThread().getName() + " : " + temp);
//                    }
//                    if (temp >= 2000) {
//                        flag = true;
//                        break;
//                    }
//                }
//            });
//        }
//        es.shutdown();
        Map<String,String> auth = hgetall("presto_auth_user");
        for (Map.Entry<String, String> entry : auth.entrySet()) {
            System.out.println("key = " + entry.getKey() + ",value = " + entry.getValue());
        }

        Map<String,String> schemes = hgetall("presto_access_schemas");
        for (Map.Entry<String, String> entry : schemes.entrySet()) {
            System.out.println("key = " + entry.getKey() + ",value = " + entry.getValue());
        }

        Map<String,String> tables = hgetall("presto_access_tables");
        for (Map.Entry<String, String> entry : tables.entrySet()) {
            System.out.println("key = " + entry.getKey() + ",value = " + entry.getValue());
        }

        System.out.println("password.encryption.enable = " + get("password.encryption.enable"));
    }
}
