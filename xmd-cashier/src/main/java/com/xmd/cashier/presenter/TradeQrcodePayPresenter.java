package com.xmd.cashier.presenter;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Handler;
import android.text.TextUtils;

import com.shidou.commonlibrary.helper.RetryPool;
import com.shidou.commonlibrary.helper.XLogger;
import com.shidou.commonlibrary.widget.XToast;
import com.xmd.cashier.R;
import com.xmd.cashier.UiNavigation;
import com.xmd.cashier.cashier.PosFactory;
import com.xmd.cashier.common.AppConstants;
import com.xmd.cashier.common.Utils;
import com.xmd.cashier.contract.TradeQrcodePayContract;
import com.xmd.cashier.contract.TradeQrcodePayContract.Presenter;
import com.xmd.cashier.dal.bean.GiftActivityInfo;
import com.xmd.cashier.dal.bean.Trade;
import com.xmd.cashier.dal.event.QRScanStatusEvent;
import com.xmd.cashier.dal.event.RechargeDoneEvent;
import com.xmd.cashier.dal.event.TradeDoneEvent;
import com.xmd.cashier.dal.event.TradeQrcodeCloseEvent;
import com.xmd.cashier.dal.net.AuthPayRetrofit;
import com.xmd.cashier.dal.net.RequestConstant;
import com.xmd.cashier.dal.net.SpaService;
import com.xmd.cashier.dal.net.response.GiftActivityResult;
import com.xmd.cashier.dal.net.response.MemberRecordResult;
import com.xmd.cashier.dal.net.response.TradeOrderInfoResult;
import com.xmd.cashier.dal.sp.SPManager;
import com.xmd.cashier.manager.AccountManager;
import com.xmd.cashier.manager.MemberManager;
import com.xmd.cashier.manager.TradeManager;
import com.xmd.cashier.widget.CustomAlertDialogBuilder;
import com.xmd.m.network.NetworkException;
import com.xmd.m.network.NetworkSubscriber;
import com.xmd.m.network.XmdNetwork;

import org.greenrobot.eventbus.EventBus;

import retrofit2.Call;
import rx.Observable;
import rx.Subscription;

/**
 * Created by zr on 17-5-12.
 */

public class TradeQrcodePayPresenter implements Presenter {
    private static final String TAG = "TradeQrcodePayPresenter";
    private Context mContext;
    private TradeQrcodePayContract.View mView;

    private int mTradeType;

    private GiftActivityInfo mGiftActivityInfo;
    private Subscription mGetGiftActivitySubscription;
    private Subscription mActiveAuthCodePaySubscription;
    private Subscription mActiveAuthCodeRechargeSubscription;

    private TradeManager mTradeManager;
    private MemberManager mMemberManager;

    private Handler mHandler;

    private Runnable mWaitScanRunnable = new Runnable() {
        @Override
        public void run() {
            PosFactory.getCurrentCashier().speech("请扫描客户支付二维码");
            mHandler.postDelayed(mWaitScanRunnable, 15 * 1000);
        }
    };

    private Runnable mWaitPayRunnable = new Runnable() {
        @Override
        public void run() {
            PosFactory.getCurrentCashier().speech("扫码成功，等待客户完成支付");
            mHandler.postDelayed(mWaitPayRunnable, 10 * 1000);
        }
    };

    public TradeQrcodePayPresenter(Context context, TradeQrcodePayContract.View view) {
        mContext = context;
        mView = view;
        mView.setPresenter(this);
        mTradeManager = TradeManager.getInstance();
        mMemberManager = MemberManager.getInstance();
        mHandler = new Handler();
    }

    @Override
    public void onCreate() {
        mTradeType = mView.getType();
        mHandler.removeCallbacksAndMessages(null);
        stopDetailRecharge();
        stopTradeOrder();
        switch (mTradeType) {
            case AppConstants.TRADE_TYPE_NORMAL:
            case AppConstants.TRADE_TYPE_INNER:
                showCashierQrcode();
                break;
            case AppConstants.TRADE_TYPE_RECHARGE:
                showRechargeQrcode();
                break;
            default:
                break;
        }

        String priority = SPManager.getInstance().getOnlinePayPriority();
        XLogger.i(TAG, AppConstants.LOG_BIZ_TRADE_PAYMENT + "扫码支付：");
        mView.showView(priority);
        switch (priority) {
            case AppConstants.ONLINE_PAY_PRIORITY_AUTH:
                XLogger.i(TAG, AppConstants.LOG_BIZ_TRADE_PAYMENT + "扫码支付：主扫用户支付二维码");
                initAuth();
                break;
            case AppConstants.ONLINE_PAY_PRIORITY_BITMAP:
                XLogger.i(TAG, AppConstants.LOG_BIZ_TRADE_PAYMENT + "扫码支付：二维码展示被扫");
                initBitmap();
                break;
            default:
                XLogger.i(TAG, AppConstants.LOG_BIZ_TRADE_PAYMENT + "扫码支付：unknown");
                break;
        }
    }

    private void showCashierQrcode() {
        Trade trade = mTradeManager.getCurrentTrade();
        mView.setAmount(String.format(mContext.getResources().getString(R.string.cashier_money), Utils.moneyToStringEx(trade.getWillPayMoney())));
        getGiftActivity();  //买单活动
        if (TextUtils.isEmpty(trade.payUrl)) {
            mView.showQrError("获取二维码失败");
        } else {
            Bitmap bitmap;
            try {
                bitmap = Utils.getQRBitmap(trade.payUrl);
            } catch (Exception e) {
                bitmap = null;
            }
            if (bitmap == null) {
                mView.showQrError("二维码解析失败");
            } else {
                mView.showQrSuccess();
                mView.setQRCode(bitmap);
            }
        }
    }

    private void showRechargeQrcode() {
        switch (mMemberManager.getAmountType()) {
            case AppConstants.MEMBER_RECHARGE_AMOUNT_TYPE_MONEY:    // 充值金额
                mView.setAmount(String.format(mContext.getResources().getString(R.string.cashier_money), Utils.moneyToStringEx(mMemberManager.getAmount())));
                break;
            case AppConstants.MEMBER_RECHARGE_AMOUNT_TYPE_PACKAGE:  // 充值套餐
                mView.setAmount(String.format(mContext.getResources().getString(R.string.cashier_money), Utils.moneyToStringEx(mMemberManager.getPackageInfo().amount)));
                break;
            case AppConstants.MEMBER_RECHARGE_AMOUNT_TYPE_NONE:
            default:
                break;
        }
        if (TextUtils.isEmpty(mMemberManager.getRechargeUrl())) {
            mView.showQrError("获取二维码失败");
        } else {
            Bitmap bitmap;
            try {
                bitmap = Utils.getQRBitmap(mMemberManager.getRechargeUrl());
            } catch (Exception e) {
                bitmap = null;
            }
            if (bitmap == null) {
                mView.showQrError("二维码解析失败");
            } else {
                mView.showQrSuccess();
                mView.setQRCode(bitmap);
            }
        }
    }

    private void initBitmap() {
        PosFactory.getCurrentCashier().speech("请扫描屏幕中二维码");
        switch (mTradeType) {
            case AppConstants.TRADE_TYPE_NORMAL:
            case AppConstants.TRADE_TYPE_INNER:
                startTradeOrder();
                break;
            case AppConstants.TRADE_TYPE_RECHARGE:
                startDetailRecharge();
                break;
            default:
                break;
        }
    }

    private void initAuth() {
        mHandler.post(mWaitScanRunnable);
    }

    @Override
    public void onStart() {

    }

    @Override
    public void onDestroy() {
        mHandler.removeCallbacksAndMessages(null);
        stopTradeOrder();
        stopDetailRecharge();
        if (mGetGiftActivitySubscription != null) {
            mGetGiftActivitySubscription.unsubscribe();
        }
        if (mActiveAuthCodePaySubscription != null) {
            mActiveAuthCodePaySubscription.unsubscribe();
        }
        if (mActiveAuthCodeRechargeSubscription != null) {
            mActiveAuthCodeRechargeSubscription.unsubscribe();
        }
    }

    // **************** 二维码授权支付 ****************
    @Override
    public void authPay(String authCode) {
        switch (mTradeType) {
            case AppConstants.TRADE_TYPE_NORMAL:
            case AppConstants.TRADE_TYPE_INNER:
                cashierAuthPay(authCode);
                break;
            case AppConstants.TRADE_TYPE_RECHARGE:
                rechargeAuthPay(authCode);
                break;
            default:
                break;
        }
    }

    private void rechargeAuthPay(String authCode) {
        XLogger.i(TAG, AppConstants.LOG_BIZ_TRADE_PAYMENT + "二维码授权会员充值：" + authCode);
        mHandler.removeCallbacksAndMessages(null);
        mHandler.postDelayed(mWaitPayRunnable, 5 * 1000);
        mView.showLoading();
        if (mActiveAuthCodeRechargeSubscription != null) {
            mActiveAuthCodeRechargeSubscription.unsubscribe();
        }

        Observable<MemberRecordResult> observable = AuthPayRetrofit.getService()
                .doAuthCodeRecharge(AccountManager.getInstance().getToken(), mMemberManager.getRechargeOrderId(), authCode, RequestConstant.DEFAULT_SIGN_VALUE);
        mActiveAuthCodeRechargeSubscription = XmdNetwork.getInstance().request(observable, new NetworkSubscriber<MemberRecordResult>() {
            @Override
            public void onCallbackSuccess(MemberRecordResult result) {
                XLogger.i(TAG, AppConstants.LOG_BIZ_TRADE_PAYMENT + "二维码授权会员充值---成功");
                mView.hideLoading();
                PosFactory.getCurrentCashier().speech("充值成功");
                mMemberManager.tradeStatus = AppConstants.TRADE_STATUS_SUCCESS;
                mMemberManager.recordInfo = result.getRespData();
                EventBus.getDefault().post(new RechargeDoneEvent());
                mView.finishSelf();
            }

            @Override
            public void onCallbackError(Throwable e) {
                mView.hideLoading();
                if (e instanceof NetworkException) {
                    XLogger.e(TAG, AppConstants.LOG_BIZ_TRADE_PAYMENT + "二维码授权会员充值---异常：" + e.getMessage());
                    PosFactory.getCurrentCashier().speech("收款出现异常");
                    mMemberManager.tradeStatus = AppConstants.TRADE_STATUS_EXCEPTION;
                    mMemberManager.tradeStatusError = e.getMessage();
                } else {
                    XLogger.e(TAG, AppConstants.LOG_BIZ_TRADE_PAYMENT + "二维码授权会员充值---失败：" + e.getMessage());
                    PosFactory.getCurrentCashier().speech("收款失败");
                    mMemberManager.tradeStatus = AppConstants.TRADE_STATUS_ERROR;
                    mMemberManager.tradeStatusError = e.getMessage();
                }
                EventBus.getDefault().post(new RechargeDoneEvent());
                mView.finishSelf();
            }
        });
    }

    private void cashierAuthPay(String authCode) {
        XLogger.i(TAG, AppConstants.LOG_BIZ_TRADE_PAYMENT + "二维码授权支付：" + authCode);
        mHandler.removeCallbacksAndMessages(null);
        mHandler.postDelayed(mWaitPayRunnable, 5 * 1000);
        mView.showLoading();
        if (mActiveAuthCodePaySubscription != null) {
            mActiveAuthCodePaySubscription.unsubscribe();
        }

        Observable<TradeOrderInfoResult> observable = AuthPayRetrofit.getService()
                .activeAuthPay(AccountManager.getInstance().getToken(),
                        String.valueOf(mTradeManager.getCurrentTrade().getWillPayMoney()),
                        mTradeManager.getCurrentTrade().payNo,
                        authCode,
                        mTradeManager.getCurrentTrade().payOrderId,
                        RequestConstant.DEFAULT_SIGN_VALUE);
        mActiveAuthCodePaySubscription = XmdNetwork.getInstance().request(observable, new NetworkSubscriber<TradeOrderInfoResult>() {
            @Override
            public void onCallbackSuccess(TradeOrderInfoResult result) {
                XLogger.i(TAG, AppConstants.LOG_BIZ_TRADE_PAYMENT + "二维码授权支付---成功");
                mView.hideLoading();
                PosFactory.getCurrentCashier().speech("支付成功");
                mTradeManager.getCurrentTrade().tradeStatus = AppConstants.TRADE_STATUS_SUCCESS;
                mTradeManager.getCurrentTrade().resultOrderInfo = result.getRespData().orderDetail;
                EventBus.getDefault().post(new TradeDoneEvent(mTradeType));
                mView.finishSelf();
            }

            @Override
            public void onCallbackError(Throwable e) {
                mView.hideLoading();
                if (e instanceof NetworkException) {
                    XLogger.e(TAG, AppConstants.LOG_BIZ_TRADE_PAYMENT + "二维码授权支付---异常：" + e.getMessage());
                    PosFactory.getCurrentCashier().speech("支付出现异常");
                    mTradeManager.getCurrentTrade().tradeStatus = AppConstants.TRADE_STATUS_EXCEPTION;
                    mTradeManager.getCurrentTrade().tradeStatusError = e.getMessage();
                } else {
                    XLogger.e(TAG, AppConstants.LOG_BIZ_TRADE_PAYMENT + "二维码授权支付---失败：" + e.getMessage());
                    PosFactory.getCurrentCashier().speech("支付失败");
                    mTradeManager.getCurrentTrade().tradeStatus = AppConstants.TRADE_STATUS_ERROR;
                    mTradeManager.getCurrentTrade().tradeStatusError = e.getMessage();
                }
                EventBus.getDefault().post(new TradeDoneEvent(mTradeType));
                mView.finishSelf();
            }
        });
    }

    @Override
    public void onAuthClick() {
        XLogger.i(TAG, AppConstants.LOG_BIZ_TRADE_PAYMENT + "选择主扫");
        switch (mTradeType) {
            case AppConstants.TRADE_TYPE_NORMAL:
            case AppConstants.TRADE_TYPE_INNER:
                stopTradeOrder();
                break;
            case AppConstants.TRADE_TYPE_RECHARGE:
                stopDetailRecharge();
                break;
            default:
                stopTradeOrder();
                stopDetailRecharge();
                break;
        }
        mHandler.post(mWaitScanRunnable);
    }

    @Override
    public void onBitmapClick() {
        XLogger.i(TAG, AppConstants.LOG_BIZ_TRADE_PAYMENT + "选择被扫");
        switch (mTradeType) {
            case AppConstants.TRADE_TYPE_NORMAL:
            case AppConstants.TRADE_TYPE_INNER:
                startTradeOrder();
                break;
            case AppConstants.TRADE_TYPE_RECHARGE:
                startDetailRecharge();
                break;
            default:
                break;
        }
        mHandler.removeCallbacksAndMessages(null);
    }

    // 查看买单活动
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
                XLogger.e(TAG, AppConstants.LOG_BIZ_TRADE_PAYMENT + "获取买单有礼活动失败:" + e.getLocalizedMessage());
            }
        });
    }

    @Override
    public void onGiftActivity() {
        UiNavigation.gotoGiftActActivity(mContext, mGiftActivityInfo);
    }

    @Override
    public void onKeyEventBack() {
        new CustomAlertDialogBuilder(mContext)
                .setMessage("请确认是否退出此次交易？")
                .setPositiveButton("继续交易", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        XLogger.i(TAG, AppConstants.LOG_BIZ_TRADE_PAYMENT + "选择继续交易");
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("退出交易", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        XLogger.i(TAG, AppConstants.LOG_BIZ_TRADE_PAYMENT + "选择退出交易");
                        dialog.dismiss();
                        mHandler.removeCallbacksAndMessages(null);
                        stopTradeOrder();
                        stopDetailRecharge();
                        switch (mTradeType) {
                            case AppConstants.TRADE_TYPE_NORMAL:
                            case AppConstants.TRADE_TYPE_INNER:
                                mTradeManager.getCurrentTrade().tradeStatus = AppConstants.TRADE_STATUS_ERROR;
                                mTradeManager.getCurrentTrade().tradeStatusError = "已取消交易";
                                EventBus.getDefault().post(new TradeDoneEvent(mTradeType));
                                break;
                            case AppConstants.TRADE_TYPE_RECHARGE:
                                mMemberManager.tradeStatus = AppConstants.TRADE_STATUS_ERROR;
                                mMemberManager.tradeStatusError = "已取消充值";
                                EventBus.getDefault().post(new RechargeDoneEvent());
                                break;
                            default:
                                break;
                        }
                        mView.finishSelf();
                    }
                })
                .create()
                .show();
    }


    // -----------------------------------------轮询充值详情----------------------------------------
    private Call<MemberRecordResult> callDetailRecharge;
    private RetryPool.RetryRunnable mRetryDetailRecharge;
    private boolean resultDetailRecharge = false;

    public void startDetailRecharge() {
        mRetryDetailRecharge = new RetryPool.RetryRunnable(1000, 1.0f, new RetryPool.RetryExecutor() {
            @Override
            public boolean run() {
                return detailRechargeTrade();
            }
        });
        RetryPool.getInstance().postWork(mRetryDetailRecharge);
    }

    public void stopDetailRecharge() {
        if (callDetailRecharge != null && !callDetailRecharge.isCanceled()) {
            callDetailRecharge.cancel();
        }
        if (mRetryDetailRecharge != null) {
            RetryPool.getInstance().removeWork(mRetryDetailRecharge);
            mRetryDetailRecharge = null;
        }
    }

    private boolean detailRechargeTrade() {
        final String rechargeOrderId = mMemberManager.getRechargeOrderId();
        if (TextUtils.isEmpty(rechargeOrderId)) {
            XLogger.e(TAG, AppConstants.LOG_BIZ_TRADE_PAYMENT + "查询会员充值订单详情---失败：缺少orderId参数");
            resultDetailRecharge = true;
            mMemberManager.tradeStatus = AppConstants.TRADE_STATUS_ERROR;
            mMemberManager.tradeStatusError = "缺少orderId参数";
            EventBus.getDefault().post(new RechargeDoneEvent());
            EventBus.getDefault().post(new TradeQrcodeCloseEvent());
        } else {
            XLogger.i(TAG, AppConstants.LOG_BIZ_MEMBER_MANAGER + "会员充值查询微信支付宝支付详情：" + RequestConstant.URL_GET_MEMBER_RECHARGE_DETAIL);
            callDetailRecharge = XmdNetwork.getInstance().getService(SpaService.class)
                    .detailMemberRecharge(AccountManager.getInstance().getToken(), rechargeOrderId);
            XmdNetwork.getInstance().requestSync(callDetailRecharge, new NetworkSubscriber<MemberRecordResult>() {
                @Override
                public void onCallbackSuccess(MemberRecordResult result) {
                    XLogger.i(TAG, AppConstants.LOG_BIZ_MEMBER_MANAGER + "会员充值查询微信支付宝支付详情---成功：" + rechargeOrderId);
                    PosFactory.getCurrentCashier().speech("充值成功");
                    resultDetailRecharge = true;
                    mMemberManager.tradeStatus = AppConstants.TRADE_STATUS_SUCCESS;
                    mMemberManager.recordInfo = result.getRespData();
                    EventBus.getDefault().post(new RechargeDoneEvent());
                    EventBus.getDefault().post(new TradeQrcodeCloseEvent());
                }

                @Override
                public void onCallbackError(Throwable e) {
                    XLogger.e(TAG, AppConstants.LOG_BIZ_MEMBER_MANAGER + "会员充值查询微信支付宝支付详情---失败：" + e.getLocalizedMessage());
                    if ((e instanceof NetworkException) && !e.getLocalizedMessage().equals("Canceled")) {
                        XToast.show("网络状况不佳，正在努力加载...");
                    }
                    resultDetailRecharge = false;
                }
            });
        }
        return resultDetailRecharge;
    }


    // **************************************轮询整合后的接口*************************************
    private Call<TradeOrderInfoResult> callTradeOrder;
    private RetryPool.RetryRunnable mRetryTradeOrder;
    private boolean resultTradeOrder = false;

    public void startTradeOrder() {
        mRetryTradeOrder = new RetryPool.RetryRunnable(1000, 1.0f, new RetryPool.RetryExecutor() {
            @Override
            public boolean run() {
                return checkTradeOrder();
            }
        });
        RetryPool.getInstance().postWork(mRetryTradeOrder);
    }

    public void stopTradeOrder() {
        if (callTradeOrder != null && !callTradeOrder.isCanceled()) {
            callTradeOrder.cancel();
        }
        if (mRetryTradeOrder != null) {
            RetryPool.getInstance().removeWork(mRetryTradeOrder);
            mRetryTradeOrder = null;
        }
    }

    private boolean checkTradeOrder() {
        final String orderId = mTradeManager.getCurrentTrade().payOrderId;
        if (TextUtils.isEmpty(orderId)) {
            XLogger.e(TAG, AppConstants.LOG_BIZ_TRADE_PAYMENT + "查询订单---失败：缺少payOrderId参数");
            resultTradeOrder = true;
            mTradeManager.getCurrentTrade().tradeStatus = AppConstants.TRADE_STATUS_ERROR;
            mTradeManager.getCurrentTrade().tradeStatusError = "交易出现未知异常，缺少必要参数";
            EventBus.getDefault().post(new TradeQrcodeCloseEvent());
        } else {
            XLogger.i(TAG, AppConstants.LOG_BIZ_TRADE_PAYMENT + "查询订单：" + orderId);
            callTradeOrder = XmdNetwork.getInstance().getService(SpaService.class)
                    .checkHoleOrder(AccountManager.getInstance().getToken(), orderId, mTradeManager.getCurrentTrade().payNo);
            XmdNetwork.getInstance().requestSync(callTradeOrder, new NetworkSubscriber<TradeOrderInfoResult>() {
                @Override
                public void onCallbackSuccess(TradeOrderInfoResult result) {
                    String scanStatus = result.getRespData().scanStatus;
                    String payStatus = result.getRespData().payStatus;
                    XLogger.i(TAG, AppConstants.LOG_BIZ_TRADE_PAYMENT + "查询订单---成功：[" + orderId + "][扫码状态：" + scanStatus + "][支付状态：" + payStatus + "]");

                    if (AppConstants.APP_REQUEST_YES.equals(scanStatus)) {
                        EventBus.getDefault().post(new QRScanStatusEvent());
                    }

                    if (AppConstants.APP_REQUEST_YES.equals(payStatus)) {
                        resultTradeOrder = true;
                        mTradeManager.getCurrentTrade().tradeStatus = AppConstants.TRADE_STATUS_SUCCESS;
                        mTradeManager.getCurrentTrade().resultOrderInfo = result.getRespData().orderDetail;
                        PosFactory.getCurrentCashier().speech("支付成功");
                        EventBus.getDefault().post(new TradeDoneEvent(mTradeType));
                        EventBus.getDefault().post(new TradeQrcodeCloseEvent());
                    } else {
                        resultTradeOrder = false;
                    }
                }

                @Override
                public void onCallbackError(Throwable e) {
                    XLogger.i(TAG, AppConstants.LOG_BIZ_TRADE_PAYMENT + "查询订单---失败：" + e.getLocalizedMessage());
                    if ((e instanceof NetworkException) && !e.getLocalizedMessage().equals("Canceled")) {
                        XToast.show("网络状况不佳，正在努力加载...");
                    }
                    resultTradeOrder = false;
                }
            });
        }
        return resultTradeOrder;
    }
}
