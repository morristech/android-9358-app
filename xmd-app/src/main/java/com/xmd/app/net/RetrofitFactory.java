package com.xmd.app.net;

import com.shidou.commonlibrary.network.OkHttpUtil;
import com.xmd.app.XmdApp;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by heyangya on 17-5-25.
 */

public class RetrofitFactory {
    private static Map<String, Object> mServiceMap = new HashMap<>();

    public static synchronized <T> T getService(Class<T> serviceClass) {
        Object service = mServiceMap.get(serviceClass.getName());
        if (service == null) {
            service = new Retrofit.Builder()
                    .baseUrl(XmdApp.getServer())
                    .client(OkHttpUtil.getInstance().getClient())
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .build()
                    .create(serviceClass);
            mServiceMap.put(serviceClass.getName(), service);
        }
        return (T) service;
    }

    public static void clear() {
        mServiceMap.clear();
    }
}
