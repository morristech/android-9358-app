
package com.shidou.commonlibrary.util;

import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class AESCipherUtils {
    /**
     * 加密函数
     * @param key 密钥，长度非128位时会做HASH处理成128位
     * @param src
     * @return
     * @throws Exception
     */
    public static String encrypt(String key, String src) throws Exception {
        byte[] rawKey;
        if (key.length() != 16) {
            rawKey = getRawKey(key.getBytes());
        } else {
            rawKey = key.getBytes();
        }
        byte[] result = encrypt(rawKey, src.getBytes());
        return toHex(result);
    }

    /**
     * 解密
     * @param key 密钥，长度非128位时会做HASH处理成128位
     * @param src
     * @return
     * @throws Exception
     */
    public static String encrypt(String key, byte[] src) throws Exception {
        byte[] rawKey;
        if (key.length() != 16) {
            rawKey = getRawKey(key.getBytes());
        } else {
            rawKey = key.getBytes();
        }
        byte[] result = encrypt(rawKey, src);
        return toHex(result);
    }

    public static String decrypt(String key, String encrypted) throws Exception {
        byte[] rawKey;
        if (key.length() != 16) {
            rawKey = getRawKey(key.getBytes());
        } else {
            rawKey = key.getBytes();
        }
        byte[] enc = toByte(encrypted);
        byte[] result = decrypt(rawKey, enc);
        return new String(result);
    }

    /**
     * 对密钥做HASH处理
     * @param seed
     * @return
     * @throws Exception
     */
    private static byte[] getRawKey(byte[] seed) throws Exception {
        KeyGenerator kgen = KeyGenerator.getInstance("AES");
        // SHA1PRNG 强随机种子算法, 要区别4.2以上版本的调用方法
        SecureRandom sr = null;
        if (android.os.Build.VERSION.SDK_INT >= 17) {
            sr = SecureRandom.getInstance("SHA1PRNG", "Crypto");
        } else {
            sr = SecureRandom.getInstance("SHA1PRNG");
        }
        sr.setSeed(seed);
        kgen.init(128, sr); //256 bits or 128 bits,192bits
        SecretKey skey = kgen.generateKey();
        byte[] raw = skey.getEncoded();

        String raws = toHex(raw);
        return raw;
    }


    private static byte[] encrypt(byte[] key, byte[] src) throws Exception {
        SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
        byte[] encrypted = cipher.doFinal(src);
        return encrypted;
    }

    private static byte[] decrypt(byte[] key, byte[] encrypted) throws Exception {
        SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, skeySpec);
        byte[] decrypted = cipher.doFinal(encrypted);
        return decrypted;
    }

    public static String toHex(String txt) {
        return toHex(txt.getBytes());
    }

    public static String fromHex(String hex) {
        return new String(toByte(hex));
    }

    public static byte[] toByte(String hexString) {
        int len = hexString.length() / 2;
        byte[] result = new byte[len];
        for (int i = 0; i < len; i++)
            result[i] = Integer.valueOf(hexString.substring(2 * i, 2 * i + 2), 16).byteValue();
        return result;
    }

    public static String toHex(byte[] buf) {
        if (buf == null)
            return "";
        StringBuffer result = new StringBuffer(2 * buf.length);
        for (int i = 0; i < buf.length; i++) {
            appendHex(result, buf[i]);
        }
        return result.toString();
    }

    private final static String HEX = "0123456789ABCDEF";

    private static void appendHex(StringBuffer sb, byte b) {
        sb.append(HEX.charAt((b >> 4) & 0x0f)).append(HEX.charAt(b & 0x0f));
    }
}