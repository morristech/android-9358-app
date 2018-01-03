package com.xmd.cashier.dal.net;

import com.xmd.cashier.BuildConfig;
import com.xmd.m.network.OkHttpUtil;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by zr on 17-12-27.
 */

public class UploadRetrofit {
    private static UploadService uploadService;

    public static synchronized UploadService getService() {
        if (uploadService == null) {
            Retrofit.Builder builder = new Retrofit.Builder()
                    .baseUrl("http://" + BuildConfig.UPLOAD_SERVER_HOST)
                    .client(OkHttpUtil.getInstance().getClient())
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create());
            uploadService = builder.build().create(UploadService.class);
        }
        return uploadService;
    }
}
