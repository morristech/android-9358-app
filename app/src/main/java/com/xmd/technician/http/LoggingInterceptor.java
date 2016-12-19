package com.xmd.technician.http;

import android.util.Log;

import com.xmd.technician.AppConfig;
import com.xmd.technician.TechApplication;
import com.xmd.technician.common.Logger;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
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
        request = request.newBuilder().header("User-Agent",hear + "-android"+ AppConfig.getAppVersionNameAndCode()).build();
        //request.newBuilder().addHeader("User-Agent","android"+ AppConfig.getAppVersionNameAndCode()).build();

//        long t1 = System.nanoTime();
            if(TechApplication.isTest ){
                OkHttpClient client = new OkHttpClient();
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {

                    }
                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        Log.i("Json","json=="+response.body().string());
                    }

                });
            }
//        Logger.v("request:" + request.toString());
//        FormBody formBody = (FormBody) request.body();
//        if(formBody != null){
//            int size = formBody.size();
//            for(int i = 0; i < size; i++) {
//                Logger.v(formBody.name(i) + ":" + formBody.value(i));
//            }
//        }

        Logger.d(requestToString(request));

        Response response = chain.proceed(request);
//        long t2 = System.nanoTime();
//        Logger.d(String.format("received " + response.toString() + " in %.1fms%n", (t2 - t1) / 1e6d));

        return response;

    }


    private String requestToString(Request request) {
        String result = request.method() + ":" + request.url().toString();
        RequestBody requestBody = request.body();
        if (requestBody != null) {
            if (requestBody instanceof FormBody) {
                FormBody body = (FormBody) request.body();
                result += "----params:";
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
