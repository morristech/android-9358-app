package com.xmd.cashier.presenter;

import android.content.Context;

import com.shidou.commonlibrary.helper.XLogger;
import com.xmd.cashier.common.AppConstants;
import com.xmd.cashier.common.Utils;
import com.xmd.cashier.contract.CashierResultContract;
import com.xmd.cashier.dal.bean.TradeRecordInfo;
import com.xmd.cashier.manager.TradeManager;

/**
 * Created by zr on 17-5-16.
 */

public class CashierResultPresenter implements CashierResultContract.Presenter {
    private static final String TAG = "CashierResultPresenter";
    private Context mContext;
    private CashierResultContract.View mView;
    private TradeManager mTradeManager;

    public CashierResultPresenter(Context context, CashierResultContract.View view) {
        mContext = context;
        mView = view;
        mView.setPresenter(this);
        mTradeManager = TradeManager.getInstance();
    }

    @Override
    public void onCreate() {
        switch (mTradeManager.getCurrentTrade().tradeStatus) {
            case AppConstants.TRADE_STATUS_SUCCESS:
                XLogger.i(TAG, AppConstants.LOG_BIZ_NORMAL_CASHIER + "补收款交易状态：成功");
                mView.statusSuccess("收款金额：￥" + Utils.moneyToStringEx(mTradeManager.getCurrentTrade().getWillPayMoney()));
                TradeRecordInfo recordInfo = mTradeManager.getCurrentTrade().resultOrderInfo;
                if (recordInfo != null) {
                    mTradeManager.printTradeRecordInfoAsync(recordInfo, false);
                }
                break;
            case AppConstants.TRADE_STATUS_ERROR:
                XLogger.i(TAG, AppConstants.LOG_BIZ_NORMAL_CASHIER + "补收款交易状态：失败或取消");
                mView.statusError(mTradeManager.getCurrentTrade().tradeStatusError);
                break;
            case AppConstants.TRADE_STATUS_EXCEPTION:
                XLogger.i(TAG, AppConstants.LOG_BIZ_NORMAL_CASHIER + "补收款交易状态：出现异常");
                mView.statusException();
                break;
            default:
                XLogger.i(TAG, AppConstants.LOG_BIZ_NORMAL_CASHIER + "补收款交易状态：其他未知状态");
                break;
        }
    }

    @Override
    public void onStart() {

    }

    @Override
    public void onDestroy() {

    }

    @Override
    public void onConfirm() {
        mTradeManager.newTrade();
        mView.finishSelf();
    }
}
