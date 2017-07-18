package com.xmd.cashier.pos;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.iboxpay.cashbox.minisdk.model.Config;
import com.shidou.commonlibrary.helper.XLogger;
import com.xmd.m.network.OkHttpUtil;
import com.xmd.cashier.dal.net.RequestConstant;
import com.xmd.cashier.dal.net.response.StringResult;
import com.xmd.cashier.dal.sp.SPManager;
import com.xmd.cashier.manager.AccountManager;

import java.util.Map;
import java.util.TreeMap;

import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by heyangya on 16-10-17.
 */

public class SignTool {
    public static final String TRADE_APP_CODE_KEY = "appCode";
    public static final String TRADE_AMOUNT_KEY = "transAmount";
    public static final String OUT_TRADE_NO_KEY = "outTradeNo";
    public static final String TRADE_NO_KEY = "tradeNo";
    public static final String PARTNER_ID = "partnerId";
    public static final String PARTNER_USER_ID = "partnerUserId";
    public static final String IBOXPAY_MERCHANT_NO = "iboxMchtNo";
    public static final String URL_SIGN = "/v2/manager/pospay/sign/get";

    public static String getSign(Config config, String outTradeNo, String cbTradeNo, String amount, Map<String, String> attachMap) throws RuntimeException {
        TreeMap<String, String> treeMap = new TreeMap<>();
        treeMap.put(TRADE_APP_CODE_KEY, config.getAppCode());
        treeMap.put(TRADE_AMOUNT_KEY, amount);
        treeMap.put(OUT_TRADE_NO_KEY, outTradeNo);
        treeMap.put(TRADE_NO_KEY, cbTradeNo);
        treeMap.put(PARTNER_USER_ID, config.getPartnerUserId());
        treeMap.put(PARTNER_ID, config.getPartnerId());
        treeMap.put(IBOXPAY_MERCHANT_NO, config.getIboxMchtNo());
        treeMap.putAll(attachMap);

        FormBody.Builder builder = new FormBody.Builder();
        for (String key : treeMap.keySet()) {
            if (!TextUtils.isEmpty(treeMap.get(key))) {
                builder.add(key, treeMap.get(key));
            }
        }
        builder.add(RequestConstant.KEY_TOKEN, AccountManager.getInstance().getToken());
        builder.add(RequestConstant.KEY_SIGN, RequestConstant.DEFAULT_SIGN_VALUE);
        builder.add("signType", "MD5");

        Request request = new Request.Builder()
                .url("http://" + SPManager.getInstance().getSpaServerAddress() + RequestConstant.SPA_SERVICE_BASE + URL_SIGN)
                .post(builder.build())
                .build();
        try {
            Response response = OkHttpUtil.getInstance().getClient().newCall(request).execute();
            String responseString = response.body().string();
            StringResult result = new Gson().fromJson(responseString, StringResult.class);
            if (result.getRespData() != null) {
                XLogger.i("sign result:" + result.getRespData());
                return result.getRespData();
            } else {
                XLogger.e("sign failed: respData is null!!");
            }
        } catch (Exception e) {
            XLogger.e("sign failed:" + e.getLocalizedMessage());
        }
        throw new RuntimeException("获取签名信息失败");
    }
}
