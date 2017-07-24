package com.xmd.cashier.presenter;

import android.content.Context;

import com.xmd.cashier.R;
import com.xmd.cashier.UiNavigation;
import com.xmd.cashier.common.AppConstants;
import com.xmd.cashier.common.Utils;
import com.xmd.cashier.contract.MemberCashierContract;
import com.xmd.cashier.dal.bean.Trade;
import com.xmd.cashier.dal.net.response.MemberPayResult;
import com.xmd.cashier.dal.net.response.MemberRecordResult;
import com.xmd.cashier.manager.Callback;
import com.xmd.cashier.manager.Callback0;
import com.xmd.cashier.manager.TradeManager;

import rx.Subscription;

/**
 * Created by zr on 17-7-22.
 */

public class MemberCashierPresenter implements MemberCashierContract.Presenter {
    private Context mContext;
    private MemberCashierContract.View mView;

    private Subscription mMemberPayByCodeSubscription;
    private Subscription mMemberPayByScanSubscription;

    public MemberCashierPresenter(Context context, MemberCashierContract.View view) {
        mContext = context;
        mView = view;
        mView.setPresenter(this);
    }

    @Override
    public void onCreate() {
        Trade trade = TradeManager.getInstance().getCurrentTrade();
        mView.showInfo(trade.memberInfo);
        mView.showOriginAmount(Utils.moneyToStringEx(trade.getNeedPayMoney()));
    }

    @Override
    public void onStart() {
        Trade trade = TradeManager.getInstance().getCurrentTrade();
        int payMoney = (int) (trade.getNeedPayMoney() * (trade.memberInfo.discount / 1000.0f)); //计算折扣
        int discountMoney = (int) (trade.getNeedPayMoney() * (1000 - trade.memberInfo.discount) / 1000.0f);
        trade.memberNeedPayMoney = trade.getNeedPayMoney();
        mView.showDiscountAmount(Utils.moneyToStringEx(discountMoney));
        mView.showNeedAmount(Utils.moneyToStringEx(payMoney));
        mView.showButton(trade.memberInfo.amount >= payMoney);
    }

    @Override
    public void onDestroy() {
        if (mMemberPayByCodeSubscription != null) {
            mMemberPayByCodeSubscription.unsubscribe();
        }

        if (mMemberPayByScanSubscription != null) {
            mMemberPayByScanSubscription.unsubscribe();
        }
    }

    @Override
    public void onMemberPay() {
        switch (TradeManager.getInstance().getCurrentTrade().memberPayMethod) {
            case AppConstants.MEMBER_PAY_METHOD_CODE:
                // 接口
                doPayByCode();
                break;
            case AppConstants.MEMBER_PAY_METHOD_SCAN:
                // 二维码
                doPayByScan();
                break;
            default:
                mView.showError("支付过程出现未知异常");
                break;
        }
    }

    private void doPayByCode() {
        if (!Utils.isNetworkEnabled(mContext)) {
            mView.showError(mContext.getString(R.string.network_disabled));
            return;
        }
        if (mMemberPayByCodeSubscription != null) {
            mMemberPayByCodeSubscription.unsubscribe();
        }
        mView.showLoading();
        mMemberPayByCodeSubscription = TradeManager.getInstance().memberPayByCode(new Callback<MemberRecordResult>() {
            @Override
            public void onSuccess(MemberRecordResult o) {
                mView.hideLoading();
                TradeManager.getInstance().finishPay(mContext, AppConstants.TRADE_STATUS_SUCCESS, new Callback0<Void>() {
                    @Override
                    public void onFinished(Void result) {
                        mView.hideLoading();
                        mView.showToast("支付成功！");
                        mView.finishSelf();
                    }
                });
            }

            @Override
            public void onError(String error) {
                mView.hideLoading();
                mView.showError("会员支付失败:" + error);
                TradeManager.getInstance().getCurrentTrade().memberPayError = error;
            }
        });
    }

    private void doPayByScan() {
        if (!Utils.isNetworkEnabled(mContext)) {
            mView.showError(mContext.getString(R.string.network_disabled));
            return;
        }
        if (mMemberPayByScanSubscription != null) {
            mMemberPayByScanSubscription.unsubscribe();
        }
        mView.showLoading();
        mMemberPayByScanSubscription = TradeManager.getInstance().memberPayByScan(new Callback<MemberPayResult.PayResult>() {
            @Override
            public void onSuccess(MemberPayResult.PayResult o) {
                mView.hideLoading();
                TradeManager.getInstance().finishPay(mContext, AppConstants.TRADE_STATUS_SUCCESS, new Callback0<Void>() {
                    @Override
                    public void onFinished(Void result) {
                        mView.hideLoading();
                        mView.showToast("支付成功！");
                        mView.finishSelf();
                    }
                });
            }

            @Override
            public void onError(String error) {
                mView.hideLoading();
                mView.showError("会员支付失败:" + error);
                TradeManager.getInstance().getCurrentTrade().memberPayError = error;
            }
        });
    }

    @Override
    public void onNavigationBack() {
        if (TradeManager.getInstance().getCurrentTrade().memberPayResult == 0) {
            TradeManager.getInstance().getCurrentTrade().memberPayResult = AppConstants.PAY_RESULT_CANCEL;
        }
        UiNavigation.gotoConfirmActivity(mContext, null);
        mView.finishSelf();
    }
}
