package com.yiban.javaBase.dev.encrypt;

import java.io.UnsupportedEncodingException;

import org.apache.commons.codec.binary.Base64;

public class Base64Coder {
    /** 加密BYTE数组
     * 
     * @param bytes
     * @return */
    public static String encodeBytes(byte[] bytes) {
        if (bytes == null) {
            return null;
        }

        try {
            return new String(Base64.encodeBase64(bytes));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /** 加密字符串 */
    public static String encodeStr(String str) {
        if (str != null && !"".equals(str)) {
            try {
                return encodeBytes(str.getBytes("UTF-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /** 将字符串加密转换成BYTE数组
     * 
     * @param str
     * @return */
    public static byte[] decodeStr(String str) {
        if (str == null || "".equals(str)) {
            return null;
        }

        try {
            return Base64.decodeBase64(str.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /** 解密bytes
     * 
     * @param bytes
     * @return */
    public static byte[] decodeBytes(byte[] bytes) {
        if (bytes != null && bytes.length > 0) {
            try {
                return decodeStr(new String(bytes, "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
