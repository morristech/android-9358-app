package com.xmd.cashier.presenter;

import android.content.Context;
import android.content.DialogInterface;

import com.xmd.cashier.R;
import com.xmd.cashier.cashier.PosFactory;
import com.xmd.cashier.common.AppConstants;
import com.xmd.cashier.common.Utils;
import com.xmd.cashier.contract.TradeMarkPayContract;
import com.xmd.cashier.dal.event.TradeDoneEvent;
import com.xmd.cashier.manager.Callback;
import com.xmd.cashier.manager.TradeManager;
import com.xmd.cashier.widget.CustomAlertDialogBuilder;
import com.xmd.m.network.BaseBean;

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
    private TradeManager mTradeManager;

    public TradeMarkPayPresenter(Context context, TradeMarkPayContract.View view) {
        mContext = context;
        mView = view;
        mView.setPresenter(this);
        mTradeManager = TradeManager.getInstance();
    }

    @Override
    public void onCreate() {
        mView.showAmount(String.format(mContext.getResources().getString(R.string.cashier_money), Utils.moneyToStringEx(mTradeManager.getCurrentTrade().getWillPayMoney())));
        mView.showChannelName(mTradeManager.getCurrentTrade().currentChannelName);
        mView.showChannelDesc(mTradeManager.getCurrentTrade().currentChannelMark);
    }

    @Override
    public void onStart() {

    }

    @Override
    public void onDestroy() {
        if (mMarkPaySubscription != null) {
            mMarkPaySubscription.unsubscribe();
        }
    }

    @Override
    public void onMarkPay() {
        if (!Utils.isNetworkEnabled(mContext)) {
            mView.showError(mContext.getString(R.string.network_disabled));
            return;
        }
        mView.showLoading();
        if (mMarkPaySubscription != null) {
            mMarkPaySubscription.unsubscribe();
        }
        mMarkPaySubscription = mTradeManager.callbackBatchOrder(new Callback<BaseBean>() {
            @Override
            public void onSuccess(BaseBean o) {
                mView.hideLoading();
                mTradeManager.getCurrentTrade().tradeStatus = AppConstants.TRADE_STATUS_SUCCESS;
                PosFactory.getCurrentCashier().speech("支付成功");
                EventBus.getDefault().post(new TradeDoneEvent(mView.getType()));
                mView.finishSelf();
            }

            @Override
            public void onError(String error) {
                mView.hideLoading();
                mView.showToast(error);
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
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("退出交易", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
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
