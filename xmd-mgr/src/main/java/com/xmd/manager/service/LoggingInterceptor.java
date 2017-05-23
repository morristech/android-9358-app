package com.xmd.manager.service;

import com.xmd.manager.AppConfig;
import com.xmd.manager.common.Logger;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by sdcm on 16-1-12.
 */
public class LoggingInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {

        Request request = chain.request();
        String header = request.header("User-Agent");
        request = request.newBuilder()
                .header("User-Agent", header + "-android" + AppConfig.getAppVersionNameAndCode())
                .build();
        /**
         * 一下代码仅作调试使用
         * 用于显示请求后台的数据
         */

        long t1 = System.nanoTime();
        Logger.d(requestToString(request));

        Response response = chain.proceed(request);
        long t2 = System.nanoTime();
        Logger.d(String.format("received " + response.toString() + " in %.1fms%n", (t2 - t1) / 1e6d));

        return response;
    }

    private String requestToString(Request request) {
        String result = request.method() + ":" + request.url().toString();
        RequestBody requestBody = request.body();
        if (requestBody != null) {
            if (requestBody instanceof FormBody) {
                FormBody body = (FormBody) request.body();
                result += "?";
                for (int i = 0; i < body.size(); i++) {
                    result += body.name(i) + "=" + body.value(i) + "&";
                }
                result = result.substring(0, result.length() - 1);
            } else {
                result += "----un form data!";
            }
        }
        return result;
    }
}
