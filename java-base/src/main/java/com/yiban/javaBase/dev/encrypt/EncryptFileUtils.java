package com.yiban.javaBase.dev.encrypt;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;

/**
 * 文件加密工具类
 *
 * @auther WEI.DUAN
 * @date 2017/5/8
 * @website http://blog.csdn.net/dwshmilyss
 */
public class EncryptFileUtils {

    public static void main(String[] args) {
        System.out.println(EncryptFileUtils.class.getClassLoader().getResource("jdbc.properties").getPath());
//        File src = new File(EncryptFileUtils.class.getClassLoader().getResource("conf/jdbc.properties").getPath());
//        File dest = new File(EncryptFileUtils.class.getClassLoader().getResource("conf/jdbc1.properties").getPath());
//        System.out.println(MD5ToFile.getMD5(src,"MD5"));
//        System.out.println(MD5ToFile.getMD5(dest,"MD5"));
//        System.out.println(" =============== ");
//        System.out.println(MD5ToFile.getMD5(src,"SHA1"));
//        System.out.println(MD5ToFile.getMD5(dest,"SHA1"));
    }

    static class MD5ToFile {
        public final static char HEX_DIGITS[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

        public static String getMD5(File file, String strategy) {
            FileInputStream fis = null;
            try {
                MessageDigest md = MessageDigest.getInstance(strategy);
                fis = new FileInputStream(file);
                byte[] buffer = new byte[4096];
                int offset = -1;
                while ((offset = fis.read(buffer)) != -1) {
                    md.update(buffer, 0, offset);
                }
                byte[] b = md.digest();
//                return byteToHexString(b);
                return getFormattedText(b);
                // 16位加密
                // return buf.toString().substring(8, 24);
            } catch (Exception ex) {
                ex.printStackTrace();
                return null;
            } finally {
                try {
                    fis.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }

        private static String byteToHexString(byte[] tmp) {
            String s;
            // 用字节表示就是 16 个字节
            char str[] = new char[16 * 2]; // 每个字节用 16 进制表示的话，使用两个字符，
            // 所以表示成 16 进制需要 32 个字符
            int k = 0; // 表示转换结果中对应的字符位置
            for (int i = 0; i < 16; i++) { // 从第一个字节开始，对 MD5 的每一个字节
                // 转换成 16 进制字符的转换
                byte byte0 = tmp[i]; // 取第 i 个字节
                str[k++] = HEX_DIGITS[byte0 >>> 4 & 0xf]; // 取字节中高 4 位的数字转换,
                // >>> 为逻辑右移，将符号位一起右移
                str[k++] = HEX_DIGITS[byte0 & 0xf]; // 取字节中低 4 位的数字转换
            }
            s = new String(str); // 换后的结果转换为字符串
            return s;
        }

        private static String getFormattedText(byte[] bytes) {
            int len = bytes.length;
            StringBuilder buf = new StringBuilder(len * 2);
            // 把密文转换成十六进制的字符串形式
            for (int j = 0; j < len; j++) {
                buf.append(HEX_DIGITS[(bytes[j] >> 4) & 0x0f]);
                buf.append(HEX_DIGITS[bytes[j] & 0x0f]);
            }
            return buf.toString();
        }
    }
}
