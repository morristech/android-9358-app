package com.xmd.cashier.presenter;

import android.content.Context;
import android.content.DialogInterface;

import com.shidou.commonlibrary.helper.XLogger;
import com.xmd.cashier.R;
import com.xmd.cashier.cashier.PosFactory;
import com.xmd.cashier.common.AppConstants;
import com.xmd.cashier.common.Utils;
import com.xmd.cashier.contract.TradeMarkPayContract;
import com.xmd.cashier.dal.event.RechargeDoneEvent;
import com.xmd.cashier.dal.event.TradeDoneEvent;
import com.xmd.cashier.dal.net.response.MemberRecordResult;
import com.xmd.cashier.dal.net.response.TradeOrderInfoResult;
import com.xmd.cashier.manager.Callback;
import com.xmd.cashier.manager.MemberManager;
import com.xmd.cashier.manager.TradeManager;
import com.xmd.cashier.widget.CustomAlertDialogBuilder;

import org.greenrobot.eventbus.EventBus;

import rx.Subscription;

/**
 * Created by zr on 17-8-17.
 */

public class TradeMarkPayPresenter implements TradeMarkPayContract.Presenter {
    private static final String TAG = "TradeMarkPayPresenter";
    private Context mContext;
    private TradeMarkPayContract.View mView;
    private Subscription mMarkPaySubscription;
    private Subscription mMarkRechargeSubscription;
    private TradeManager mTradeManager;
    private MemberManager mMemberManager;

    public TradeMarkPayPresenter(Context context, TradeMarkPayContract.View view) {
        mContext = context;
        mView = view;
        mView.setPresenter(this);
        mTradeManager = TradeManager.getInstance();
        mMemberManager = MemberManager.getInstance();
    }

    @Override
    public void onCreate() {
        switch (mView.getType()) {
            case AppConstants.TRADE_TYPE_NORMAL:
            case AppConstants.TRADE_TYPE_INNER:
                showCashierInfo();
                break;
            case AppConstants.TRADE_TYPE_RECHARGE:
                showRechargeInfo();
                break;
            default:
                break;
        }
    }

    private void showCashierInfo() {
        mView.showAmount(String.format(mContext.getResources().getString(R.string.cashier_money), Utils.moneyToStringEx(mTradeManager.getCurrentTrade().getWillPayMoney())));
        mView.showChannelName(mTradeManager.getCurrentTrade().currentChannelName);
        mView.showChannelDesc(mTradeManager.getCurrentTrade().currentChannelMark);
    }

    private void showRechargeInfo() {
        switch (mMemberManager.getAmountType()) {
            case AppConstants.MEMBER_RECHARGE_AMOUNT_TYPE_MONEY:    // 充值金额
                mView.showAmount(String.format(mContext.getResources().getString(R.string.cashier_money), Utils.moneyToStringEx(mMemberManager.getAmount())));
                break;
            case AppConstants.MEMBER_RECHARGE_AMOUNT_TYPE_PACKAGE:  // 充值套餐
                mView.showAmount(String.format(mContext.getResources().getString(R.string.cashier_money), Utils.moneyToStringEx(mMemberManager.getPackageInfo().amount)));
                break;
            case AppConstants.MEMBER_RECHARGE_AMOUNT_TYPE_NONE:
            default:
                break;
        }
        mView.showChannelName(mMemberManager.currentChannelName);
        mView.showChannelDesc(mMemberManager.currentChannelMark);
    }

    @Override
    public void onStart() {

    }

    @Override
    public void onDestroy() {
        if (mMarkPaySubscription != null) {
            mMarkPaySubscription.unsubscribe();
        }
        if (mMarkRechargeSubscription != null) {
            mMarkRechargeSubscription.unsubscribe();
        }
    }

    @Override
    public void onMark() {
        switch (mView.getType()) {
            case AppConstants.TRADE_TYPE_NORMAL:
            case AppConstants.TRADE_TYPE_INNER:
                markPay();
                break;
            case AppConstants.TRADE_TYPE_RECHARGE:
                markRecharge();
                break;
            default:
                break;
        }
    }

    private void markPay() {
        if (!Utils.isNetworkEnabled(mContext)) {
            mView.showError(mContext.getString(R.string.network_disabled));
            return;
        }
        mView.showLoading();
        XLogger.i(TAG, AppConstants.LOG_BIZ_TRADE_PAYMENT + "交易订单记账支付");
        if (mMarkPaySubscription != null) {
            mMarkPaySubscription.unsubscribe();
        }
        mMarkPaySubscription = mTradeManager.callbackBatchOrder(new Callback<TradeOrderInfoResult>() {
            @Override
            public void onSuccess(TradeOrderInfoResult o) {
                XLogger.i(TAG, AppConstants.LOG_BIZ_TRADE_PAYMENT + "交易订单记账支付---成功");
                mView.hideLoading();
                mTradeManager.getCurrentTrade().tradeStatus = AppConstants.TRADE_STATUS_SUCCESS;
                mTradeManager.getCurrentTrade().resultOrderInfo = o.getRespData().orderDetail;
                PosFactory.getCurrentCashier().speech("支付成功");
                EventBus.getDefault().post(new TradeDoneEvent(mView.getType()));
                mView.finishSelf();
            }

            @Override
            public void onError(String error) {
                XLogger.i(TAG, AppConstants.LOG_BIZ_TRADE_PAYMENT + "交易订单记账支付---失败：" + error);
                mView.hideLoading();
                mView.showToast(error);
            }
        });
    }

    private void markRecharge() {
        if (!Utils.isNetworkEnabled(mContext)) {
            mView.showError(mContext.getString(R.string.network_disabled));
            return;
        }
        mView.showLoading();
        XLogger.i(TAG, AppConstants.LOG_BIZ_TRADE_PAYMENT + "会员充值记账支付");
        if (mMarkRechargeSubscription != null) {
            mMarkRechargeSubscription.unsubscribe();
        }
        mMarkRechargeSubscription = mMemberManager.callbackRechargeOrder(mMemberManager.getRechargeOrderId(), mMemberManager.currentChannelType, new Callback<MemberRecordResult>() {
            @Override
            public void onSuccess(MemberRecordResult o) {
                XLogger.i(TAG, AppConstants.LOG_BIZ_MEMBER_MANAGER + "会员充值记账支付---成功");
                PosFactory.getCurrentCashier().speech("充值成功");
                mView.hideLoading();
                mMemberManager.tradeStatus = AppConstants.TRADE_STATUS_SUCCESS;
                mMemberManager.recordInfo = o.getRespData();
                EventBus.getDefault().post(new RechargeDoneEvent());
                mView.finishSelf();
            }

            @Override
            public void onError(String error) {
                XLogger.e(TAG, AppConstants.LOG_BIZ_MEMBER_MANAGER + "会员充值记账支付---失败：" + error);
                mView.hideLoading();
                mView.showToast("充值失败：" + error);
            }
        });
    }

    @Override
    public void onNavigationBack() {
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
                        dialog.dismiss();
                        XLogger.i(TAG, AppConstants.LOG_BIZ_TRADE_PAYMENT + "选择退出交易");
                        switch (mView.getType()) {
                            case AppConstants.TRADE_TYPE_NORMAL:
                            case AppConstants.TRADE_TYPE_INNER:
                                mTradeManager.getCurrentTrade().tradeStatus = AppConstants.TRADE_STATUS_ERROR;
                                mTradeManager.getCurrentTrade().tradeStatusError = "已取消交易";
                                EventBus.getDefault().post(new TradeDoneEvent(mView.getType()));
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
}
