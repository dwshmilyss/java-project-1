package com.yiban.javaBase.dev.algorithm.consistent_hash;

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

    //使用FNV1_32_HASH算法计算服务器的Hash值,这里不使用重写hashCode的方法，最终效果没区别
    public static int getHash(String str) {
        final int p = 16777619;
        int hash = (int) 2166136261L;
        for (int i = 0; i < str.length(); i++)
            hash = (hash ^ str.charAt(i)) * p;
        hash += hash << 13;
        hash ^= hash >> 7;
        hash += hash << 3;
        hash ^= hash >> 17;
        hash += hash << 5;

        // 如果算出来的值为负数则取其绝对值
        if (hash < 0)
            hash = Math.abs(hash);
        return hash;
    }

    public static long hash(String key) {
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
}
