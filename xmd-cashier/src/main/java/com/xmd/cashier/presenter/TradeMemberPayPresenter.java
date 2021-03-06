package com.xmd.cashier.presenter;

import android.content.Context;
import android.content.DialogInterface;

import com.shidou.commonlibrary.helper.XLogger;
import com.xmd.cashier.R;
import com.xmd.cashier.cashier.PosFactory;
import com.xmd.cashier.common.AppConstants;
import com.xmd.cashier.common.Utils;
import com.xmd.cashier.contract.TradeMemberPayContract;
import com.xmd.cashier.dal.bean.Trade;
import com.xmd.cashier.dal.event.TradeDoneEvent;
import com.xmd.cashier.dal.net.response.TradeOrderInfoResult;
import com.xmd.cashier.manager.Callback;
import com.xmd.cashier.manager.TradeManager;
import com.xmd.cashier.widget.CustomAlertDialogBuilder;

import org.greenrobot.eventbus.EventBus;

import rx.Subscription;

/**
 * Created by zr on 17-7-22.
 */

public class TradeMemberPayPresenter implements TradeMemberPayContract.Presenter {
    private static final String TAG = "TradeMemberPayPresenter";
    private Context mContext;
    private TradeMemberPayContract.View mView;
    private Subscription mMemberPaySubscription;
    private TradeManager mTradeManager;

    public TradeMemberPayPresenter(Context context, TradeMemberPayContract.View view) {
        mContext = context;
        mView = view;
        mView.setPresenter(this);
        mTradeManager = TradeManager.getInstance();
    }

    @Override
    public void onCreate() {
        Trade trade = mTradeManager.getCurrentTrade();
        mView.showInfo(trade.memberInfo);
        mView.showOriginAmount(Utils.moneyToStringEx(trade.getWillPayMoney()));
    }

    @Override
    public void onStart() {
        Trade trade = mTradeManager.getCurrentTrade();
        int payMoney = (int) (trade.getWillPayMoney() * (trade.memberInfo.discount / 1000.0f)); //计算折扣
        int discountMoney = (int) (trade.getWillPayMoney() * (1000 - trade.memberInfo.discount) / 1000.0f);
        mView.showDiscountAmount(Utils.moneyToStringEx(discountMoney));
        mView.showNeedAmount(Utils.moneyToStringEx(payMoney));
        mView.showButton(trade.memberInfo.amount >= payMoney);
    }

    @Override
    public void onDestroy() {
        if (mMemberPaySubscription != null) {
            mMemberPaySubscription.unsubscribe();
        }
    }

    @Override
    public void onMemberPay() {
        if (!Utils.isNetworkEnabled(mContext)) {
            mView.showError(mContext.getString(R.string.network_disabled));
            return;
        }
        mView.showLoading();
        XLogger.i(TAG, AppConstants.LOG_BIZ_TRADE_PAYMENT + "交易订单会员支付");
        if (mMemberPaySubscription != null) {
            mMemberPaySubscription.unsubscribe();
        }
        mMemberPaySubscription = mTradeManager.callbackBatchOrder(new Callback<TradeOrderInfoResult>() {
            @Override
            public void onSuccess(TradeOrderInfoResult o) {
                XLogger.i(TAG, AppConstants.LOG_BIZ_TRADE_PAYMENT + "交易订单会员支付---成功");
                mView.hideLoading();
                mTradeManager.getCurrentTrade().tradeStatus = AppConstants.TRADE_STATUS_SUCCESS;
                mTradeManager.getCurrentTrade().resultOrderInfo = o.getRespData().orderDetail;
                PosFactory.getCurrentCashier().speech("支付成功");
                EventBus.getDefault().post(new TradeDoneEvent(mView.getType()));
                mView.finishSelf();
            }

            @Override
            public void onError(String error) {
                XLogger.e(TAG, AppConstants.LOG_BIZ_TRADE_PAYMENT + "交易订单会员支付---失败：" + error);
                mView.hideLoading();
                mView.showToast("支付失败:" + error);
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
                        XLogger.i(TAG, AppConstants.LOG_BIZ_TRADE_PAYMENT + "选择退出交易");
                        dialog.dismiss();
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
