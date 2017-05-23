package com.xmd.cashier.dal.net;

import com.shidou.commonlibrary.network.OkHttpUtil;
import com.xmd.cashier.BuildConfig;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by heyangya on 16-8-22.
 */

public class UpdateRetrofit {
    private static UpdateService updateService;

    public static synchronized UpdateService getService() {
        if (updateService == null) {
            Retrofit.Builder builder = new Retrofit.Builder()
                    .baseUrl("http://" + BuildConfig.UPDATE_SERVER_HOST)
                    .client(OkHttpUtil.getInstance().getClient())
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create());
            updateService = builder.build().create(UpdateService.class);
        }
        return updateService;
    }
}
