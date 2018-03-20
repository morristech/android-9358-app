package com.xmd.cashier.manager;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.text.TextUtils;

import com.google.zxing.WriterException;
import com.google.zxing.client.android.MyQrEncoder;
import com.shidou.commonlibrary.helper.XLogger;
import com.xmd.cashier.common.AppConstants;
import com.xmd.cashier.dal.LocalPersistenceManager;
import com.xmd.cashier.dal.bean.ClubQrcodeBytes;
import com.xmd.cashier.dal.bean.TradeRecordInfo;
import com.xmd.cashier.dal.net.RequestConstant;
import com.xmd.cashier.dal.net.SpaService;
import com.xmd.cashier.dal.net.response.StringResult;
import com.xmd.m.network.NetworkSubscriber;
import com.xmd.m.network.XmdNetwork;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import rx.Subscriber;

/**
 * Created by zr on 18-3-19.
 * 二维码：会所活动二维码 交易二维码
 */

public class QrcodeManager {
    private static final String TAG = "QrcodeManager";
    private static QrcodeManager mInstance = new QrcodeManager();

    public static QrcodeManager getInstance() {
        return mInstance;
    }

    // 交易二维码
    private byte[] tradeQrcodeBytes;

    public byte[] getTradeQrcodeBytes(TradeRecordInfo tradeRecordInfo) {
        tradeQrcodeBytes = null;
        Call<StringResult> tradeCodeCall = XmdNetwork.getInstance().getService(SpaService.class)
                .getTradeQrcode(AccountManager.getInstance().getToken(), tradeRecordInfo.id, tradeRecordInfo.payChannel, RequestConstant.DEFAULT_SIGN_VALUE);
        XmdNetwork.getInstance().requestSync(tradeCodeCall, new NetworkSubscriber<StringResult>() {
            @Override
            public void onCallbackSuccess(StringResult result) {
                String content = result.getRespData();
                if (!TextUtils.isEmpty(content)) {
                    XLogger.i(TAG, AppConstants.LOG_BIZ_TRADE_PAYMENT + "getTradeQrcodeBytes content:" + content);
                    try {
                        Bitmap bitmap = MyQrEncoder.encode(content, 240, 240);
                        if (bitmap != null) {
                            ByteArrayOutputStream bos = new ByteArrayOutputStream();
                            if (bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos)) {
                                tradeQrcodeBytes = bos.toByteArray();
                            } else {
                                XLogger.i(TAG, AppConstants.LOG_BIZ_TRADE_PAYMENT + "getTradeQrcodeBytes--bitmap compress failed");
                            }
                            bitmap.recycle();
                        } else {
                            XLogger.i(TAG, AppConstants.LOG_BIZ_TRADE_PAYMENT + "getTradeQrcodeBytes--Qrcode encode failed");
                        }
                    } catch (WriterException e) {
                        XLogger.i(TAG, AppConstants.LOG_BIZ_TRADE_PAYMENT + "getTradeQrcodeBytes--Qrcode encode exception:" + e.getLocalizedMessage());
                    }
                } else {
                    XLogger.i(TAG, AppConstants.LOG_BIZ_TRADE_PAYMENT + "getTradeQrcodeBytes--request success && isEmpty");
                }
            }

            @Override
            public void onCallbackError(Throwable e) {
                XLogger.i(TAG, AppConstants.LOG_BIZ_TRADE_PAYMENT + "getTradeQrcodeBytes--request error:" + e.getLocalizedMessage());
            }
        });

        if (tradeQrcodeBytes == null) {
            tradeQrcodeBytes = getClubQRCodeSync();
        }
        return tradeQrcodeBytes;
    }

    // 会所活动二维码
    private byte[] clubQrcodeBytes;

    public byte[] getClubQRCodeSync() {
        final String clubId = AccountManager.getInstance().getClubId();
        ClubQrcodeBytes c = LocalPersistenceManager.getClubQrcode(clubId);
        if (c != null) {
            clubQrcodeBytes = c.data;
            return clubQrcodeBytes;
        }

        Call<StringResult> callGetUrl = XmdNetwork.getInstance().getService(SpaService.class).getClubWXQrcodeURL(AccountManager.getInstance().getClubId());
        XmdNetwork.getInstance().requestSync(callGetUrl, new NetworkSubscriber<StringResult>() {
            @Override
            public void onCallbackSuccess(StringResult result) {
                String content = result.getRespData();
                if (!TextUtils.isEmpty(content)) {
                    XLogger.i(TAG, AppConstants.LOG_BIZ_TRADE_PAYMENT + "getClubQRCodeSync content:" + content);
                }
                Call<ResponseBody> callGetBytes = XmdNetwork.getInstance().getService(SpaService.class).getClubQrcodeByWX(content);
                XmdNetwork.getInstance().requestSync(callGetBytes, new Subscriber<ResponseBody>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        XLogger.i(TAG, AppConstants.LOG_BIZ_TRADE_PAYMENT + "getClubQRCodeBytes error:" + e.getLocalizedMessage());
                    }

                    @Override
                    public void onNext(ResponseBody responseBody) {
                        try {
                            byte[] bitmapBytes = responseBody.bytes();
                            if (bitmapBytes != null) {
                                Bitmap bitmap = BitmapFactory.decodeByteArray(bitmapBytes, 0, bitmapBytes.length);
                                Matrix matrix = new Matrix();
                                matrix.postScale(240.f / bitmap.getWidth(), 240.f / bitmap.getHeight());
                                bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                                if (bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos)) {
                                    clubQrcodeBytes = bos.toByteArray();
                                    ClubQrcodeBytes cc = new ClubQrcodeBytes();
                                    cc.data = clubQrcodeBytes;
                                    LocalPersistenceManager.writeClubQrcodeBytes(clubId, cc);
                                } else {
                                    XLogger.i(TAG, AppConstants.LOG_BIZ_TRADE_PAYMENT + "getClubQRCodeBytes : bitmap.compress failed!");
                                }
                                bitmap.recycle();
                            } else {
                                XLogger.i(TAG, AppConstants.LOG_BIZ_TRADE_PAYMENT + "getClubQRCodeBytes : can not get qrcode !");
                            }
                        } catch (IOException e) {
                            XLogger.i(TAG, AppConstants.LOG_BIZ_TRADE_PAYMENT + "getClubQRCodeBytes exception:" + e.getLocalizedMessage());
                        }
                    }
                });
            }

            @Override
            public void onCallbackError(Throwable e) {
                XLogger.i(TAG, AppConstants.LOG_BIZ_TRADE_PAYMENT + "getClubQRCodeURL error:" + e.getLocalizedMessage());
            }
        });
        return clubQrcodeBytes;
    }
}
