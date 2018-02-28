package com.xmd.cashier.dal.net;

import com.shidou.commonlibrary.util.MD5Utils;

import java.util.Iterator;
import java.util.TreeMap;

import okhttp3.FormBody;
import okhttp3.Request;

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
}
