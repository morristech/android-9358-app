package com.xmd.cashier.presenter;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Handler;
import android.text.TextUtils;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.shidou.commonlibrary.helper.XLogger;
import com.shidou.commonlibrary.util.DateUtils;
import com.xmd.cashier.R;
import com.xmd.cashier.UiNavigation;
import com.xmd.cashier.cashier.PosFactory;
import com.xmd.cashier.common.AppConstants;
import com.xmd.cashier.common.Utils;
import com.xmd.cashier.contract.ScanPayContract;
import com.xmd.cashier.contract.ScanPayContract.Presenter;
import com.xmd.cashier.dal.bean.OnlinePayUrlInfo;
import com.xmd.cashier.dal.bean.Trade;
import com.xmd.cashier.dal.net.RequestConstant;
import com.xmd.cashier.dal.net.SpaService;
import com.xmd.cashier.dal.net.response.OnlinePayDetailResult;
import com.xmd.cashier.dal.net.response.OnlinePayUrlResult;
import com.xmd.cashier.dal.net.response.StringResult;
import com.xmd.cashier.manager.AccountManager;
import com.xmd.cashier.manager.Callback0;
import com.xmd.cashier.manager.TradeManager;
import com.xmd.cashier.widget.CustomAlertDialogBuilder;
import com.xmd.m.network.BaseBean;
import com.xmd.m.network.NetworkSubscriber;
import com.xmd.m.network.ServerException;
import com.xmd.m.network.XmdNetwork;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import rx.Observable;
import rx.Subscription;

/**
 * Created by zr on 17-5-12.
 */

public class ScanPayPresenter implements Presenter {
    private static final int INTERVAL = 5 * 1000;
    private static final int EXPIRE_INTERVAL = 60 * 60 * 1000; //前端二维码过期时间:1小时

    private Context mContext;
    private ScanPayContract.View mView;

    private Bitmap mQRBitmap;
    private TradeManager mTradeManager;

    private boolean isScan = false;
    private Subscription mGetXMDScanStatusSubscription;
    private Subscription mGetXMDOnlinePayDetailSubscription;
    private Subscription mGetXMDOnlineQrcodeUrlSubscription;
    private Subscription mDeleteXMDOnlineOrderIdSubscription;
    private Handler mHandler;
    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            if (isScan) {
                // 已经扫码:查询买单详情
                handleDetail();
            } else {
                // 尚未扫码
                if (mGetXMDScanStatusSubscription != null) {
                    mGetXMDScanStatusSubscription.unsubscribe();
                }
                Observable<StringResult> observable = XmdNetwork.getInstance().getService(SpaService.class)
                        .getXMDOnlineScanStatus(AccountManager.getInstance().getToken(), mTradeManager.getCurrentTrade().tradeNo);
                mGetXMDScanStatusSubscription = XmdNetwork.getInstance().request(observable, new NetworkSubscriber<StringResult>() {
                    @Override
                    public void onCallbackSuccess(StringResult result) {
                        if (isCodeExpire()) {
                            doCodeExpire();
                            return;
                        }
                        if (AppConstants.APP_REQUEST_YES.equals(result.getRespData())) {
                            // 已经扫码:获取买单详情
                            isScan = true;
                            mView.updateScanStatus();
                        }
                        handleDetail();
                    }

                    @Override
                    public void onCallbackError(Throwable e) {
                        if (e instanceof ServerException) {
                            if (((ServerException) e).statusCode == RequestConstant.RESP_ERROR) {
                                // 400:二维码过期
                                doCodeExpire();
                            } else if (((ServerException) e).statusCode == RequestConstant.RESP_TOKEN_EXPIRED) {
                                // 会话过期
                                doFinish();
                            } else {
                                mHandler.postDelayed(mRunnable, INTERVAL);
                            }
                        }
                    }
                });
            }
        }
    };

    // 处理详情信息
    private void handleDetail() {
        if (mGetXMDOnlinePayDetailSubscription != null) {
            mGetXMDOnlinePayDetailSubscription.unsubscribe();
        }
        Observable<OnlinePayDetailResult> observable = XmdNetwork.getInstance().getService(SpaService.class)
                .getXMDOnlinePayDetail(AccountManager.getInstance().getToken(), mTradeManager.getCurrentTrade().tradeNo);
        mGetXMDOnlinePayDetailSubscription = XmdNetwork.getInstance().request(observable, new NetworkSubscriber<OnlinePayDetailResult>() {
            @Override
            public void onCallbackSuccess(OnlinePayDetailResult result) {
                if (AppConstants.ONLINE_PAY_STATUS_PASS.equals(result.getRespData().status)) {
                    // 支付成功
                    PosFactory.getCurrentCashier().textToSound("买单成功");
                    mTradeManager.getCurrentTrade().tradeTime = result.getRespData().createTime;
                    mTradeManager.getCurrentTrade().setOnlinePayPaidMoney(result.getRespData().payAmount);
                    mTradeManager.getCurrentTrade().setOnlinePayChannel(result.getRespData().payChannel);
                    mTradeManager.getCurrentTrade().setOnlinePayId(result.getRespData().payId);
                    UiNavigation.gotoScanPayResultActivity(mContext, result.getRespData());
                    mView.finishSelf();
                } else {
                    if (isCodeExpire()) {
                        // 二维码过期
                        doCodeExpire();
                    } else {
                        // 尚未支付成功:重试
                        mHandler.postDelayed(mRunnable, INTERVAL);
                    }
                }
            }

            @Override
            public void onCallbackError(Throwable e) {
                if (e instanceof ServerException) {
                    if (((ServerException) e).statusCode == RequestConstant.RESP_TOKEN_EXPIRED) {
                        // 会话过期
                        doFinish();
                    } else {
                        mHandler.postDelayed(mRunnable, INTERVAL);
                    }
                }
            }
        });
    }

    public ScanPayPresenter(Context context, ScanPayContract.View view) {
        mContext = context;
        mView = view;
        mView.setPresenter(this);
        mTradeManager = TradeManager.getInstance();
        mHandler = new Handler();
    }

    @Override
    public void onCreate() {
        mView.setOrigin(Utils.moneyToStringEx(mTradeManager.getCurrentTrade().getOriginMoney()));
        mView.setDiscount("- " + Utils.moneyToStringEx(mTradeManager.getCurrentTrade().getWillDiscountMoney()));
        mView.setPaid(String.format(mContext.getResources().getString(R.string.cashier_money),
                Utils.moneyToStringEx(mTradeManager.getCurrentTrade().getOriginMoney() - mTradeManager.getCurrentTrade().getWillDiscountMoney())));

        getQrcode();
    }

    @Override
    public void onStart() {
    }

    @Override
    public void onDestroy() {
        mHandler.removeCallbacks(mRunnable);
        if (mGetXMDOnlinePayDetailSubscription != null) {
            mGetXMDOnlinePayDetailSubscription.unsubscribe();
        }
        if (mGetXMDScanStatusSubscription != null) {
            mGetXMDScanStatusSubscription.unsubscribe();
        }
        if (mGetXMDOnlineQrcodeUrlSubscription != null) {
            mGetXMDOnlineQrcodeUrlSubscription.unsubscribe();
        }
        if (mDeleteXMDOnlineOrderIdSubscription != null) {
            mDeleteXMDOnlineOrderIdSubscription.unsubscribe();
        }
        mQRBitmap = null;
    }

    private Bitmap getQRBitmap(String content) throws WriterException {
        if (TextUtils.isEmpty(content)) {
            return null;
        }

        int width = 350;
        int height = 350;
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);

        BitMatrix bitMatrix = new QRCodeWriter().encode(content, BarcodeFormat.QR_CODE, width, height, hints);
        int[] pixels = new int[width * height];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (bitMatrix.get(x, y)) {
                    pixels[y * width + x] = 0xff000000;
                } else {
                    pixels[y * width + x] = 0xffffffff;
                }
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return bitmap;
    }

    @Override
    public void onCancel() {
        String message;
        final Trade trade = mTradeManager.getCurrentTrade();
        if (trade.getVerificationSuccessfulMoney() > 0) {
            message = "选择的优惠券已经核销无法再次使用，确定退出本次交易？";
        } else {
            message = "确定退出交易？";
        }
        new CustomAlertDialogBuilder(mContext)
                .setMessage(message)
                .setPositiveButton("继续交易", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("退出交易", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        deleteOrderId(trade.tradeNo);
                    }
                })
                .create()
                .show();
    }

    // 取消交易时汇报给后台
    private void deleteOrderId(String orderId) {
        mView.showLoading();
        if (mDeleteXMDOnlineOrderIdSubscription != null) {
            mDeleteXMDOnlineOrderIdSubscription.unsubscribe();
        }

        Observable<BaseBean> observable = XmdNetwork.getInstance().getService(SpaService.class)
                .deleteXMDOnlineOrderId(AccountManager.getInstance().getToken(), orderId);
        mDeleteXMDOnlineOrderIdSubscription = XmdNetwork.getInstance().request(observable, new NetworkSubscriber<BaseBean>() {
            @Override
            public void onCallbackSuccess(BaseBean result) {
                mView.hideLoading();
                doFinish();
            }

            @Override
            public void onCallbackError(Throwable e) {
                mView.hideLoading();
                mView.showToast("请求失败：" + e.getLocalizedMessage());
            }
        });
    }

    @Override
    public void getQrcode() {
        mView.showQrLoading();
        if (mGetXMDOnlineQrcodeUrlSubscription != null) {
            mGetXMDOnlineQrcodeUrlSubscription.unsubscribe();
        }
        Observable<OnlinePayUrlResult> observable = XmdNetwork.getInstance().getService(SpaService.class)
                .getXMDOnlineQrcodeUrl(AccountManager.getInstance().getToken(), mTradeManager.getCurrentTrade().tradeNo, String.valueOf(mTradeManager.getCurrentTrade().getOriginMoney()), String.valueOf(mTradeManager.getCurrentTrade().getWillDiscountMoney()));
        mGetXMDOnlineQrcodeUrlSubscription = XmdNetwork.getInstance().request(observable, new NetworkSubscriber<OnlinePayUrlResult>() {
            @Override
            public void onCallbackSuccess(OnlinePayUrlResult result) {
                OnlinePayUrlInfo info = result.getRespData();
                if (info == null || TextUtils.isEmpty(info.url)) {
                    mView.showQrError("获取二维码数据异常");
                    return;
                }
                XLogger.d(info.orderId + " --- " + info.url);
                // 获取二维码成功
                try {
                    mQRBitmap = getQRBitmap(info.url);
                } catch (Exception e) {
                    mQRBitmap = null;
                }
                if (mQRBitmap == null) {
                    // 解析失败
                    mView.showQrError("解析二维码链接失败");
                } else {
                    mView.showQrSuccess();
                    mView.setQRCode(mQRBitmap);
                    mHandler.postDelayed(mRunnable, INTERVAL);
                }
            }

            @Override
            public void onCallbackError(Throwable e) {
                // 获取失败
                mView.showQrError(e.getLocalizedMessage());
            }
        });
    }

    private boolean isCodeExpire() {
        long current = new Date().getTime();
        long create = DateUtils.doString2Long(mTradeManager.getCurrentTrade().tradeTime);
        return current - create > EXPIRE_INTERVAL;
    }

    private void doCodeExpire() {
        mTradeManager.finishPay(mContext, AppConstants.TRADE_STATUS_CANCEL, new Callback0<Void>() {
            @Override
            public void onFinished(Void result) {
                mView.showError("二维码已过期，请重新支付");
            }
        });
    }

    private void doFinish() {
        mTradeManager.finishPay(mContext, AppConstants.TRADE_STATUS_CANCEL, new Callback0<Void>() {
            @Override
            public void onFinished(Void result) {
                mView.finishSelf();
            }
        });
    }
}
