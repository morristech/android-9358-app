package com.xmd.m.network;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by heyangya on 17-5-25.
 * retrofit
 */

class RetrofitFactory {
    private static Map<String, Object> mServiceMap = new HashMap<>();
    private static String baseUrl;

    static synchronized <T> T getService(Class<T> serviceClass) {
        Object service = mServiceMap.get(serviceClass.getName());
        if (service == null) {
            service = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .client(OkHttpUtil.getInstance().getClient())
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .build()
                    .create(serviceClass);
            mServiceMap.put(serviceClass.getName(), service);
        }
        return (T) service;
    }

    static void clear() {
        mServiceMap.clear();
    }

    public static String getBaseUrl() {
        return baseUrl;
    }

    static void setBaseUrl(String baseUrl) {
        RetrofitFactory.baseUrl = baseUrl;
    }
}
