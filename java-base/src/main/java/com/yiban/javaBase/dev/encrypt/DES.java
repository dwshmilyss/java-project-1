package com.yiban.javaBase.dev.encrypt;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DES {
    private static final Logger log = LoggerFactory.getLogger(DES.class);
    
    private static DES des;
    
    private SecretKey secretKey; //安全密钥
    
    private static final String ALGORITHM = "DES";
    
    private static final String DES_KEY = "livebytouch";
    
    private DESKeySpec desKeySpec;
    
    private DES() {
        SecretKeyFactory keyFactory = null;
        
        try {
            //从原始密钥数据创建DESKeySpec对象
            desKeySpec = new DESKeySpec(DES_KEY.getBytes());
            //创建一个密钥工厂，然后用它把DESKeySpec转换成Secret Key对象
            keyFactory = SecretKeyFactory.getInstance(ALGORITHM);
            secretKey = keyFactory.generateSecret(desKeySpec);
        } catch (Exception e) {
            log.error("DES 初始化[error]", e);
        }
    }
    
    public static synchronized DES getInstance() {
        if (des == null) {
            des = new DES();
        }
        return des;
    }
    
    /**
     * 加密
     * @param bytes
     * @return
     * @throws NoSuchPaddingException 
     * @throws NoSuchAlgorithmException 
     * @throws InvalidKeyException 
     * @throws BadPaddingException 
     * @throws IllegalBlockSizeException 
     */
    private byte[] encryptBytes(byte[] bytes) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        byte[] encryptedData = null;
        SecureRandom sr = null;
        
        // 产生一个可信任的随机数源
        sr = new SecureRandom();
        // Cipher对象实际完成加密操作
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        // 用密钥初始化Cipher对象
        cipher.init(Cipher.ENCRYPT_MODE, this.secretKey, sr);
        // 执行加密操作
        encryptedData = cipher.doFinal(bytes);
        return encryptedData;
    }
    
    /**
     * 解密
     * @param bytes
     * @return
     * @throws NoSuchPaddingException 
     * @throws NoSuchAlgorithmException 
     * @throws InvalidKeyException 
     * @throws BadPaddingException 
     * @throws IllegalBlockSizeException 
     */
    private byte[] dencryptBytes(byte[] bytes) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        byte[] dencryptedData = null;
        // 生成一个可信任的随机数源
        SecureRandom sr = null;
     
        sr = new SecureRandom();
        // Cipher对象实际完成解密操作
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        // 用密钥初始化Cipher对象
        cipher.init(Cipher.DECRYPT_MODE, this.secretKey, sr);
        dencryptedData = cipher.doFinal(bytes);
        
        return dencryptedData;
    }
    
    /**
     * 加密字符串
     * @param str
     * @return
     */
    public String encrypt(String str) {
        if (str != null && !"".equals(str)) {
            try {
                return Base64Coder.encodeBytes(this.encryptBytes(str.getBytes("UTF-8")));
            } catch (Exception e) {
                log.error("DES 加密[error]", e);
            }
        }        
        return null;
    }
    
    /**
     * 解密字符串
     * @param str
     * @return
     */
    public String dencrypt(String str) {
        if (str != null && !"".equals(str)) {
            try {
                return new String(this.dencryptBytes(Base64Coder.decodeStr(str)));
            } catch (Exception e) {
                log.error("DES 解密[error]", e);
            }
        }        
        return null;
    }
    
    public static void main(String[] args) {
		System.out.println(DES.getInstance().dencrypt("UjcQyMcnAtPuy6zzm3uLvF4pfjxvySns"));
	}
}
