package com.xmd.cashier.dal.net;

import com.shidou.commonlibrary.network.OkHttpUtil;
import com.xmd.cashier.dal.sp.SPManager;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by heyangya on 16-8-22.
 */

public class SpaRetrofit {
    private static SpaService spaService;

    public static synchronized SpaService getService() {
        if (spaService == null) {
            Retrofit.Builder builder = new Retrofit.Builder()
                    .baseUrl("http://" + SPManager.getInstance().getSpaServerAddress())
                    .client(OkHttpUtil.getInstance().getClient())
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create());
            spaService = builder.build().create(SpaService.class);
        }
        return spaService;
    }

    public static void rebuildService() {
        spaService = null;
        getService();
    }
}
