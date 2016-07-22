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

        long t1 = System.nanoTime();
       /* Logger.v("request:" + request.toString());
        FormBody formBody = (FormBody) request.body();
        int size = formBody.size();
        for(int i = 0; i < size; i++) {
            Logger.v(formBody.name(i) + ":" + formBody.value(i));
        }*/

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


        Response response = chain.proceed(request);
        long t2 = System.nanoTime();
        Logger.v(String.format("received " + response.toString() + " in %.1fms%n", (t2 - t1) / 1e6d));

        return response;

    }
}
