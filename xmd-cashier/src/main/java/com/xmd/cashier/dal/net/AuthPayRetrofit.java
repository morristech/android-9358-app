package com.xmd.cashier.dal.net;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by zr on 18-3-27.
 */

public class AuthPayRetrofit {
    private static AuthPayService authPayService;
    private static String baseUrl;

    public static synchronized AuthPayService getService() {
        if (authPayService == null) {
            Retrofit.Builder builder = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .client(CustomOkHttpUtil.getInstance().getClient())
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create());
            authPayService = builder.build().create(AuthPayService.class);
        }
        return authPayService;
    }

    public static void setBaseUrl(String baseUrl) {
        AuthPayRetrofit.baseUrl = baseUrl;
    }
}