package com.xmd.technician.http;

import com.xmd.technician.AppConfig;
import com.xmd.technician.TechApplication;
import com.xmd.technician.common.Logger;

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
        String hear = request.header("User-Agent");
        request = request.newBuilder().header("User-Agent", hear + "-android" + AppConfig.getAppVersionNameAndCode()).build();


        if (TechApplication.isTest) {
            Logger.d(requestToString(request));
        }
        Response response = chain.proceed(request);
        if (TechApplication.isTest) {
            Logger.d("response to " + request.url() + "," + response.code());
        }
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
