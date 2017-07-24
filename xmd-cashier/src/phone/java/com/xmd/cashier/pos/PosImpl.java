package com.xmd.cashier.pos;

import android.content.Context;

import com.shidou.commonlibrary.util.DeviceInfoUtils;
import com.xmd.cashier.MainApplication;
import com.xmd.cashier.cashier.IPos;
import com.xmd.cashier.common.AppConstants;
import com.xmd.cashier.manager.Callback;
import com.xmd.cashier.manager.PayCallback;

/**
 * Created by heyangya on 16-10-10.
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
        return AppConstants.APP_CODE_HUI_POS;
    }

    @Override
    public boolean needCheckUpdate() {
        return true;
    }

    @Override
    public void init(Context context, Callback<?> callback) {
        callback.onSuccess(null);
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
    public void printRight(String text) {

    }

    @Override
    public void printText(String text, int gravity) {

    }

    // left居左 right居右
    @Override
    public void printText(String left, String right) {

    }

    @Override
    public void printCenter(String text) {

    }

    // 分割线
    @Override
    public void printDivide() {

    }

    @Override
    public void printEnd() {

    }

    @Override
    public String getPosIdentifierNo() {
        return DeviceInfoUtils.getDeviceId(MainApplication.getInstance().getApplicationContext());
    }

    @Override
    public void textToSound(String text) {

    }

    @Override
    public String getMagneticReaderInfo() {
        return null;
    }
}
