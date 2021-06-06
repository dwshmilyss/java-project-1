package com.yiban.javaBase.dev.algorithm.bloom_filter;

import com.google.common.hash.Funnels;
import com.google.common.hash.Hashing;
import com.yiban.javaBase.dev.redis.RedisPool;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;

import java.io.IOException;
import java.nio.charset.Charset;

/**
 * @auther WEI.DUAN
 * @date 2019/3/7
 * @website http://blog.csdn.net/dwshmilyss
 */
public class BloomFilterByRedis {
    static Jedis jedis = RedisPool.getJedis();
    //预计插入量
    private long expectedInsertions = 100000000;
    //可接受的错误率
    private double fpp = 0.001F;
    //布隆过滤器的key在redis中的前缀 利用它可以统计过滤器对redis的使用情况
    private String redisKeyPrefix = "bf:";

    public void setExpectedInsertions(long expectedInsertions) {
        this.expectedInsertions = expectedInsertions;
    }

    public void setFpp(double fpp) {
        this.fpp = fpp;
    }

    public void setRedisKeyPrefix(String redisKeyPrefix) {
        this.redisKeyPrefix = redisKeyPrefix;
    }

    //bit数组长度
    private long numBits = optimalNumOfBits(expectedInsertions, fpp);
    //hash函数数量
    private int numHashFunctions = optimalNumOfHashFunctions(expectedInsertions, numBits);

    //计算hash函数个数 方法来自guava
    private int optimalNumOfHashFunctions(long n, long m) {
        return Math.max(1, (int) Math.round((double) m / n * Math.log(2)));
    }

    //计算bit数组长度 方法来自guava
    private long optimalNumOfBits(long n, double p) {
        if (p == 0) {
            p = Double.MIN_VALUE;
        }
        return (long) (-n * Math.log(p) / (Math.log(2) * Math.log(2)));
    }

    /**
     * 判断keys是否存在于集合where中
     */
    public boolean isExist(String where, String key) {
        long[] indexs = getIndexs(key);
        boolean result;
        //这里使用了Redis管道来降低过滤器运行当中访问Redis次数 降低Redis并发量
        Pipeline pipeline = jedis.pipelined();
        try {
            for (long index : indexs) {
                pipeline.getbit(getRedisKey(where), index);
            }
            result = !pipeline.syncAndReturnAll().contains(false);
        } finally {
            try {
                pipeline.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (!result) {
            put(where, key);
        }
        return result;
    }

    /**
     * 将key存入redis bitmap
     */
    private void put(String where, String key) {
        long[] indexs = getIndexs(key);
        //这里使用了Redis管道来降低过滤器运行当中访问Redis次数 降低Redis并发量
        Pipeline pipeline = jedis.pipelined();
        try {
            for (long index : indexs) {
                pipeline.setbit(getRedisKey(where), index, true);
            }
            pipeline.sync();
        } finally {
            try {
                pipeline.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 根据key获取bitmap下标 方法来自guava
     */
    private long[] getIndexs(String key) {
        long hash1 = hash(key);
        long hash2 = hash1 >>> 16;
        long[] result = new long[numHashFunctions];
        for (int i = 0; i < numHashFunctions; i++) {
            long combinedHash = hash1 + i * hash2;
            if (combinedHash < 0) {
                combinedHash = ~combinedHash;
            }
            result[i] = combinedHash % numBits;
        }
        return result;
    }

    /**
     * 获取一个hash值 方法来自guava
     */
    private long hash(String key) {
        Charset charset = Charset.forName("UTF-8");
        return Hashing.murmur3_128().hashObject(key, Funnels.stringFunnel(charset)).asLong();
    }

    private String getRedisKey(String where) {
        return redisKeyPrefix + where;
    }

    public static void main(String[] args) {
        BloomFilterByRedis bloomFilter = new BloomFilterByRedis();

//        System.out.println(bloomFilter.numBits);
//        System.out.println(bloomFilter.numHashFunctions);


        System.out.println(bloomFilter.isExist("ccc","bb"));
        System.out.println(bloomFilter.isExist("ccc","cc"));
        System.out.println(bloomFilter.isExist("ccc","dd"));
        System.out.println(bloomFilter.isExist("ccc","ee"));
        System.out.println(bloomFilter.isExist("ccc","ff"));
        System.out.println(bloomFilter.isExist("ccc","gg"));
//        System.out.println(bloomFilter.isExist("aaa","aaa"));
//        System.out.println(bloomFilter.isExist("aaa","bbb"));
//        System.out.println(bloomFilter.isExist("bbb","aaa"));
    }
}