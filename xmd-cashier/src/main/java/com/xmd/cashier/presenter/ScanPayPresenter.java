package com.xmd.cashier.presenter;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.text.TextUtils;

import com.shidou.commonlibrary.helper.RetryPool;
import com.shidou.commonlibrary.helper.XLogger;
import com.shidou.commonlibrary.util.DateUtils;
import com.xmd.cashier.R;
import com.xmd.cashier.UiNavigation;
import com.xmd.cashier.cashier.PosFactory;
import com.xmd.cashier.common.AppConstants;
import com.xmd.cashier.common.Utils;
import com.xmd.cashier.contract.ScanPayContract;
import com.xmd.cashier.contract.ScanPayContract.Presenter;
import com.xmd.cashier.dal.bean.GiftActivityInfo;
import com.xmd.cashier.dal.bean.OnlinePayUrlInfo;
import com.xmd.cashier.dal.bean.Trade;
import com.xmd.cashier.dal.event.QRScanStatusEvent;
import com.xmd.cashier.dal.net.RequestConstant;
import com.xmd.cashier.dal.net.SpaService;
import com.xmd.cashier.dal.net.response.GiftActivityResult;
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

import org.greenrobot.eventbus.EventBus;

import java.util.Date;

import retrofit2.Call;
import rx.Observable;
import rx.Subscription;

/**
 * Created by zr on 17-5-12.
 */

public class ScanPayPresenter implements Presenter {
    private static final String TAG = "ScanPayPresenter";
    private static final int EXPIRE_INTERVAL = 60 * 60 * 1000; //前端二维码过期时间:1小时

    private GiftActivityInfo mGiftActivityInfo;
    private Subscription mGetGiftActivitySubscription;

    private Context mContext;
    private ScanPayContract.View mView;

    private Bitmap mQRBitmap;
    private TradeManager mTradeManager;

    private Subscription mGetXMDOnlineQrcodeUrlSubscription;
    private Subscription mDeleteXMDOnlineOrderIdSubscription;

    // ****************************************轮询扫码状态******************************************
    private Call<StringResult> getScanStatusCall;
    private RetryPool.RetryRunnable mRetryScanStatus;
    private boolean resultScanStatus = false;

    private void startGetScanStatus() {
        mRetryScanStatus = new RetryPool.RetryRunnable(AppConstants.DEFAULT_INTERVAL, 1.0f, new RetryPool.RetryExecutor() {
            @Override
            public boolean run() {
                return getScanStatus();
            }
        });
        RetryPool.getInstance().postWork(mRetryScanStatus);
    }

    private void stopGetScanStatus() {
        if (getScanStatusCall != null && !getScanStatusCall.isCanceled()) {
            getScanStatusCall.cancel();
        }
        if (mRetryScanStatus != null) {
            RetryPool.getInstance().removeWork(mRetryScanStatus);
            mRetryScanStatus = null;
        }
    }

    private boolean getScanStatus() {
        XLogger.i(TAG, AppConstants.LOG_BIZ_NORMAL_CASHIER + "补收款微信支付宝查询二维码扫码状态：" + RequestConstant.URL_GET_XMD_ONLINE_SCAN_STATUS);
        getScanStatusCall = XmdNetwork.getInstance().getService(SpaService.class)
                .getXMDOnlineScanStatus(AccountManager.getInstance().getToken(), mTradeManager.getCurrentTrade().tradeNo);
        XmdNetwork.getInstance().requestSync(getScanStatusCall, new NetworkSubscriber<StringResult>() {
            @Override
            public void onCallbackSuccess(StringResult result) {
                XLogger.i(TAG, AppConstants.LOG_BIZ_NORMAL_CASHIER + "补收款微信支付宝查询二维码扫码状态---成功：" + result.getRespData());
                if (AppConstants.APP_REQUEST_YES.equals(result.getRespData())) {
                    resultScanStatus = true;
                    EventBus.getDefault().post(new QRScanStatusEvent());
                } else {
                    resultScanStatus = false;
                }
            }

            @Override
            public void onCallbackError(Throwable e) {
                XLogger.e(TAG, AppConstants.LOG_BIZ_NORMAL_CASHIER + "补收款微信支付宝查询二维码扫码状态---失败：" + e.getLocalizedMessage());
                resultScanStatus = false;
            }
        });
        return resultScanStatus;
    }

    // ****************************************轮询买单详情******************************************
    private Call<OnlinePayDetailResult> getOnlinePayDetailCall;
    private RetryPool.RetryRunnable mRetryGetOnlinePayDetail;
    private boolean resultGetOnlinePayDetail = false;

    private void startGetOnlinePayDetail() {
        mRetryGetOnlinePayDetail = new RetryPool.RetryRunnable(AppConstants.TINNY_INTERVAL, 1.0f, new RetryPool.RetryExecutor() {
            @Override
            public boolean run() {
                return getOnlinePayDetail();
            }
        });
        RetryPool.getInstance().postWork(mRetryGetOnlinePayDetail);
    }

    private void stopGetOnlinePayDetail() {
        if (getOnlinePayDetailCall != null && !getOnlinePayDetailCall.isCanceled()) {
            getOnlinePayDetailCall.cancel();
        }
        if (mRetryGetOnlinePayDetail != null) {
            RetryPool.getInstance().removeWork(mRetryGetOnlinePayDetail);
            mRetryGetOnlinePayDetail = null;
        }
    }

    private boolean getOnlinePayDetail() {
        XLogger.i(TAG, AppConstants.LOG_BIZ_NORMAL_CASHIER + "补收款微信支付宝查询订单详情：" + RequestConstant.URL_GET_XMD_ONLINE_ORDER_DETAIL);
        getOnlinePayDetailCall = XmdNetwork.getInstance().getService(SpaService.class)
                .getXMDOnlinePayDetail(AccountManager.getInstance().getToken(), mTradeManager.getCurrentTrade().tradeNo);
        XmdNetwork.getInstance().requestSync(getOnlinePayDetailCall, new NetworkSubscriber<OnlinePayDetailResult>() {
            @Override
            public void onCallbackSuccess(OnlinePayDetailResult result) {
                XLogger.i(TAG, AppConstants.LOG_BIZ_NORMAL_CASHIER + "补收款微信支付宝查询订单详情---成功：" + result.getRespData().status);
                if (AppConstants.ONLINE_PAY_STATUS_PASS.equals(result.getRespData().status)) {
                    // 支付成功
                    resultGetOnlinePayDetail = true;
                    PosFactory.getCurrentCashier().textToSound("买单成功");
                    mTradeManager.getCurrentTrade().tradeStatus = AppConstants.TRADE_STATUS_SUCCESS;
                    mTradeManager.getCurrentTrade().tradeTime = result.getRespData().createTime;
                    mTradeManager.getCurrentTrade().onlinePayInfo = result.getRespData();
                    UiNavigation.gotoScanPayResultActivity(mContext, result.getRespData());
                    mView.finishSelf();
                } else {
                    if (isCodeExpire()) {
                        // 二维码过期
                        resultGetOnlinePayDetail = true;
                        XLogger.e(TAG, AppConstants.LOG_BIZ_NORMAL_CASHIER + "补收款微信支付宝查询订单详情：手动计算二维码过期");
                        doError("二维码已过期，请重新支付");
                    } else {
                        // 尚未支付成功:重试
                        resultGetOnlinePayDetail = false;
                    }
                }
            }

            @Override
            public void onCallbackError(Throwable e) {
                XLogger.e(TAG, AppConstants.LOG_BIZ_NORMAL_CASHIER + "补收款微信支付宝查询订单详情---失败：" + e.getLocalizedMessage());
                if (e instanceof ServerException) {
                    if (((ServerException) e).statusCode == RequestConstant.RESP_TOKEN_EXPIRED) {
                        // 会话过期
                        resultGetOnlinePayDetail = true;
                        XLogger.e(TAG, AppConstants.LOG_BIZ_NORMAL_CASHIER + "补收款微信支付宝查询订单详情---失败:会话过期");
                        doError("会话过期，请重新支付");
                    } else {
                        resultGetOnlinePayDetail = false;
                    }
                } else {
                    resultGetOnlinePayDetail = false;
                }
            }
        });
        return resultGetOnlinePayDetail;
    }

    public ScanPayPresenter(Context context, ScanPayContract.View view) {
        mContext = context;
        mView = view;
        mView.setPresenter(this);
        mTradeManager = TradeManager.getInstance();
    }

    @Override
    public void onCreate() {
        mView.setOrigin(Utils.moneyToStringEx(mTradeManager.getCurrentTrade().getOriginMoney()));
        mView.setDiscount("- " + Utils.moneyToStringEx(mTradeManager.getCurrentTrade().getWillDiscountMoney()));
        mView.setPaid(String.format(mContext.getResources().getString(R.string.cashier_money),
                Utils.moneyToStringEx(mTradeManager.getCurrentTrade().getOriginMoney() - mTradeManager.getCurrentTrade().getWillDiscountMoney())));

        getQrcode();
        getGiftActivity();
    }

    @Override
    public void onStart() {
    }

    @Override
    public void onDestroy() {
        stopGetScanStatus();
        stopGetOnlinePayDetail();

        if (mGetXMDOnlineQrcodeUrlSubscription != null) {
            mGetXMDOnlineQrcodeUrlSubscription.unsubscribe();
        }
        if (mDeleteXMDOnlineOrderIdSubscription != null) {
            mDeleteXMDOnlineOrderIdSubscription.unsubscribe();
        }
        if (mGetGiftActivitySubscription != null) {
            mGetGiftActivitySubscription.unsubscribe();
        }
        mQRBitmap = null;
    }

    @Override
    public void onCancel() {
        XLogger.i(TAG, AppConstants.LOG_BIZ_NORMAL_CASHIER + "补收款微信支付宝取消交易?");
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
                        XLogger.i(TAG, AppConstants.LOG_BIZ_NORMAL_CASHIER + "补收款微信支付宝选择继续交易");
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("退出交易", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        XLogger.i(TAG, AppConstants.LOG_BIZ_NORMAL_CASHIER + "补收款微信支付宝选择退出交易");
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

        XLogger.i(TAG, AppConstants.LOG_BIZ_NORMAL_CASHIER + "补收款微信支付宝取消交易：" + RequestConstant.URL_DELETE_XMD_ONLINE_ORDER_ID);
        Observable<BaseBean> observable = XmdNetwork.getInstance().getService(SpaService.class)
                .deleteXMDOnlineOrderId(AccountManager.getInstance().getToken(), orderId);
        mDeleteXMDOnlineOrderIdSubscription = XmdNetwork.getInstance().request(observable, new NetworkSubscriber<BaseBean>() {
            @Override
            public void onCallbackSuccess(BaseBean result) {
                XLogger.i(TAG, AppConstants.LOG_BIZ_NORMAL_CASHIER + "补收款微信支付宝取消交易---成功");
                mView.hideLoading();
                stopGetOnlinePayDetail();
                stopGetScanStatus();
                doFinish();
            }

            @Override
            public void onCallbackError(Throwable e) {
                XLogger.e(TAG, AppConstants.LOG_BIZ_NORMAL_CASHIER + "补收款微信支付宝取消交易---失败：" + e.getLocalizedMessage());
                mView.hideLoading();
                mView.showToast("请求失败：" + e.getLocalizedMessage());
            }
        });
    }

    private void getGiftActivity() {
        if (mGetGiftActivitySubscription != null) {
            mGetGiftActivitySubscription.unsubscribe();
        }
        Observable<GiftActivityResult> observable = XmdNetwork.getInstance().getService(SpaService.class)
                .getGiftActivity(AccountManager.getInstance().getToken(), AccountManager.getInstance().getClubId());
        mGetGiftActivitySubscription = XmdNetwork.getInstance().request(observable, new NetworkSubscriber<GiftActivityResult>() {
            @Override
            public void onCallbackSuccess(GiftActivityResult result) {
                if (result != null && result.getRespData() != null) {
                    mGiftActivityInfo = result.getRespData();
                    mView.showGiftActivity();
                }
            }

            @Override
            public void onCallbackError(Throwable e) {
                XLogger.e("获取买单有礼活动失败:" + e.getLocalizedMessage());
            }
        });
    }


    @Override
    public void getQrcode() {
        mView.showQrLoading();
        if (mGetXMDOnlineQrcodeUrlSubscription != null) {
            mGetXMDOnlineQrcodeUrlSubscription.unsubscribe();
        }
        XLogger.i(TAG, AppConstants.LOG_BIZ_NORMAL_CASHIER + "补收款获取微信支付宝二维码：" + RequestConstant.URL_GET_XMD_ONLINE_QRCODE_URL);
        Observable<OnlinePayUrlResult> observable = XmdNetwork.getInstance().getService(SpaService.class)
                .getXMDOnlineQrcodeUrl(AccountManager.getInstance().getToken(), String.valueOf(mTradeManager.getCurrentTrade().getOriginMoney()), String.valueOf(mTradeManager.getCurrentTrade().getWillDiscountMoney()));
        mGetXMDOnlineQrcodeUrlSubscription = XmdNetwork.getInstance().request(observable, new NetworkSubscriber<OnlinePayUrlResult>() {
            @Override
            public void onCallbackSuccess(OnlinePayUrlResult result) {
                OnlinePayUrlInfo info = result.getRespData();
                mTradeManager.getCurrentTrade().tradeNo = info.orderId;
                mTradeManager.getCurrentTrade().tradeTime = DateUtils.doDate2String(new Date());
                if (info == null || TextUtils.isEmpty(info.url)) {
                    XLogger.i(TAG, AppConstants.LOG_BIZ_NORMAL_CASHIER + "补收款获取微信支付宝二维码：获取二维码数据异常(null)");
                    mView.showQrError("获取二维码数据异常");
                    return;
                }
                // 获取二维码成功
                try {
                    mQRBitmap = Utils.getQRBitmap(info.url);
                } catch (Exception e) {
                    mQRBitmap = null;
                }
                if (mQRBitmap == null) {
                    // 解析失败
                    XLogger.i(TAG, AppConstants.LOG_BIZ_NORMAL_CASHIER + "补收款获取微信支付宝二维码：解析二维码链接失败(exception)");
                    mView.showQrError("解析二维码链接失败");
                } else {
                    XLogger.i(TAG, AppConstants.LOG_BIZ_NORMAL_CASHIER + "补收款获取微信支付宝二维码：[" + info.orderId + "]" + info.url);
                    mView.showQrSuccess();
                    mView.setQRCode(mQRBitmap);
                    startGetScanStatus();
                    startGetOnlinePayDetail();
                }
            }

            @Override
            public void onCallbackError(Throwable e) {
                // 获取失败
                XLogger.e(TAG, AppConstants.LOG_BIZ_NORMAL_CASHIER + "补收款获取微信支付宝二维码---失败:" + e.getLocalizedMessage());
                mView.showQrError(e.getLocalizedMessage());
            }
        });
    }

    @Override
    public void onGiftActivity() {
        UiNavigation.gotoGiftActActivity(mContext, mGiftActivityInfo);
    }

    private boolean isCodeExpire() {
        long current = new Date().getTime();
        long create = DateUtils.doString2Long(mTradeManager.getCurrentTrade().tradeTime);
        return current - create > EXPIRE_INTERVAL;
    }

    private void doError(final String msg) {
        mTradeManager.getCurrentTrade().tradeStatus = AppConstants.TRADE_STATUS_CANCEL;
        mTradeManager.finishPay(mContext, new Callback0<Void>() {
            @Override
            public void onFinished(Void result) {
                mView.showError(msg);
            }
        });
    }

    private void doFinish() {
        mTradeManager.getCurrentTrade().tradeStatus = AppConstants.TRADE_STATUS_CANCEL;
        mTradeManager.finishPay(mContext, new Callback0<Void>() {
            @Override
            public void onFinished(Void result) {
                mView.finishSelf();
            }
        });
    }
}
