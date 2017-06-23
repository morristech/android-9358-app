package com.xmd.manager.service;

import com.xmd.m.network.OkHttpUtil;
import com.xmd.manager.SharedPreferenceHelper;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by sdcm on 16-1-11.
 */
public class RetrofitServiceFactory {

    private static SpaService mSpaService;
    private static AppUpdateService mAppUpdateService;

    public static SpaService getSpaService() {
        if (mSpaService == null) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(SharedPreferenceHelper.getServerHost())
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(OkHttpUtil.getInstance().getClient())
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .build();
            mSpaService = retrofit.create(SpaService.class);
        }
        return mSpaService;
    }

    public static synchronized AppUpdateService getAppUpdateService() {
        if (mAppUpdateService == null) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(SharedPreferenceHelper.getUpdateServer())
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(OkHttpUtil.getInstance().getClient())
                    .build();
            mAppUpdateService = retrofit.create(AppUpdateService.class);
        }
        return mAppUpdateService;
    }

    /**
     * Recreate the spaservice, when configuration changed, such as baseUrl
     */
    public static synchronized void recreateService() {
        mSpaService = null;
        mAppUpdateService = null;
    }

}
