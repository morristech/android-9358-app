package com.xmd.m.notify.push;

/**
 * Created by sdcm on 15-11-30.
 */

import android.text.TextUtils;
import android.util.Base64;

import com.shidou.commonlibrary.helper.XLogger;

import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;


/**
 * @author wmj911
 * @ClassName: DESede
 * @Description: 对称加密算法 3重des加密
 * @date 2015年5月30日 下午2:16:23
 */
public final class DESede {

    /**
     * 加解密使用的字符集
     */
    private static final String DESEDE_CHARSET_NAME = "UTF-8";

    /**
     * 加密算法
     */
    private static final String DESEDE = "DESede";

    /**
     * 算法的转换名称
     */
    private static final String DES_CBC = "DESede/CBC/PKCS5Padding";

    // 默认密钥
    public static String DEFAULT_KEY = "240262447423713749922240";
    //默认向量值
    public static String DEFAULT_DESEDE_IV = "12345678";

    /**
     * 使用3Des算法进行明文加密
     *
     * @param key    24个字符的密钥(3个des密钥)
     * @param ivByte 8字符的随机向量
     * @param value  需要进行加密的原明文字节数组
     * @return 加密后的密文字节数组，如果加密失败，返回null
     */
    public static byte[] encrypt(byte[] key, byte[] ivByte, byte[] value) {
        try {
            SecureRandom sr = new SecureRandom();
            SecretKey securekey = new SecretKeySpec(key, DESEDE);
            IvParameterSpec iv = new IvParameterSpec(ivByte);
            Cipher cipher = Cipher.getInstance(DES_CBC);
            cipher.init(Cipher.ENCRYPT_MODE, securekey, iv, sr);
            return cipher.doFinal(value);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @param decryptPwd 明文字符串
     * @return
     * @author wmj911
     * @Description: 对字符串加密
     */
    public static String encrypt(String decryptPwd) {
        String sPwd = "";
        try {
            if (!TextUtils.isEmpty(decryptPwd)) {
                byte[] encrypt = DESede.encrypt(DEFAULT_KEY.getBytes(), DEFAULT_DESEDE_IV.getBytes(), decryptPwd.getBytes());
                sPwd = DESede.base64Encode(encrypt);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return sPwd;
    }

    /**
     * @param encryptPwd 密文
     * @return
     * @author wmj911
     * @Description: 对密文进行解密
     */
    public static String decrypt(String encryptPwd) {
        String decryptPwd = "";
        try {
            if (!TextUtils.isEmpty(encryptPwd)) {
                byte[] decrypt = DESede.base64DecodeToBytes(encryptPwd);
                decrypt = DESede.decrypt(DEFAULT_KEY.getBytes(),
                        DEFAULT_DESEDE_IV.getBytes(), decrypt);
                decryptPwd = new String(decrypt);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return decryptPwd;
    }


    /**
     * 使用3Des算法进行密文解密
     *
     * @param key    24个字符的密钥(3个des密钥)
     * @param ivByte 8字符的随机向量
     * @param value  需要进行解密的密文字节数组
     * @return 返回解密后的明文字节数组, 如果解密失败，返回null
     */
    public static byte[] decrypt(byte[] key, byte[] ivByte, byte[] value) {
        try {
            SecureRandom sr = new SecureRandom();
            SecretKey securekey = new SecretKeySpec(key, DESEDE);
            IvParameterSpec iv = new IvParameterSpec(ivByte);
            Cipher cipher = Cipher.getInstance(DES_CBC);
            cipher.init(Cipher.DECRYPT_MODE, securekey, iv, sr);
            return cipher.doFinal(value);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 进行Base64编码
     *
     * @param src 原字符串
     * @return 返回经过Base64编码后的字符串
     */
    public static String base64Encode(String src) throws Exception {

        return Base64.encodeToString(src.getBytes(DESEDE_CHARSET_NAME), Base64.DEFAULT);
    }

    /**
     * 进行Base64编码
     *
     * @param buff 原字符串
     * @return 返回经过Base64编码后的字符串
     */
    public static String base64Encode(byte[] buff) throws Exception {
        return Base64.encodeToString(buff, Base64.DEFAULT);
    }

    /**
     * 进行Base64解码
     *
     * @param src 经Base64编码后的字符串
     * @return 返回解码后的字符串
     * @throws Exception
     */
    public static String base64Decode(String src) throws Exception {
        return new String(Base64.decode(src.getBytes(DESEDE_CHARSET_NAME), Base64.DEFAULT), DESEDE_CHARSET_NAME);
    }

    /**
     * 进行Base64解码
     *
     * @param src 经Base64编码后的字符串
     * @return 返回解码后的字符串
     * @throws Exception
     */
    public static byte[] base64DecodeToBytes(String src) throws Exception {

        return Base64.decode(src.getBytes(DESEDE_CHARSET_NAME), Base64.DEFAULT);
    }


    /**
     * 加密小工具
     *
     * @param clientId
     */
    public static String encrypt(String appID, String appSecret, String appKey, String masterSecret, String clientId, String telephone) {
        //AppID、AppSecret、telephone、AppKey、MasterSecret、clientId
        String decryptPwd = appID + appSecret + telephone + appKey + masterSecret + clientId;
        XLogger.v("加密前字符串:" + decryptPwd);
        String encryptPwd = encrypt(decryptPwd);
        XLogger.v("加密:" + encryptPwd);
        String decryptPwds = decrypt(encryptPwd);
        XLogger.v("解密后字符串:" + decryptPwds);
        return encrypt(decryptPwd);
    }


    /**
     * 加密小工具
     *
     * @param args
     */
    public static void main(String[] args) throws Exception {
        //AppID、AppSecret、telephone、AppKey、MasterSecret、clientId
        String appID = "WvUqksV0Ob6gUBKdGxOLk8";
        String appSecret = "nxA8wGbyYl5tCX6aDeH3i1";
        String appKey = "OPclY7PiR37dSgU8NDtWC";
        String masterSecret = "JEZC14IDw86NZIgxAn0et5";
        String telephone = "刘德华";
        String clientId = "1234567890";
        String decryptPwd = appID + appSecret + telephone + appKey + masterSecret + clientId;
        System.out.println("加密前字符串:" + decryptPwd);
        String encryptPwd = encrypt(decryptPwd);
        System.out.println("加密:" + encryptPwd);
        String decryptPwds = decrypt(encryptPwd);
        System.out.println("解密后字符串:" + decryptPwds);

    }
}

