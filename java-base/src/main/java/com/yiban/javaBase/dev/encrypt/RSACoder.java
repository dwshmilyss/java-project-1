package com.yiban.javaBase.dev.encrypt;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Cipher;

import com.yiban.javaBase.dev.tools.MyTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 概述: 1、RSA是基于大数因子分解难题。目前各种主流计算机语言都支持RSA算法的实现 2、java6支持RSA算法
 * 3、RSA算法可以用于数据加密和数字签名 4、RSA算法相对于DES/AES等对称加密算法，他的速度要慢的多
 * 5、总原则：公钥加密，私钥解密/私钥加密，公钥解密
 * 
 * 模型分析: RSA算法构建密钥对简单的很，这里我们还是以甲乙双方发送数据为模型 1、甲方在本地构建密钥对（公钥+私钥），并将公钥公布给乙方
 * 2、甲方将数据用私钥进行加密，发送给乙方 3、乙方用甲方提供的公钥对数据进行解密
 * 
 * 如果乙方向传送数据给甲方： 4、乙方用公钥对数据进行加密，然后传送给甲方 5、甲方用私钥对数据进行解密
 * 
 * 非对称加密算法RSA算法组件 非对称算法一般是用来传送对称加密算法的密钥来使用的，相对于DH算法，RSA算法只需要一方构造密钥，不需要
 * 大费周章的构造各自本地的密钥对了。DH算法只能算法非对称算法的底层实现。而RSA算法算法实现起来较为简单
 * 
 * 注意: 加密数据不能大于53字节
 * 
 * @author junqing.cao
 * @date 2013-4-23
 */
public class RSACoder {
	private static final Logger log = LoggerFactory.getLogger(RSACoder.class);

	private static RSACoder instance;

	// 非对称密钥算法
	public static final String KEY_ALGORITHM = "RSA";
	/*
	 * RSA密钥长度，RSA算法的默认密钥长度是1024 密钥长度必须是64的倍数，在512到65536位之间
	 */
	private final int KEY_SIZE = 512;
	// 公钥
	private final String public_key;
	// 私钥
	private String private_key;
	// 密钥对
	private Map<String, Object> keyMap;

	static {

	}

	/**
	 * 私有构造函数 禁止无参数构造
	 * 
	 * @throws Exception
	 */
	private RSACoder() throws Exception {
		this.private_key = MyTools.getPropertyVal("props/keys.properties",
				"PRIVATE_KEY");
		this.public_key = MyTools.getPropertyVal("props/keys.properties",
				"PUBLIC_KEY");
		keyMap = initKey();
	}

	/**
	 * 获取银行实例
	 * 
	 * @return
	 */
	public static synchronized RSACoder getInstance() {
		if (instance == null) {
			try {
				instance = new RSACoder();
			} catch (Exception e) {
				log.error("初始化密钥出错", e);
			}
		}
		return instance;
	}

	/**
	 * 构造函数
	 * 
	 * @param publicKey
	 */
	public RSACoder(String publicKey) {
		this.public_key = publicKey;
		try {
			keyMap = initKey();
		} catch (Exception e) {
			log.error("初始化密钥失败", e);
		}
	}

	/**
	 * 初始化密钥对
	 * 
	 * @return Map 甲方密钥的Map
	 * */
	private Map<String, Object> initKey() throws Exception {
		// 实例化密钥生成器
		KeyPairGenerator keyPairGenerator = KeyPairGenerator
				.getInstance(KEY_ALGORITHM);
		// 初始化密钥生成器
		keyPairGenerator.initialize(KEY_SIZE);
		// 生成密钥对
		KeyPair keyPair = keyPairGenerator.generateKeyPair();
		// 甲方公钥
		RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
		// 甲方私钥
		RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
		// 将密钥存储在map中
		Map<String, Object> keyMap = new HashMap<String, Object>();
		keyMap.put(public_key, publicKey);
		keyMap.put(private_key, privateKey);
		return keyMap;
	}

	/**
	 * 取得私钥
	 * 
	 * @return byte[]:私钥
	 * */
	public byte[] getPrivateKey() {
		Key key = (Key) keyMap.get(private_key);
		return key.getEncoded();
	}

	/**
	 * 取得公钥
	 * 
	 * @return byte[]:公钥
	 * */
	public byte[] getPublicKey() {
		Key key = (Key) keyMap.get(public_key);
		return key.getEncoded();
	}

	/**
	 * 私钥加密
	 * 
	 * @param data
	 *            :待加密数据
	 * @param key
	 *            :密钥
	 * @return byte[]:加密数据
	 * */
	public static byte[] encryptByPrivateKey(byte[] data, byte[] key)
			throws Exception {
		// 取得私钥
		PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(key);
		KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
		// 生成私钥
		PrivateKey privateKey = keyFactory.generatePrivate(pkcs8KeySpec);
		// 数据加密
		// Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
		Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
		cipher.init(Cipher.ENCRYPT_MODE, privateKey);
		return cipher.doFinal(data);
	}

	/**
	 * 公钥加密
	 * 
	 * @param data
	 *            :待加密数据
	 * @param key
	 *            :密钥
	 * @return byte[]:加密数据
	 * */
	public static byte[] encryptByPublicKey(byte[] data, byte[] key)
			throws Exception {
		// 实例化密钥工厂
		KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
		// 初始化公钥
		// 密钥材料转换
		X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(key);
		// 产生公钥
		PublicKey pubKey = keyFactory.generatePublic(x509KeySpec);

		// 数据加密
		// Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
		Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
		cipher.init(Cipher.ENCRYPT_MODE, pubKey);
		return cipher.doFinal(data);
	}

	/**
	 * 私钥解密
	 * 
	 * @param data
	 *            :待解密数据
	 * @param key
	 *            :密钥
	 * @return byte[] 解密数据
	 * */
	public static byte[] decryptByPrivateKey(byte[] data, byte[] key)
			throws Exception {
		// 取得私钥
		PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(key);
		KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
		// 生成私钥
		PrivateKey privateKey = keyFactory.generatePrivate(pkcs8KeySpec);
		// 数据解密
		// Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
		Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
		cipher.init(Cipher.DECRYPT_MODE, privateKey);
		return cipher.doFinal(data);
	}

	/**
	 * 公钥解密
	 * 
	 * @param data
	 *            :待解密数据
	 * @param key
	 *            :密钥
	 * @return byte[]:解密数据
	 * */
	public static byte[] decryptByPublicKey(byte[] data, byte[] key)
			throws Exception {
		// 实例化密钥工厂
		KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
		// 初始化公钥
		// 密钥材料转换
		X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(key);
		// 产生公钥
		PublicKey pubKey = keyFactory.generatePublic(x509KeySpec);
		// 数据解密
		// Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
		Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
		cipher.init(Cipher.DECRYPT_MODE, pubKey);
		return cipher.doFinal(data);
	}

	/**
	 * 保存密钥文件
	 * 
	 * @param address
	 * @throws Exception
	 */
	public void createPublicKeyFile(String address) throws Exception {
		// 保存公匙
		File file = new File(address + "/public_key.dat");
		if (!file.getParentFile().exists()) {
			file.getParentFile().mkdirs();
		}
		BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(
				new FileOutputStream(file));
		bufferedOutputStream.write(getPublicKey());
		bufferedOutputStream.close();
	}

	public void createPrivateKeyFile(String address) throws Exception {
		// 保存公匙
		File file = new File(address + "/private_key.dat");
		if (!file.getParentFile().exists()) {
			file.getParentFile().mkdirs();
		}
		BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(
				new FileOutputStream(file));
		bufferedOutputStream.write(getPrivateKey());
		bufferedOutputStream.close();
	}
}
