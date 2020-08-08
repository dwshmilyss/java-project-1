package com.yiban.javaBase.dev.algorithm.consistent_hash.demo1;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Administrator on 2018/1/28 0028.
 */
public class HashUtils {
    /**
     * 使用MD5实现hash
     */
    private static MessageDigest md5 = null;

    /**
     * 使用FNV1_32_HASH算法计算服务器的Hash值,这里不使用重写hashCode的方法，最终效果没区别
     */
    public static int hash_FNV1_32(String str) {
        final int p = 16777619;
        int hash = (int) 2166136261L;
        for (int i = 0; i < str.length(); i++) {
            hash = (hash ^ str.charAt(i)) * p;
        }
        hash += hash << 13;
        hash ^= hash >> 7;
        hash += hash << 3;
        hash ^= hash >> 17;
        hash += hash << 5;

        // 如果算出来的值为负数则取其绝对值
        if (hash < 0) {
            hash = Math.abs(hash);
        }
        return hash;
    }

    /**
     * 使用MD5进行hash运算
     *
     * @param key
     * @return
     */
    public static long hash_MD5(String key) {
        if (md5 == null) {
            try {
                md5 = MessageDigest.getInstance("MD5");
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
        }
        md5.reset();
        md5.update(key.getBytes());
        byte[] bytes = md5.digest();
        //每个字节 & 0xFF再移位
        long result = ((long) (bytes[3] & 0xFF) << 24) | ((long) (bytes[2] & 0xFF) << 16) | ((long) (bytes[1] & 0xFF) << 8) | ((long) (bytes[0] & 0xFF));
        return result & 0xffffffffL;
    }

    /**
     * 俗称“Times33”算法，并不复杂, 不复杂所以计算效率才高
     * @param value
     * @return
     */
    public static long hash_DJB2(String value) {
        long hash = 5381;
        for (int i = 0; i < value.length(); i++) {
            /* hash * 33 + value[i] */
            hash = ((hash << 5) + hash) + value.charAt(i);
        }
        return (int) hash;
    }

    /**
     * 伯克利DB使用的hash算法
     * @param value
     * @return
     */
    public static long hash_sdbm(String value){
        long hash = 0;
        for (int i = 0; i < value.length(); i++) {
            /* hash * 65599-+
             + value[i] */
            hash = value.charAt(i) + (hash << 16) + (hash << 16) - hash;
        }
        return (int) hash;
    }
}
