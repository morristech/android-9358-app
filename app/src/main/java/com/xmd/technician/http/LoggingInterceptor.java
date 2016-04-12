package com.xmd.technician.http;

import com.xmd.technician.common.Logger;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by sdcm on 16-1-12.
 */
public class LoggingInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {

        Request request = chain.request();

        long t1 = System.nanoTime();
//        Logger.v("request:" + request.toString());
//        FormBody formBody = (FormBody) request.body();
//        int size = formBody.size();
//        for(int i = 0; i < size; i++) {
//            Logger.v(formBody.name(i) + ":" + formBody.value(i));
//        }
        Response response = chain.proceed(request);
        long t2 = System.nanoTime();
        Logger.v(String.format("received " + response.toString() + " in %.1fms%n", (t2 - t1) / 1e6d));

        return response;

    }
}
