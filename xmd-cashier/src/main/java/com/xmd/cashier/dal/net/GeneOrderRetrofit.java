package com.xmd.cashier.dal.net;

import com.xmd.cashier.dal.sp.SPManager;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by zr on 18-3-27.
 */

public class GeneOrderRetrofit {
    private static GeneOrderService geneOrderService;

    public static synchronized GeneOrderService getService() {
        if (geneOrderService == null) {
            Retrofit.Builder builder = new Retrofit.Builder()
                    .baseUrl(SPManager.getInstance().getSpaServerAddress())
                    .client(GeneOrderOkHttpUtil.getInstance().getClient())
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create());
            geneOrderService = builder.build().create(GeneOrderService.class);
        }
        return geneOrderService;
    }

    public static void clear() {
        geneOrderService = null;
    }
}
