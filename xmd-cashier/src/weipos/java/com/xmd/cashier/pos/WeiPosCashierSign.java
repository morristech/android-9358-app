package com.xmd.cashier.pos;

import android.text.TextUtils;

import com.shidou.commonlibrary.helper.XLogger;
import com.xmd.cashier.BuildConfig;
import com.xmd.cashier.dal.net.RequestConstant;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA
 * User: Sirius
 * Date: 2015/6/19
 * Time: 15:20
 */
public class WeiPosCashierSign {
    public static final String PACKAGE_INVOKE_WANGPOS_CASHIER = "com.wangpos.by.cashier3";
    public static final String PACKAGE_INVOIKE_WEIPASS = "cn.weipass.cashier";

    public static final String InvokeCashier_APPID = "57b2d92f91b9b20394299efb";
    public static final String InvokeCashier_BPID = "57b2d818fa0bab6e91e24ae9";
    public static final String InvokeCashier_KEY = "OB4PHvGgRei6wh42d0UASmvNGZNQe07z";

    private static final String Tag = "WeiPosCashierSign";

    private static final String SignType = "MD5";
    private static final String inputCharset = "UTF-8";

    public static byte[] sign(String bpId, String invokeCashierKey, String channel,
                              String payType, String outTradeNo, String body,
                              String attach, String feeType, String totalFee,
                              String packageName, String classPath) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        XLogger.i("outTradeNo:" + outTradeNo + ",totalFee:" + totalFee + ",packageName:" + packageName + ",classpath:" + classPath);
        Map<String, String> dataMap = new HashMap<String, String>();
        dataMap.put("bp_id", bpId);
        dataMap.put("channel", channel);
        dataMap.put("payType", payType);
        dataMap.put("out_trade_no", outTradeNo);
        dataMap.put("body", body);
        if (!TextUtils.isEmpty(attach)) {
            dataMap.put("attach", attach);
        }
        dataMap.put("fee_type", feeType);
        dataMap.put("total_fee", totalFee);
        dataMap.put("input_charset", inputCharset);
        dataMap.put("notify_url", BuildConfig.WANG_POS_NOTIFY_HOST + RequestConstant.WANG_POS_NOTIFY_URL);
        dataMap.put("package", packageName);
        dataMap.put("classpath", classPath);

        String sign = getSign(invokeCashierKey, dataMap);
        dataMap.put("sign", sign);

        JSONObject json = new JSONObject(dataMap);
        return json.toString().getBytes(inputCharset);
    }

    private static String getSign(String invokeCashierKey, Map<String, String> dataMap) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        List<String> keyList = new ArrayList<String>(dataMap.keySet());
        Collections.sort(keyList);
        StringBuilder builder = new StringBuilder();
        for (String mapKey : keyList) {
            builder.append(mapKey).append("=").append(dataMap.get(mapKey)).append("&");
        }
        builder.append("key=").append(invokeCashierKey);
        MessageDigest md5 = MessageDigest.getInstance(SignType);
        md5.update(builder.toString().getBytes(inputCharset));
        byte[] md5Bytes = md5.digest();
        StringBuffer hexValue = new StringBuffer();
        for (int i = 0; i < md5Bytes.length; i++) {
            int val = ((int) md5Bytes[i]) & 0xff;
            if (val < 16) {
                hexValue.append("0");
            }
            hexValue.append(Integer.toHexString(val));
        }
        return hexValue.toString();
    }
}
