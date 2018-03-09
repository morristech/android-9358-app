package com.xmd.cashier.presenter;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Handler;
import android.text.TextUtils;

import com.shidou.commonlibrary.helper.RetryPool;
import com.shidou.commonlibrary.helper.XLogger;
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
import com.xmd.cashier.dal.event.TradeDoneEvent;
import com.xmd.cashier.dal.net.SpaService;
import com.xmd.cashier.dal.net.response.GiftActivityResult;
import com.xmd.cashier.dal.net.response.StringResult;
import com.xmd.cashier.dal.sp.SPManager;
import com.xmd.cashier.manager.AccountManager;
import com.xmd.cashier.manager.Callback;
import com.xmd.cashier.manager.TradeManager;
import com.xmd.cashier.widget.CustomAlertDialogBuilder;
import com.xmd.m.network.BaseBean;
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

    private GiftActivityInfo mGiftActivityInfo;
    private Subscription mGetGiftActivitySubscription;
    private Subscription mActiveAuthCodePaySubscription;

    private TradeManager mTradeManager;

    private Handler mHandler;

    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            PosFactory.getCurrentCashier().speech("请扫描客户支付二维码");
            mHandler.postDelayed(mRunnable, 15 * 1000);
        }
    };


    public TradeQrcodePayPresenter(Context context, TradeQrcodePayContract.View view) {
        mContext = context;
        mView = view;
        mView.setPresenter(this);
        mTradeManager = TradeManager.getInstance();
        mHandler = new Handler();
    }

    @Override
    public void onCreate() {
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

    private void initBitmap() {
        PosFactory.getCurrentCashier().speech("请扫描屏幕中二维码");
        // 轮询扫码状态
        startGetScanStatus();
        // 轮询支付状态
        startGetPayStatus();
    }

    private void initAuth() {
        mHandler.post(mRunnable);
    }

    @Override
    public void onStart() {

    }

    @Override
    public void onDestroy() {
        mHandler.removeCallbacks(mRunnable);
        stopGetScanStatus();
        stopGetPayStatus();
        if (mGetGiftActivitySubscription != null) {
            mGetGiftActivitySubscription.unsubscribe();
        }
        if (mActiveAuthCodePaySubscription != null) {
            mActiveAuthCodePaySubscription.unsubscribe();
        }
    }

    // **************** 二维码授权支付 ****************
    @Override
    public void authPay(String authCode) {
        XLogger.i(TAG, AppConstants.LOG_BIZ_TRADE_PAYMENT + "二维码授权支付：" + authCode);
        mHandler.removeCallbacks(mRunnable);
        mView.showLoading();
        if (mActiveAuthCodePaySubscription != null) {
            mActiveAuthCodePaySubscription.unsubscribe();
        }
        mActiveAuthCodePaySubscription = mTradeManager.activeAuthPay(
                mTradeManager.getCurrentTrade().getWillPayMoney(),
                authCode,
                mTradeManager.getCurrentTrade().payOrderId,
                mTradeManager.getCurrentTrade().payNo,
                new Callback<BaseBean>() {
                    @Override
                    public void onSuccess(BaseBean o) {
                        XLogger.i(TAG, AppConstants.LOG_BIZ_TRADE_PAYMENT + "二维码授权支付---成功");
                        mView.hideLoading();
                        PosFactory.getCurrentCashier().speech("支付成功");
                        mTradeManager.getCurrentTrade().tradeStatus = AppConstants.TRADE_STATUS_SUCCESS;
                        EventBus.getDefault().post(new TradeDoneEvent(mView.getType()));
                        mView.finishSelf();
                    }

                    @Override
                    public void onError(String error) {
                        XLogger.e(TAG, AppConstants.LOG_BIZ_TRADE_PAYMENT + "二维码授权---失败：" + error);
                        mView.hideLoading();
                        mTradeManager.getCurrentTrade().tradeStatus = AppConstants.TRADE_STATUS_ERROR;
                        mTradeManager.getCurrentTrade().tradeStatusError = error;
                        EventBus.getDefault().post(new TradeDoneEvent(mView.getType()));
                        mView.finishSelf();
                    }
                });
    }

    @Override
    public void onAuthClick() {
        XLogger.i(TAG, AppConstants.LOG_BIZ_TRADE_PAYMENT + "选择主扫");
        stopGetPayStatus();
        stopGetScanStatus();
        mHandler.post(mRunnable);
    }

    @Override
    public void onBitmapClick() {
        XLogger.i(TAG, AppConstants.LOG_BIZ_TRADE_PAYMENT + "选择被扫");
        startGetPayStatus();
        startGetScanStatus();
        mHandler.removeCallbacks(mRunnable);
    }

    // ****************************************轮询扫码状态******************************************
    private Call<StringResult> getScanStatusCall;
    private RetryPool.RetryRunnable mRetryScanStatus;
    private boolean resultScanStatus = false;

    private void startGetScanStatus() {
        if (mRetryScanStatus == null) {
            mRetryScanStatus = new RetryPool.RetryRunnable(AppConstants.DEFAULT_INTERVAL, 1.0f, new RetryPool.RetryExecutor() {
                @Override
                public boolean run() {
                    return getScanStatus();
                }
            });
        }
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
        String payOrderId = mTradeManager.getCurrentTrade().payOrderId;
        if (TextUtils.isEmpty(payOrderId)) {
            XLogger.e(TAG, AppConstants.LOG_BIZ_TRADE_PAYMENT + "查询二维码扫描状态---失败：缺少payOrderId参数");
            resultScanStatus = true;
        } else {
            XLogger.i(TAG, AppConstants.LOG_BIZ_TRADE_PAYMENT + "查询二维码扫描状态：" + payOrderId);
            getScanStatusCall = XmdNetwork.getInstance().getService(SpaService.class)
                    .checkScanStatus(AccountManager.getInstance().getToken(), payOrderId);
            XmdNetwork.getInstance().requestSync(getScanStatusCall, new NetworkSubscriber<StringResult>() {
                @Override
                public void onCallbackSuccess(StringResult result) {
                    XLogger.i(TAG, AppConstants.LOG_BIZ_TRADE_PAYMENT + "查询二维码扫描状态---成功：" + result.getRespData());
                    if (AppConstants.APP_REQUEST_YES.equals(result.getRespData())) {
                        resultScanStatus = true;
                        EventBus.getDefault().post(new QRScanStatusEvent());
                    } else {
                        resultScanStatus = false;
                    }
                }

                @Override
                public void onCallbackError(Throwable e) {
                    XLogger.e(TAG, AppConstants.LOG_BIZ_TRADE_PAYMENT + "查询二维码扫描状态---失败：" + e.getLocalizedMessage());
                    resultScanStatus = false;
                }
            });
        }
        return resultScanStatus;
    }

    // ****************************************轮询买单详情******************************************
    private Call<StringResult> getPayStatusCall;
    private RetryPool.RetryRunnable mRetryPayStatus;
    private boolean resultPayStatus = false;

    private void startGetPayStatus() {
        if (mRetryPayStatus == null) {
            mRetryPayStatus = new RetryPool.RetryRunnable(AppConstants.TINNY_INTERVAL, 1.0f, new RetryPool.RetryExecutor() {
                @Override
                public boolean run() {
                    return getPayStatus();
                }
            });
        }
        RetryPool.getInstance().postWork(mRetryPayStatus);
    }

    private void stopGetPayStatus() {
        if (getPayStatusCall != null && !getPayStatusCall.isCanceled()) {
            getPayStatusCall.cancel();
        }
        if (mRetryPayStatus != null) {
            RetryPool.getInstance().removeWork(mRetryPayStatus);
            mRetryPayStatus = null;
        }
    }

    private boolean getPayStatus() {
        String payOrderId = mTradeManager.getCurrentTrade().payOrderId;
        if (TextUtils.isEmpty(payOrderId)) {
            XLogger.e(TAG, AppConstants.LOG_BIZ_TRADE_PAYMENT + "查询订单支付状态---失败：缺少payOrderId参数");
            resultPayStatus = true;
            mTradeManager.getCurrentTrade().tradeStatus = AppConstants.TRADE_STATUS_ERROR;
            mTradeManager.getCurrentTrade().tradeStatusError = "交易出现未知异常，缺少必要参数";
            EventBus.getDefault().post(new TradeDoneEvent(mView.getType()));
            mView.finishSelf();
        } else {
            XLogger.i(TAG, AppConstants.LOG_BIZ_TRADE_PAYMENT + "查询订单支付状态：" + payOrderId);
            getPayStatusCall = XmdNetwork.getInstance().getService(SpaService.class)
                    .checkPayStatus(AccountManager.getInstance().getToken(), payOrderId, mTradeManager.getCurrentTrade().payNo);
            XmdNetwork.getInstance().requestSync(getPayStatusCall, new NetworkSubscriber<StringResult>() {
                @Override
                public void onCallbackSuccess(StringResult result) {
                    XLogger.i(TAG, AppConstants.LOG_BIZ_TRADE_PAYMENT + "查询订单支付状态---成功：" + result.getRespData());
                    if (AppConstants.APP_REQUEST_YES.equals(result.getRespData())) {
                        resultPayStatus = true;
                        mTradeManager.getCurrentTrade().tradeStatus = AppConstants.TRADE_STATUS_SUCCESS;
                        PosFactory.getCurrentCashier().speech("支付成功");
                        EventBus.getDefault().post(new TradeDoneEvent(mView.getType()));
                        mView.finishSelf();
                    } else {
                        resultPayStatus = false;
                    }
                }

                @Override
                public void onCallbackError(Throwable e) {
                    XLogger.e(TAG, AppConstants.LOG_BIZ_TRADE_PAYMENT + "查询订单支付状态---失败：" + e.getLocalizedMessage());
                    resultPayStatus = false;
                }
            });
        }
        return resultPayStatus;
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
                        mHandler.removeCallbacks(mRunnable);
                        stopGetPayStatus();
                        stopGetScanStatus();
                        mTradeManager.getCurrentTrade().tradeStatus = AppConstants.TRADE_STATUS_ERROR;
                        mTradeManager.getCurrentTrade().tradeStatusError = "已取消交易";
                        EventBus.getDefault().post(new TradeDoneEvent(mView.getType()));
                        mView.finishSelf();
                    }
                })
                .create()
                .show();
    }
}
