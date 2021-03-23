package com.yiban.javaBase.dev.encrypt;

import java.security.MessageDigest;

import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * MD5加密
 *
 * @author junqing.cao
 * @date 2012-9-14
 */
public class MD5 {
    private static final Logger log = LoggerFactory.getLogger(MD5.class);

    private static MD5 md5 = null;

    private MD5() {
    }

    public static synchronized MD5 getInstance() {
        if (md5 == null) {
            md5 = new MD5();
        }
        return md5;
    }

    /**
     * 将字符串加密
     *
     * @param string
     * @return
     */
    public static String encrypt(String string) {
        if (string != null && !"".equals(string)) {
            return getInstance().getMD5ByBytes(string.getBytes());
        } else {
            return string;
        }
    }

    private String getMD5ByBytes(byte[] source) {
        String s = null;
        // 用来将字节转换成 16 进制表示的字符
        char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                'a', 'b', 'c', 'd', 'e', 'f' };
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(source);
            byte tmp[] = md.digest(); // MD5 的计算结果是一个128位的长整数， 用字节表示就是16个字节
            char str[] = new char[16 * 2]; // 每个字节用16进制表示的话，使用两个字符，所以表示成16进制需要32个字符
            int k = 0; // 表示转换结果中对应的字符位置
            for (int i = 0; i < 16; i++) { // 从第一个字节开始，对 MD5的每一个字节，转换成16进制字符的转换
                byte byte0 = tmp[i]; // 取第i个字节
                str[k++] = hexDigits[byte0 >>> 4 & 0xf]; // 取字节中高4位的数字转换,>>>为逻辑右移，将符号位一起右移
                str[k++] = hexDigits[byte0 & 0xf]; // 取字节中低4位的数字转换
            }
            s = new String(str); // 换后的结果转换为字符串
        } catch (Exception e) {
            log.error("MD5 加密 [error]", e);
        }
        return s;
    }

    /**
     * @param text 明文
     * @param key 密钥
     * @return 密文
     */
    // 带秘钥加密
    public static String md5(String text, String key) throws Exception {
        // 加密后的字符串
        String md5str = DigestUtils.md5Hex(text + key);
        System.out.println("MD5加密后的字符串为:" + md5str);
        return md5str;
    }

    // 不带秘钥加密
    public static String md52(String text) throws Exception {
        // 加密后的字符串
        String md5str = DigestUtils.md5Hex(text);
        System.out.println("MD52加密后的字符串为:" + md5str + "\t长度：" + md5str.length());
        return md5str;
    }

    /**
     * MD5验证方法
     *
     * @param text 明文
     * @param key 密钥
     * @param md5 密文
     */
    // 根据传入的密钥进行验证
    public static boolean verify(String text, String key, String md5) throws Exception {
        String md5str = md5(text, key);
        if (md5str.equalsIgnoreCase(md5)) {
            System.out.println("MD5验证通过");
            return true;
        }
        return false;
    }

    public static void main(String[] args) throws Exception {
        String s = "value";
        System.out.println(MD5.encrypt(s).toLowerCase());
        String s1 = md52(s);
        System.out.println(s1);
        System.out.println("s = " + s);
    }
}
