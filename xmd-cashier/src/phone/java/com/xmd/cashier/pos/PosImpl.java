package com.xmd.cashier.pos;

import android.content.Context;

import com.shidou.commonlibrary.helper.XLogger;
import com.shidou.commonlibrary.util.DeviceInfoUtils;
import com.xmd.cashier.MainApplication;
import com.xmd.cashier.cashier.IPos;
import com.xmd.cashier.common.AppConstants;
import com.xmd.cashier.manager.Callback;
import com.xmd.cashier.manager.PayCallback;

/**
 * Created by heyangya on 16-10-10.
 * 手机版本
 */

public class PosImpl implements IPos {
    private static PosImpl instance = new PosImpl();
    private String mTradeNo;
    private String mCertification;

    private PosImpl() {

    }

    public static PosImpl getInstance() {
        return instance;
    }

    @Override
    public String getAppCode() {
        return AppConstants.APP_CODE_PHONE_POS;
    }

    @Override
    public boolean needCheckUpdate() {
        return true;
    }

    @Override
    public void init(Context context, Callback<?> callback) {
        if (callback != null) {
            callback.onSuccess(null);
        }
    }

    @Override
    public boolean needChoicePayTypeByCaller() {
        return true;
    }

    @Override
    public String getPackageName() {
        return "";
    }

    @Override
    public void pay(Context context, String tradeNo, int money, int payType, PayCallback<Object> callback) {
        mTradeNo = String.valueOf(System.currentTimeMillis());
        mCertification = "cert-" + mTradeNo;
        callback.onResult(null, null);
    }

    @Override
    public int getPayType(Object o) {
        return AppConstants.PAY_TYPE_CASH;
    }

    @Override
    public String getTradeNo(Object o) {
        return mTradeNo;
    }

    @Override
    public String getPayCertificate(Object o) {
        return mCertification;
    }

    @Override
    public String getExtraInfo(Object o) {
        return null;
    }

    @Override
    public boolean isUserCancel(Object o) {
        return false;
    }

    @Override
    public void printBitmap(byte[] bitmap) {

    }

    @Override
    public void printText(String text) {

    }

    @Override
    public void printText(String text, boolean highLight) {

    }

    @Override
    public void printRight(String text) {

    }

    @Override
    public void printRight(String text, boolean highLight) {

    }

    @Override
    public void printText(String left, String right) {

    }

    @Override
    public void printBoldText(String left, String right) {

    }

    @Override
    public void printText(String left, String right, boolean highLight) {

    }

    @Override
    public void printCenter(String text) {

    }

    @Override
    public void printCenter(String text, boolean highLight) {

    }

    @Override
    public void printDivide() {

    }

    @Override
    public void printEnd() {

    }

    @Override
    public void speech(String text) {

    }

    @Override
    public String getPosIdentifierNo() {
        String deviceId = DeviceInfoUtils.getDeviceId(MainApplication.getInstance().getApplicationContext());
        XLogger.i(AppConstants.LOG_BIZ_LOCAL_CONFIG + "DeviceId：" + deviceId);
        return deviceId;
    }


    @Override
    public String getMagneticReaderInfo() {
        return null;
    }
}
