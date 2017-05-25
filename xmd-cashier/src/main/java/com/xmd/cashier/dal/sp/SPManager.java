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

    // ---核销成功
    public boolean getVerifySuccessSwitch() {
        return mSharedPreferences.getBoolean(SPConstants.VERIFY_SUCCESS_PRINT_SWITCH, true);
    }

    public void setVerifySuccessSwitch(boolean status) {
        mSharedPreferences.edit().putBoolean(SPConstants.VERIFY_SUCCESS_PRINT_SWITCH, status).apply();
    }

    // ---订单接受
    public boolean getOrderAcceptSwitch() {
        return mSharedPreferences.getBoolean(SPConstants.ORDER_ACCEPT_PRINT_SWITCH, true);
    }

    public void setOrderAcceptSwitch(boolean status) {
        mSharedPreferences.edit().putBoolean(SPConstants.ORDER_ACCEPT_PRINT_SWITCH, status).apply();
    }

    // ---订单拒绝
    public boolean getOrderRejectSwitch() {
        return mSharedPreferences.getBoolean(SPConstants.ORDER_REJECT_PRINT_SWITCH, false);
    }

    public void setOrderRejectSwitch(boolean status) {
        mSharedPreferences.edit().putBoolean(SPConstants.ORDER_REJECT_PRINT_SWITCH, status).apply();
    }

    // ---买单确认
    public boolean getOnlinePassSwitch() {
        return mSharedPreferences.getBoolean(SPConstants.ONLINE_PASS_PRINT_SWITCH, true);
    }

    public void setOnlinePassSwitch(boolean status) {
        mSharedPreferences.edit().putBoolean(SPConstants.ONLINE_PASS_PRINT_SWITCH, status).apply();
    }

    // ---买单异常
    public boolean getOnlineUnpassSwitch() {
        return mSharedPreferences.getBoolean(SPConstants.ONLINE_UNPASS_PRINT_SWITCH, false);
    }

    public void setOnlineUnpassSwitch(boolean status) {
        mSharedPreferences.edit().putBoolean(SPConstants.ONLINE_UNPASS_PRINT_SWITCH, status).apply();
    }

    // --微信买单
    public boolean getOnlinePaySwitch() {
        return mSharedPreferences.getBoolean(SPConstants.ONLINE_PAY_PRINT_SWITCH, true);
    }

    public void setOnlinePaySwitch(boolean status) {
        mSharedPreferences.edit().putBoolean(SPConstants.ONLINE_PAY_PRINT_SWITCH, status).apply();
    }
}
