package com.xmd.cashier.dal.sp;

import android.content.SharedPreferences;

import com.shidou.commonlibrary.helper.XLogger;
import com.xmd.cashier.BuildConfig;

/**
 * Created by heyangya on 16-8-23.
 */

public class SPManager {
    private static SPManager instance = new SPManager();
    private SharedPreferences mSharedPreferences;

    private SPManager() {

    }

    public static SPManager getInstance() {
        return instance;
    }

    public void init(SharedPreferences sharedPreferences) {
        assert sharedPreferences != null;
        mSharedPreferences = sharedPreferences;
    }

    public String getSpaServerAddress() {
        return mSharedPreferences.getString(SPConstants.SERVER_ADDRESS, BuildConfig.SERVER_HOST);
    }

    public void setSpaServerAddress(String address) {
        XLogger.i("set server to " + address);
        mSharedPreferences.edit().putString(SPConstants.SERVER_ADDRESS, address).apply();
    }
}
