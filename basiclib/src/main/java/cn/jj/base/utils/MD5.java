package cn.jj.base.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author LY MD5加密
 */
public class MD5 {

    // MD5加密�?2�?
    public static String getMD5(String str) {

        try {
            byte[] source = str.getBytes();
            // 用来将字节转换成 16 进制表示的字�?
            char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8',
                    '9', 'a', 'b', 'c', 'd', 'e', 'f'};
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(source);
            byte tmp[] = md.digest(); // MD5 的计算结果是�?�� 128 位的长整数，
            // 用字节表示就�?16 个字�?
            char s[] = new char[16 * 2]; // 每个字节�?16 进制表示的话，使用两个字符，
            // �?��表示�?16 进制�?�� 32 个字�?
            int k = 0; // 表示转换结果中对应的字符位置
            for (int i = 0; i < 16; i++) { // 从第�?��字节�?��，对 MD5 的每�?��字节
                // 转换�?16 进制字符的转�?
                byte byte0 = tmp[i]; // 取第 i 个字�?
                s[k++] = hexDigits[byte0 >>> 4 & 0xf]; // 取字节中�?4 位的数字转换,
                // >>> 为�?辑右移，将符号位�?��右移
                s[k++] = hexDigits[byte0 & 0xf]; // 取字节中�?4 位的数字转换
            }
            String data = new String(s); // 换后的结果转换为字符�?
            return data;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /*
     * // MD5加密�?2�? public static String getMD52(String str) { try {
     * MessageDigest md5 = MessageDigest.getApplication("MD5"); char[] charArray =
     * str.toCharArray(); byte[] byteArray = new byte[charArray.length]; for
     * (int i = 0; i < charArray.length; i++) { byteArray[i] = (byte)
     * charArray[i]; } byte[] md5Bytes = md5.digest(byteArray); StringBuffer
     * hexValue = new StringBuffer(); for (int i = 0; i < md5Bytes.length; i++)
     * { int val = ((int) md5Bytes[i]) & 0xff; if (val < 16) {
     * hexValue.append("0"); } hexValue.append(Integer.toHexString(val)); }
     * return hexValue.toString(); } catch (Exception e) { e.printStackTrace();
     * } return null; }
     */

    /**
     * 将字符串转成MD5值
     *
     * @param string
     * @return
     */
    public static String stringToMD5(String string) {
        byte[] hash;
        try {
            hash = MessageDigest.getInstance("MD5").digest(
                    string.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
        StringBuilder hex = new StringBuilder(hash.length * 2);
        for (byte b : hash) {
            if ((b & 0xFF) < 0x10)
                hex.append("0");
            hex.append(Integer.toHexString(b & 0xFF));
        }
        return hex.toString().substring(8, 16);// 8位
    }

    // 计算文件的 MD5 值
    public static String getMD5(File file) {
        if (file == null || !file.isFile() || !file.exists()) {
            return "";
        }
        FileInputStream in = null;
        String result = "";
        byte buffer[] = new byte[8192];
        int len;
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            in = new FileInputStream(file);
            while ((len = in.read(buffer)) != -1) {
                md5.update(buffer, 0, len);
            }
            byte[] bytes = md5.digest();

            for (byte b : bytes) {
                String temp = Integer.toHexString(b & 0xff);
                if (temp.length() == 1) {
                    temp = "0" + temp;
                }
                result += temp;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != in) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }
}
