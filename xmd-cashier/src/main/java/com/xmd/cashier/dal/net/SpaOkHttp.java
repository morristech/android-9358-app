package com.xmd.cashier.dal.net;

import com.google.gson.Gson;
import com.shidou.commonlibrary.helper.XLogger;
import com.shidou.commonlibrary.util.MD5Utils;
import com.xmd.cashier.dal.net.response.ReportTradeDataResult;
import com.xmd.cashier.dal.net.response.StringResult;
import com.xmd.cashier.dal.sp.SPManager;
import com.xmd.m.network.NetworkSubscriber;
import com.xmd.m.network.OkHttpUtil;

import java.io.IOException;
import java.util.Iterator;
import java.util.TreeMap;

import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by heyangya on 16-9-22.
 */

public class SpaOkHttp {
    public static Request checkAndSign(Request request) {
        if (request.body() != null && request.body() instanceof FormBody) {
            FormBody body = (FormBody) request.body();
            TreeMap<String, String> treeMap = new TreeMap<>();
            FormBody.Builder builder = new FormBody.Builder();
            boolean needSign = false;
            for (int i = 0; i < body.size(); i++) {
                if (body.name(i).equals(RequestConstant.KEY_SIGN)) {
                    //需要签名
                    needSign = true;
                    String now = String.valueOf(System.currentTimeMillis());
                    treeMap.put(RequestConstant.KEY_REQUEST_TIME + "=" + now + "&", null);
                    builder.add(RequestConstant.KEY_REQUEST_TIME, now);
                } else {
                    treeMap.put(body.name(i) + "=" + body.value(i) + "&", null);
                    builder.add(body.name(i), body.value(i));
                }
            }
            if (needSign) {
                String sign = calculateSignTreeMap(treeMap);
                builder.add(RequestConstant.KEY_SIGN, sign);
                Request.Builder requestBuilder = request.newBuilder();
                requestBuilder.method(request.method(), builder.build());
                request = requestBuilder.build();
            }
        }
        return request;
    }

    public static String calculateSignTreeMap(TreeMap<String, String> treeMap) {
        String result = "";
        Iterator iterator = treeMap.keySet().iterator();
        while (iterator.hasNext()) {
            result += iterator.next();
        }
        if (result.length() > 1) {
            result = result.substring(0, result.length() - 1);
        }
        result += RequestConstant.CLIENT_SECRET;
        return MD5Utils.MD5(result);
    }


    public static void reportTradeDataSync(FormBody formBody, final com.xmd.cashier.manager.Callback<ReportTradeDataResult> callback) {
        Request request = new Request.Builder()
                .url(SPManager.getInstance().getSpaServerAddress() + RequestConstant.URL_REPORT_TRADE_DATA)
                .post(formBody)
                .build();
        final NetworkSubscriber<ReportTradeDataResult> networkCallback = new NetworkSubscriber<ReportTradeDataResult>() {
            @Override
            public void onCallbackSuccess(ReportTradeDataResult result) {
                callback.onSuccess(result);
            }

            @Override
            public void onCallbackError(Throwable e) {
                callback.onError(e.getLocalizedMessage());
            }
        };

        try {
            Response response = OkHttpUtil.getInstance().getClient().newCall(request).execute();
            String body = response.body().string();
            XLogger.i("data report resp:" + body);
            ReportTradeDataResult result = new Gson().fromJson(body, ReportTradeDataResult.class);
            networkCallback.onNext(result);
        } catch (Exception e) {
            networkCallback.onError(e);
        }
    }

    public static byte[] getClubWXQrcode(String url) {
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();
        try {
            Response response = OkHttpUtil.getInstance().getClient().newCall(request).execute();
            return response.body().bytes();
        } catch (IOException e) {
            XLogger.e("getClubWXQrcode failed:" + e.getLocalizedMessage());
        }
        return null;
    }

    public static String getTradeQrcode(String token, String tradeNo) {
        FormBody.Builder builder = new FormBody.Builder();
        builder.add(RequestConstant.KEY_TOKEN, token);
        builder.add(RequestConstant.KEY_TRADE_NO, tradeNo);
        builder.add(RequestConstant.KEY_SIGN, RequestConstant.DEFAULT_SIGN_VALUE);
        Request request = new Request.Builder()
                .url(SPManager.getInstance().getSpaServerAddress() + RequestConstant.URL_TRADE_QR_CODE)
                .post(builder.build())
                .build();
        try {
            Response response = OkHttpUtil.getInstance().getClient().newCall(request).execute();
            StringResult stringResult = new Gson().fromJson(response.body().string(), StringResult.class);
            return stringResult.getRespData();
        } catch (Exception e) {
            XLogger.e("getTradeQrcode failed:" + e.getLocalizedMessage());
        }
        return null;
    }
}
