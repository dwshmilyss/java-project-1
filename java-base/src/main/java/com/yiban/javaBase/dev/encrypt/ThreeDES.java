package com.yiban.javaBase.dev.encrypt;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ThreeDES {
    private static final Logger log = LoggerFactory.getLogger(ThreeDES.class);

    /*
     * 定义 加密算法,可用DES,DESede,Blowfish
     */
    private static final String ALGORITHM = "DESede";

    private static ThreeDES threeDES;

    private SecretKey secretKey; // 安全密钥

//    private static final String DES_KEY = "立佰趣收单大优惠";

    /*
     * 24字节密钥
     */
    private static final byte[] KEYBYTES = { -25, -85, -117, -28, -67, -80,
            -24, -74, -93, -26, -108, -74, -27, -115, -107, -27, -92, -89, -28,
            -68, -104, -26, -125, -96 };

    private ThreeDES() {
        try {
            secretKey = new SecretKeySpec(KEYBYTES, ALGORITHM);
        } catch (Exception e) {
            log.error("ThreeDES 初始化[error]", e);
        }
    }

    public static synchronized ThreeDES getInstance() {
        if (threeDES == null) {
            threeDES = new ThreeDES();
        }
        return threeDES;
    }

    /**
     * 加密字节
     * 
     * @param bytes
     * @return
     */
    private byte[] encryptBytes(byte[] bytes) {
        try {
            // 加密
            Cipher c1 = Cipher.getInstance(ALGORITHM);
            c1.init(Cipher.ENCRYPT_MODE, secretKey);
            return c1.doFinal(bytes);
        } catch (java.security.NoSuchAlgorithmException e1) {
            e1.printStackTrace();
        } catch (javax.crypto.NoSuchPaddingException e2) {
            e2.printStackTrace();
        } catch (Exception e3) {
            e3.printStackTrace();
        }
        return null;
    }

    /**
     * 解密字节
     * 
     * @param bytes
     * @return
     */
    private byte[] dencryptBytes(byte[] bytes) {
        try {
            // 解密
            Cipher c1 = Cipher.getInstance(ALGORITHM);
            c1.init(Cipher.DECRYPT_MODE, secretKey);
            return c1.doFinal(bytes);
        } catch (java.security.NoSuchAlgorithmException e1) {
            e1.printStackTrace();
        } catch (javax.crypto.NoSuchPaddingException e2) {
            e2.printStackTrace();
        } catch (Exception e3) {
            e3.printStackTrace();
        }
        return null;
    }

    /**
     * 加密字符串
     * 
     * @param str
     * @return
     */
    public String encrypt(String str) {
        if (str != null && !"".equals(str)) {
            try {
                return Base64Coder.encodeBytes(this.encryptBytes(str
                        .getBytes("UTF-8")));
            } catch (Exception e) {
                log.error("ThreeDES 加密[error]", e);
            }
        }
        return null;
    }

    /**
     * 解密字符串
     * 
     * @param str
     * @return
     */
    public String dencrypt(String str) {
        if (str != null && !"".equals(str)) {
            try {
                return new String(this.dencryptBytes(Base64Coder.decodeStr(str)));
            } catch (Exception e) {
                log.error("ThreeDES 解密[error]", e);
            }
        }
        return null;
    }

    // 转换成十六进制字符串
    /*
     * public static String byte2hex(byte[] b) { String hs = ""; String stmp =
     * "";
     * 
     * for (int n = 0; n < b.length; n++) { stmp =
     * (java.lang.Integer.toHexString(b[n] & 0XFF)); if (stmp.length() == 1) hs
     * = hs + "0" + stmp; else hs = hs + stmp; if (n < b.length - 1) hs = hs +
     * ":"; } return hs.toUpperCase(); }
     */
}
