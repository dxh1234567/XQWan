package com.jj.base.utils;

import android.annotation.SuppressLint;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * 加解密工具
 * <p>
 * MD5
 * <p>
 * {@link #md5(String, String)} 字符串加密
 * <p>
 * <p>
 * {@link #md5(File, int)} 文件加密
 * <p>
 * <p>
 * {@link #md5(String)} 盐值加密
 * <p>
 * <p>
 * {@link #md5(String, int)} 重复加密
 * <p>
 * <p>
 * Base64
 * <p>
 * {@link #base64EncodeStr(String)} 字符串加密
 * <p>
 * <p>
 * {@link #base64DecodedStr(String)} 字符串解密
 * <p>
 * <p>
 * {@link #base64EncodeFile(File)} 文件加密
 * <p>
 * <p>
 * {@link #base64DecodedFile(String, String)} 文件解密
 * <p>
 * <p>
 * AES
 * <p>
 * {@link #aes(String, String, int)} 加解密
 * <p>
 * <p>
 * DES
 * <p>
 * {@link #des(String, String, int)} 加解密
 * <p>
 * <p>
 * SHA
 * <p>
 * {@link #sha(String, String)} 字符串加密
 * <p>
 * <p>
 * RSA
 * <p>
 * {@link #genKeyPair()} 生成密钥对
 * <p>
 * <p>
 * {@link #sign(byte[], String)} 生成数字签名
 * <p>
 * <p>
 * {@link #verify(byte[], String, String)} 校验数字签名
 * <p>
 * <p>
 * {@link #getKey(Map, boolean)} 密钥获取
 * <p>
 * <p>
 * {@link #rsa(byte[], String, int)} 密钥加解密
 *
 * @author gzejia 978862664@qq.com
 */
public class EncryptUtil {


    public static final String KEY = "1234567890123456";

    private static final String TAG = "EncryptUtil";

    private EncryptUtil() {
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    /**
     * @param buf 2进制
     * @return 转换成16进制
     */
    private static String parseByte2HexStr(byte buf[]) {
        StringBuilder sb = new StringBuilder();
        for (byte b : buf) {
            String hex = Integer.toHexString(b & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            sb.append(hex.toUpperCase());
        }
        return sb.toString();
    }

    /**
     * @param hexStr 16进制
     * @return 转换为二进制
     */
    private static byte[] parseHexStr2Byte(String hexStr) {
        if (hexStr.length() < 1) return null;
        byte[] result = new byte[hexStr.length() / 2];

        for (int i = 0; i < hexStr.length() / 2; i++) {
            int high = Integer.parseInt(hexStr.substring(i * 2, i * 2 + 1), 16);
            int low = Integer.parseInt(hexStr.substring(i * 2 + 1, i * 2 + 2), 16);
            result[i] = (byte) (high * 16 + low);
        }
        return result;
    }

    /**
     * @param string 文本内容
     * @param slat   盐值key
     * @return 文本加密结果
     */
    public static String md5(String string, String slat) {
        if (TextUtils.isEmpty(string)) return "";

        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            byte[] bytes = md5.digest((string + slat).getBytes());
            String result = "";
            for (byte b : bytes) {
                String temp = Integer.toHexString(b & 0xff);
                if (temp.length() == 1) {
                    temp = "0" + temp;
                }
                result += temp;
            }
            return result;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static final int MD5_TYPE_IO = 0;
    public static final int MD5_TYPE_NIO = 1;

    /**
     * @param file  文件内容
     * @param style {@link #MD5_TYPE_NIO} NIO文件MD5加密； {@link #MD5_TYPE_IO} IO文件MD5加密
     * @return 文件加密结果
     */
    public static String md5(File file, int style) {
        if (file == null || !file.isFile() || !file.exists()) return "";

        FileInputStream in = null;
        String result = "";
        byte buffer[] = new byte[8192];
        int len;

        try {
            in = new FileInputStream(file);
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            byte[] bytes = md5.digest();

            if (style == MD5_TYPE_IO) {
                while ((len = in.read(buffer)) != -1) {
                    md5.update(buffer, 0, len);
                }
            } else {
                MappedByteBuffer byteBuffer = in.getChannel().map(
                        FileChannel.MapMode.READ_ONLY, 0, file.length());
                md5.update(byteBuffer);
            }

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

    /**
     * @param string 文本内容
     * @param times  重复加密次数
     * @return 多次加密结果
     */
    public static String md5(String string, int times) {
        if (TextUtils.isEmpty(string)) return "";

        String md5 = md5(string);
        for (int i = 0; i < times; i++) md5 = md5(md5);
        return md5;
    }

    /**
     * @return 盐值加密结果
     * @see #md5(String, String)
     */
    public static String md5(String string) {
        return TextUtils.isEmpty(string) ? "" : md5(string, "");
    }

    /**
     * @param string md5加密字符串
     * @return 解密结果
     */
    public static String md5Decoded(String string) {
        char[] a = string.toCharArray();
        for (int i = 0; i < a.length; i++) {
            a[i] = (char) (a[i] ^ 't');
        }
        return new String(a);
    }

    /**
     * @param string md5加密字符串
     * @param times  md5重复加密次数
     * @return 解密结果
     */
    public static String md5Decoded(String string, int times) {
        if (TextUtils.isEmpty(string)) return "";

        String md5Decoded = md5Decoded(string);
        for (int i = 0; i < times; i++) md5Decoded = md5Decoded(string);
        return md5Decoded;
    }

    /**
     * @param string 文本内容
     * @return base64加密结果
     */
    public static String base64EncodeStr(String string) {
        if (TextUtils.isEmpty(string)) return "";
        return Base64.encodeToString(string.getBytes(), Base64.DEFAULT);
    }

    /**
     * @param string 文本内容
     * @return base64解密结果
     */
    public static String base64DecodedStr(String string) {
        if (TextUtils.isEmpty(string)) return "";
        return new String(Base64.decode(string, Base64.DEFAULT));
    }

    /**
     * @param file 文件
     * @return base64加密结果
     */
    public static String base64EncodeFile(File file) {
        if (null == file) return "";

        try {
            FileInputStream inputFile = new FileInputStream(file);
            byte[] buffer = new byte[(int) file.length()];
            inputFile.read(buffer);
            inputFile.close();
            return Base64.encodeToString(buffer, Base64.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * @param filePath 文件存储路径
     * @param code     文件编码
     * @return base64解密结果
     */
    public static File base64DecodedFile(String filePath, String code) {
        if (TextUtils.isEmpty(filePath) || TextUtils.isEmpty(code)) {
            Log.w(TAG, "File path or code not null");
            return null;
        }

        File desFile = new File(filePath);
        try {
            byte[] decodeBytes = Base64.decode(code.getBytes(), Base64.DEFAULT);
            FileOutputStream fos = new FileOutputStream(desFile);
            fos.write(decodeBytes);
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return desFile;
    }

    /**
     * @param content  加密/解密内容
     * @param password 加密/解密密钥
     * @param type     {@link Cipher} ENCRYPT_MODE_指定加密； ENCRYPT_MODE_指定解密
     * @return 加密/解密结果
     */
    public static String aes(String content, String password, int type) {
        try {
            KeyGenerator generator = KeyGenerator.getInstance("AES");
            @SuppressLint("DeletedProvider") SecureRandom secureRandom =
                    SecureRandom.getInstance("SHA1PRNG", "Crypto");
            secureRandom.setSeed(password.getBytes());
            generator.init(128, secureRandom);
            SecretKey secretKey = generator.generateKey();
            byte[] enCodeFormat = secretKey.getEncoded();
            SecretKeySpec key = new SecretKeySpec(enCodeFormat, "AES");
            @SuppressLint("GetInstance") Cipher cipher = Cipher.getInstance("AES");
            cipher.init(type, key);

            if (type == Cipher.ENCRYPT_MODE) {
                byte[] byteContent = content.getBytes("utf-8");
                return parseByte2HexStr(cipher.doFinal(byteContent));
            } else {
                byte[] byteContent = parseHexStr2Byte(content);
                return new String(cipher.doFinal(byteContent));
            }
        } catch (NoSuchAlgorithmException | BadPaddingException | IllegalBlockSizeException |
                UnsupportedEncodingException | InvalidKeyException | NoSuchPaddingException |
                NoSuchProviderException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @param content  加密/解密内容
     * @param password 加密/解密密钥
     * @param type     {@link Cipher} ENCRYPT_MODE_指定加密； ENCRYPT_MODE_指定解密
     * @return 加密/解密结果
     */
    public static String des(String content, String password, int type) {
        try {
            SecureRandom random = new SecureRandom();
            DESKeySpec desKey = new DESKeySpec(password.getBytes());
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            @SuppressLint("GetInstance") Cipher cipher = Cipher.getInstance("DES");
            cipher.init(type, keyFactory.generateSecret(desKey), random);

            if (type == Cipher.ENCRYPT_MODE) {
                byte[] byteContent = content.getBytes("utf-8");
                return parseByte2HexStr(cipher.doFinal(byteContent));
            } else {
                byte[] byteContent = parseHexStr2Byte(content);
                return new String(cipher.doFinal(byteContent));
            }
        } catch (NoSuchAlgorithmException | BadPaddingException | IllegalBlockSizeException |
                UnsupportedEncodingException | InvalidKeyException | NoSuchPaddingException |
                InvalidKeySpecException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String desEncrypt(String content) {
        return des(content, KEY, Cipher.ENCRYPT_MODE);
    }

    public static String desDecrypt(String content) {
        return des(content, KEY, Cipher.DECRYPT_MODE);
    }

    public final static String SHA224 = "sha-224";
    public final static String SHA256 = "sha-256";
    public final static String SHA384 = "sha-384";
    public final static String SHA512 = "sha-512";

    /**
     * @param string 加密内容
     * @param type   加密类型 {@link #SHA224},{@link #SHA256},{@link #SHA384},{@link #SHA512}
     * @return SHA加密结果
     */
    public static String sha(String string, String type) {
        if (TextUtils.isEmpty(string)) return "";
        if (TextUtils.isEmpty(type)) type = SHA256;

        try {
            MessageDigest md5 = MessageDigest.getInstance(type);
            byte[] bytes = md5.digest((string).getBytes());
            String result = "";
            for (byte b : bytes) {
                String temp = Integer.toHexString(b & 0xff);
                if (temp.length() == 1) {
                    temp = "0" + temp;
                }
                result += temp;
            }
            return result;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * @return 随机生成密钥对(公钥和私钥), 客户端公钥加密，服务器私钥解密
     * @throws Exception
     */
    public static Map genKeyPair() throws Exception {
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA", "BC");
        keyPairGen.initialize(1024);

        KeyPair keyPair = keyPairGen.generateKeyPair();
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();

        Map keyMap = new HashMap<>(2);
        keyMap.put("RSAPublicKey", publicKey);
        keyMap.put("RSAPrivateKey", privateKey);
        return keyMap;
    }

    /**
     * @param keyMap      密钥对
     * @param isPublicKey true：获取公钥
     *                    false：获取私钥
     * @return 获取密钥
     */
    public static String getKey(Map keyMap, boolean isPublicKey) {
        Key key = (Key) keyMap.get(isPublicKey ? "RSAPublicKey" : "RSAPrivateKey");
        return new String(Base64.encode(key.getEncoded(), Base64.DEFAULT));
    }

    /**
     * @param data       已加密数据
     * @param privateKey 私钥(BASE64编码)
     * @return 生成数字签名
     * @throws Exception
     */
    public static String sign(byte[] data, String privateKey) throws Exception {
        byte[] keyBytes = Base64.decode(privateKey.getBytes(), Base64.DEFAULT);
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA", "BC");
        PrivateKey privateK = keyFactory.generatePrivate(pkcs8KeySpec);

        Signature signature = Signature.getInstance("MD5withRSA");
        signature.initSign(privateK);
        signature.update(data);
        return new String(Base64.encode(signature.sign(), Base64.DEFAULT));
    }

    /**
     * @param data      已加密数据
     * @param publicKey 公钥(BASE64编码)
     * @param sign      数字签名
     * @return 校验数字签名
     * @throws Exception
     */
    public static boolean verify(byte[] data, String publicKey, String sign) throws Exception {
        byte[] keyBytes = Base64.decode(publicKey.getBytes(), Base64.DEFAULT);

        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA", "BC");
        PublicKey publicK = keyFactory.generatePublic(keySpec);

        Signature signature = Signature.getInstance("MD5withRSA");
        signature.initVerify(publicK);
        signature.update(data);
        return signature.verify(Base64.decode(sign.getBytes(), Base64.DEFAULT));
    }

    public static final int RSA_PUBLIC_ENCRYPT = 0;
    public static final int RSA_PUBLIC_DECRYPT = 1;
    public static final int RSA_PRIVATE_ENCRYPT = 2;
    public static final int RSA_PRIVATE_DECRYPT = 3;

    /**
     * @param data   源数据
     * @param string 密钥(BASE64编码)
     * @param type   {@link #RSA_PUBLIC_ENCRYPT} 公钥加密；{@link #RSA_PUBLIC_DECRYPT} 公钥解密；{@link #RSA_PRIVATE_ENCRYPT} 私钥加密；{@link #RSA_PRIVATE_DECRYPT} 私钥解密；
     * @return 一般情况下，公钥加密私钥解密
     * @throws Exception
     */
    public static byte[] rsa(byte[] data, String string, int type)
            throws Exception {
        byte[] keyBytes = Base64.decode(string, Base64.DEFAULT);

        Key key;
        KeyFactory keyFactory = KeyFactory.getInstance("RSA", "BC");
        if (type == RSA_PUBLIC_ENCRYPT || type == RSA_PUBLIC_DECRYPT) {
            X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
            key = keyFactory.generatePublic(x509KeySpec);
        } else {
            PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
            key = keyFactory.generatePrivate(pkcs8KeySpec);
        }


        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        int inputLen = data.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] cache;
        int i = 0;


        if (type == RSA_PUBLIC_ENCRYPT || type == RSA_PRIVATE_ENCRYPT) {
            cipher.init(Cipher.ENCRYPT_MODE, key);

            while (inputLen - offSet > 0) {
                cache = cipher.doFinal(data, offSet, inputLen - offSet > 117 ? 117 : inputLen - offSet);
                out.write(cache, 0, cache.length);
                i++;
                offSet = i * 117;
            }
        } else {
            cipher.init(Cipher.DECRYPT_MODE, key);

            while (inputLen - offSet > 0) {
                if (inputLen - offSet > 128) {
                    cache = cipher.doFinal(data, offSet, 128);
                } else {
                    cache = cipher.doFinal(data, offSet, inputLen - offSet);
                }
                out.write(cache, 0, cache.length);
                i++;
                offSet = i * 128;
            }
        }
        byte[] result = out.toByteArray();
        out.close();
        return result;
    }
}