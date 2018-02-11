package com.xmd.cashier.presenter;

import android.content.Context;

import com.xmd.cashier.common.AppConstants;
import com.xmd.cashier.common.Utils;
import com.xmd.cashier.contract.PayTypeChoiceContract;
import com.xmd.cashier.manager.CashierManager;
import com.xmd.cashier.manager.TradeManager;

/**
 * Created by heyangya on 16-8-22.
 */

public class PayTypeChoicePresenter implements PayTypeChoiceContract.Presenter {
    private Context mContext;
    private PayTypeChoiceContract.View mView;
    private CashierManager mCashierManager = CashierManager.getInstance();
    private int mPayType = AppConstants.PAY_TYPE_UNKNOWN;

    public PayTypeChoicePresenter(Context context, PayTypeChoiceContract.View view) {
        mContext = context;
        mView = view;
        mView.setPresenter(this);
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onStart() {
        mView.showNeedPayMoney(Utils.moneyToStringEx(TradeManager.getInstance().getCurrentTrade().getWillPayMoney()));
        mView.showPayType(mPayType);
        mView.setPayButtonEnable(false);
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public void onClickPay() {
        mCashierManager.onChoicePayType(mPayType);
        mView.finishSelf();
    }

    @Override
    public void onCancel() {
        mCashierManager.onCancelChoicePayType();
    }

    @Override
    public void onSelectPayType(int payType) {
        if (mPayType != payType) {
            mView.showPayType(payType);
            mPayType = payType;
        }
        if (payType != AppConstants.PAY_TYPE_UNKNOWN) {
            mView.setPayButtonEnable(true);
        }
    }
}
