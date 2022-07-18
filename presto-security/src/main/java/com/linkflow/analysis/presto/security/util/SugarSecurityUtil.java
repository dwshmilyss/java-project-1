package com.linkflow.analysis.presto.security.util;

public class SugarSecurityUtil {
    private static final byte[] KEY = new byte[] {
            16, 97, 114, 107, 46, 97, 110, 97, 108, 121,
            115, 121, 115, 46, 120, 121, 122 };

    public static final byte[] decryptBASE64(String key) {
        try {
            return (new BASE64Encoder()).decode(key);
        } catch (Exception e) {
            throw new RuntimeException("解密错误，错误信息：", e);
        }
    }

    public static final String encryptBASE64(byte[] key) {
        try {
            return (new BASE64Encoder()).encode(key);
        } catch (Exception e) {
            throw new RuntimeException("加密错误，错误信息：", e);
        }
    }

    public static final String decryptDes(String cryptData) {
        String decryptedData = null;
        try {
            decryptedData = new String(DESCoder.decrypt(decryptBASE64(cryptData), KEY));
        } catch (Exception e) {
            throw new RuntimeException("解密错误，错误信息：", e);
        }
        return decryptedData;
    }

    public static final String encryptDes(String data) {
        String encryptedData = null;
        try {
            encryptedData = encryptBASE64(DESCoder.encrypt(data.getBytes(), KEY));
        } catch (Exception e) {
            throw new RuntimeException("加密错误，错误信息：", e);
        }
        return encryptedData;
    }

    public static void main(String[] args) {
        System.out.println("encryptDes 123 -> " + encryptDes("123"));
        System.out.println("decryptDes 123 -> " + decryptDes("IXdW5SuQ/yY="));
    }
}
