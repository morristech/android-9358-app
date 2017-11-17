package com.xmd.cashier.dal.sp;

import android.content.SharedPreferences;

import com.shidou.commonlibrary.helper.XLogger;
import com.xmd.cashier.BuildConfig;
import com.xmd.cashier.common.AppConstants;
import com.xmd.m.network.XmdNetwork;

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
        return mSharedPreferences.getString(SPConstants.SERVER_ADDRESS, "http://" + BuildConfig.SERVER_HOST);
    }

    public void setSpaServerAddress(String address) {
        XLogger.i("set server to " + address);
        XmdNetwork.getInstance().changeServer("http://" + address);
        mSharedPreferences.edit().putString(SPConstants.SERVER_ADDRESS, "http://" + address).apply();
    }

    public boolean getPrintClientSwitch() {
        return mSharedPreferences.getBoolean(SPConstants.GLOBAL_PRINT_CLIENT_SWITCH, false);
    }

    public void setPrintClientSwitch(boolean status) {
        mSharedPreferences.edit().putBoolean(SPConstants.GLOBAL_PRINT_CLIENT_SWITCH, status).apply();
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

    public int getFastPayPushTag() {
        return mSharedPreferences.getInt(SPConstants.FASTPAY_PUSH_TAG, 0);
    }

    public void setFastPayPushTag(int count) {
        mSharedPreferences.edit().putInt(SPConstants.FASTPAY_PUSH_TAG, count).apply();
    }

    public void updateFastPayPushTag() {
        int count = getFastPayPushTag();
        if (count > 0) {
            setFastPayPushTag(count - 1);
        }
    }

    public int getOrderPushTag() {
        return mSharedPreferences.getInt(SPConstants.ORDER_PUSH_TAG, 0);
    }

    public void setOrderPushTag(int count) {
        mSharedPreferences.edit().putInt(SPConstants.ORDER_PUSH_TAG, count).apply();
    }

    public void updateOrderPushTag() {
        int count = getOrderPushTag();
        if (count > 0) {
            setOrderPushTag(count - 1);
        }
    }

    public String getStatisticsStart() {
        return mSharedPreferences.getString(SPConstants.STATISTICS_START_TIME, AppConstants.STATISTICS_DEFAULT_TIME);
    }

    public void setStatisticsStart(String startTime) {
        mSharedPreferences.edit().putString(SPConstants.STATISTICS_START_TIME, startTime).apply();
    }

    public String getStatisticsEnd() {
        return mSharedPreferences.getString(SPConstants.STATISTICS_END_TIME, AppConstants.STATISTICS_DEFAULT_TIME);
    }

    public void setStatisticsEnd(String endTime) {
        mSharedPreferences.edit().putString(SPConstants.STATISTICS_END_TIME, endTime).apply();
    }

    public boolean getFirstStatistic() {    //是否第一次进行设置
        return mSharedPreferences.getBoolean(SPConstants.STATISTICS_FIRST_SETTING, true);
    }

    public void setFirstStatistic(boolean isFirst) {
        mSharedPreferences.edit().putBoolean(SPConstants.STATISTICS_FIRST_SETTING, isFirst).apply();
    }
}
