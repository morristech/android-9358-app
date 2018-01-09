package com.xmd.cashier.presenter;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.View;

import com.shidou.commonlibrary.helper.RetryPool;
import com.shidou.commonlibrary.helper.XLogger;
import com.xmd.cashier.UiNavigation;
import com.xmd.cashier.common.AppConstants;
import com.xmd.cashier.common.Utils;
import com.xmd.cashier.contract.InnerPaymentContract;
import com.xmd.cashier.dal.bean.GiftActivityInfo;
import com.xmd.cashier.dal.bean.Trade;
import com.xmd.cashier.dal.net.RequestConstant;
import com.xmd.cashier.dal.net.SpaService;
import com.xmd.cashier.dal.net.response.GiftActivityResult;
import com.xmd.cashier.dal.net.response.StringResult;
import com.xmd.cashier.manager.AccountManager;
import com.xmd.cashier.manager.Callback;
import com.xmd.cashier.manager.TradeManager;
import com.xmd.cashier.widget.CustomAlertDialogBuilder;
import com.xmd.m.network.BaseBean;
import com.xmd.m.network.NetworkSubscriber;
import com.xmd.m.network.XmdNetwork;

import retrofit2.Call;
import rx.Observable;
import rx.Subscription;

/**
 * Created by zr on 17-11-4.
 */

public class InnerPaymentPresenter implements InnerPaymentContract.Presenter {
    private static final String TAG = "InnerPaymentPresenter";
    private Context mContext;
    private InnerPaymentContract.View mView;
    private TradeManager mTradeManager;

    private GiftActivityInfo mGiftActivityInfo;
    private Subscription mGetGiftPayActivitySubscription;

    private Subscription mDoPayCallBackSubscription;

    private Call<StringResult> getPaymentStatus;
    private RetryPool.RetryRunnable mRetryPaymentInfo;
    private boolean resultPaymentInfo = false;

    public void startGetPaymentInfo() {
        mRetryPaymentInfo = new RetryPool.RetryRunnable(AppConstants.DEFAULT_INTERVAL, 1.0f, new RetryPool.RetryExecutor() {
            @Override
            public boolean run() {
                return checkPaymentStatus();
            }
        });
        RetryPool.getInstance().postWork(mRetryPaymentInfo);
    }

    public void stopGetPaymentInfo() {
        if (getPaymentStatus != null && !getPaymentStatus.isCanceled()) {
            getPaymentStatus.cancel();
        }
        if (mRetryPaymentInfo != null) {
            RetryPool.getInstance().removeWork(mRetryPaymentInfo);
            mRetryPaymentInfo = null;
        }
    }

    private boolean checkPaymentStatus() {
        XLogger.i(TAG, AppConstants.LOG_BIZ_NATIVE_CASHIER + "内网订单微信支付宝支付查询订单状态：" + RequestConstant.URL_CHECK_INNER_SUB_PAY_STATUS);
        getPaymentStatus = XmdNetwork.getInstance().getService(SpaService.class)
                .checkInnerSubPayStatus(AccountManager.getInstance().getToken(), mTradeManager.getCurrentTrade().payOrderId, mTradeManager.getCurrentTrade().subPayOrderId);
        XmdNetwork.getInstance().requestSync(getPaymentStatus, new NetworkSubscriber<StringResult>() {
            @Override
            public void onCallbackSuccess(StringResult result) {
                XLogger.i(TAG, AppConstants.LOG_BIZ_NATIVE_CASHIER + "内网订单微信支付宝支付查询订单状态---成功:" + result.getRespData());
                if (AppConstants.APP_REQUEST_YES.equals(result.getRespData())) {
                    // 支付成功
                    resultPaymentInfo = true;
                    mTradeManager.getCurrentTrade().tradeStatus = AppConstants.TRADE_STATUS_SUCCESS;
                    UiNavigation.gotoInnerResultActivity(mContext);
                    mView.finishSelf();
                } else {
                    // 尚未支付
                    resultPaymentInfo = false;
                }
            }

            @Override
            public void onCallbackError(Throwable e) {
                XLogger.e(TAG, AppConstants.LOG_BIZ_NATIVE_CASHIER + "内网订单微信支付宝支付查询订单状态---失败:" + e.getLocalizedMessage());
                resultPaymentInfo = false;
            }
        });
        return resultPaymentInfo;
    }

    public InnerPaymentPresenter(Context context, InnerPaymentContract.View view) {
        mTradeManager = TradeManager.getInstance();
        mContext = context;
        mView = view;
        mView.setPresenter(this);
    }

    @Override
    public void onCreate() {
        mTradeManager = TradeManager.getInstance();
        XLogger.i(TAG, AppConstants.LOG_BIZ_NATIVE_CASHIER + "内网订单支付方式:" + mTradeManager.getCurrentTrade().currentCashier);
//        mView.setOrigin(Utils.moneyToStringEx(mTradeManager.getCurrentTrade().getOriginMoney()));
//        mView.setDiscount(Utils.moneyToStringEx(mTradeManager.getCurrentTrade().getWillDiscountMoney()
//                + mTradeManager.getCurrentTrade().getAlreadyDiscountMoney()
//                + mTradeManager.getCurrentTrade().getWillReductionMoney()));
        switch (mTradeManager.getCurrentTrade().currentCashier) {
            case AppConstants.CASHIER_TYPE_QRCODE:
                mView.initScanStub();
                mView.setScanPaid("￥" + Utils.moneyToStringEx(mTradeManager.getCurrentTrade().getRealPayMoney()));
                if (TextUtils.isEmpty(mTradeManager.getCurrentTrade().payUrl)) {
                    XLogger.i(TAG, AppConstants.LOG_BIZ_NATIVE_CASHIER + "内网订单微信支付宝支付获取二维码失败");
                    mView.showToast("获取二维码失败");
                } else {
                    Bitmap bitmap;
                    try {
                        bitmap = Utils.getQRBitmap(mTradeManager.getCurrentTrade().payUrl);
                    } catch (Exception e) {
                        bitmap = null;
                    }
                    if (bitmap == null) {
                        // 解析失败
                        XLogger.e(TAG, AppConstants.LOG_BIZ_NATIVE_CASHIER + "内网订单微信支付宝支付二维码解析失败");
                        mView.showToast("二维码解析失败");
                    } else {
                        XLogger.i(TAG, AppConstants.LOG_BIZ_NATIVE_CASHIER + "内网订单微信支付宝支付展示二维码");
                        mView.setQrcode(bitmap);
                        startGetPaymentInfo();
                    }
                }
                getPayGiftActivity();
                break;
            case AppConstants.CASHIER_TYPE_CASH:
            case AppConstants.CASHIER_TYPE_MARK:
                mView.initMarkStub();
                mView.setMarkPaid("￥" + Utils.moneyToStringEx(mTradeManager.getCurrentTrade().getRealPayMoney()));
                mView.setMarkName(mTradeManager.getCurrentTrade().currentCashierName);
                mView.setMarkDesc(mTradeManager.getCurrentTrade().currentCashierMark);
                break;
            case AppConstants.CASHIER_TYPE_MEMBER:
                mView.initMemberStub();
                mView.setMemberInfo(mTradeManager.getCurrentTrade().memberInfo);
                mView.setMemberOrigin(Utils.moneyToStringEx(mTradeManager.getCurrentTrade().getRealPayMoney()));
                int payMoney = (int) (mTradeManager.getCurrentTrade().getRealPayMoney() * (mTradeManager.getCurrentTrade().memberInfo.discount / 1000.0f)); //计算折扣
                int discountMoney = (int) (mTradeManager.getCurrentTrade().getRealPayMoney() * (1000 - mTradeManager.getCurrentTrade().memberInfo.discount) / 1000.0f);
                mView.setMemberDiscount("-" + Utils.moneyToStringEx(discountMoney));
                mView.setMemberPaid(Utils.moneyToStringEx(payMoney));
                mView.setConfirmEnable(mTradeManager.getCurrentTrade().memberInfo.amount >= payMoney);
                break;
            default:
                break;
        }
    }

    @Override
    public void onStart() {

    }

    @Override
    public void onDestroy() {
        stopGetPaymentInfo();
        if (mGetGiftPayActivitySubscription != null) {
            mGetGiftPayActivitySubscription.unsubscribe();
        }
        if (mDoPayCallBackSubscription != null) {
            mDoPayCallBackSubscription.unsubscribe();
        }
    }

    public void getPayGiftActivity() {
        if (mGetGiftPayActivitySubscription != null) {
            mGetGiftPayActivitySubscription.unsubscribe();
        }
        Observable<GiftActivityResult> observable = XmdNetwork.getInstance().getService(SpaService.class)
                .getGiftActivity(AccountManager.getInstance().getToken(), AccountManager.getInstance().getClubId());
        mGetGiftPayActivitySubscription = XmdNetwork.getInstance().request(observable, new NetworkSubscriber<GiftActivityResult>() {
            @Override
            public void onCallbackSuccess(GiftActivityResult result) {
                if (result != null && result.getRespData() != null) {
                    mGiftActivityInfo = result.getRespData();
                    mView.setPayActivity(View.VISIBLE);
                }
            }

            @Override
            public void onCallbackError(Throwable e) {
                XLogger.e("获取买单有礼活动失败:" + e.getLocalizedMessage());
            }
        });
    }

    @Override
    public void onMarkConfirm() {
        doCallBack();
    }

    @Override
    public void onMemberConfirm() {
        doCallBack();
    }

    @Override
    public void onPayActivityShow() {
        UiNavigation.gotoGiftActActivity(mContext, mGiftActivityInfo);
    }

    @Override
    public void onEventBack() {
        XLogger.i(TAG, AppConstants.LOG_BIZ_NATIVE_CASHIER + "内网订单中途取消交易?");
        final Trade trade = mTradeManager.getCurrentTrade();
        new CustomAlertDialogBuilder(mContext)
                .setMessage("确认退出此次交易?")
                .setPositiveButton("继续交易", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        XLogger.i(TAG, AppConstants.LOG_BIZ_NATIVE_CASHIER + "内网订单选择继续交易");
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("退出交易", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        XLogger.i(TAG, AppConstants.LOG_BIZ_NATIVE_CASHIER + "内网订单选择退出交易");
                        dialog.dismiss();
                        trade.tradeStatus = AppConstants.TRADE_STATUS_CANCEL;
                        UiNavigation.gotoInnerResultActivity(mContext);
                        mView.finishSelf();
                    }
                })
                .create()
                .show();
    }

    private void doCallBack() {
        XLogger.i(TAG, AppConstants.LOG_BIZ_NATIVE_CASHIER + "内网订单支付标记支付结果：" + RequestConstant.URL_CALLBACK_INNER_BATCH_ORDER);
        mView.showLoading();
        if (mDoPayCallBackSubscription != null) {
            mDoPayCallBackSubscription.unsubscribe();
        }
        Trade trade = mTradeManager.getCurrentTrade();
        mDoPayCallBackSubscription = mTradeManager.callbackInnerBatch(trade.payOrderId, trade.subPayOrderId, String.valueOf(trade.getRealPayMoney()), trade.currentCashierType, trade.memberId, null, new Callback<BaseBean>() {
            @Override
            public void onSuccess(BaseBean o) {
                XLogger.i(TAG, AppConstants.LOG_BIZ_NATIVE_CASHIER + "内网订单支付标记支付结果---成功");
                mView.hideLoading();
                mTradeManager.getCurrentTrade().tradeStatus = AppConstants.TRADE_STATUS_SUCCESS;
                UiNavigation.gotoInnerResultActivity(mContext);
                mView.finishSelf();
            }

            @Override
            public void onError(String error) {
                XLogger.e(TAG, AppConstants.LOG_BIZ_NATIVE_CASHIER + "内网订单支付标记支付结果---失败:" + error);
                mView.hideLoading();
                mView.showError(error);
            }
        });
    }
}
