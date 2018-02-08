package com.xmd.cashier.presenter;

import android.content.Context;
import android.text.TextUtils;

import com.shidou.commonlibrary.helper.XLogger;
import com.xmd.cashier.R;
import com.xmd.cashier.UiNavigation;
import com.xmd.cashier.cashier.PosFactory;
import com.xmd.cashier.common.AppConstants;
import com.xmd.cashier.common.Utils;
import com.xmd.cashier.contract.MemberCashierContract;
import com.xmd.cashier.dal.bean.Trade;
import com.xmd.cashier.dal.net.response.MemberRecordResult;
import com.xmd.cashier.manager.Callback;
import com.xmd.cashier.manager.Callback0;
import com.xmd.cashier.manager.MemberManager;
import com.xmd.cashier.manager.TradeManager;
import com.xmd.cashier.widget.InputPasswordDialog;

import rx.Subscription;

/**
 * Created by zr on 17-7-22.
 */

public class MemberCashierPresenter implements MemberCashierContract.Presenter {
    private static final String TAG = "MemberCashierPresenter";
    private Context mContext;
    private MemberCashierContract.View mView;

    private Subscription mMemberPaySubscription;

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
        String method = TradeManager.getInstance().getCurrentTrade().memberPayMethod;
        switch (method) {
            case AppConstants.MEMBER_PAY_METHOD_CODE:
                // 接口
                if (!AppConstants.APP_REQUEST_NO.equals(MemberManager.getInstance().getVerificationSwitch())) {
                    doInputPassword(method);
                } else {
                    // 无需校验密码
                    doMemberPay(method, null);
                }
                break;
            case AppConstants.MEMBER_PAY_METHOD_SCAN:
                // 二维码
                doMemberPay(method, null);
                break;
            default:
                mView.showError("支付过程出现未知异常");
                break;
        }
    }

    private void doInputPassword(final String method) {
        final InputPasswordDialog dialog = new InputPasswordDialog(mContext);
        dialog.show();
        dialog.setCancelable(false);
        dialog.setTitle("会员消费");
        dialog.setCallBack(new InputPasswordDialog.BtnCallBack() {
            @Override
            public void onBtnNegative() {
                dialog.dismiss();
            }

            @Override
            public void onBtnPositive(String password) {
                if (TextUtils.isEmpty(password)) {
                    mView.showToast("请输入密码");
                    return;
                }
                dialog.dismiss();
                // 调用会员支付
                doMemberPay(method, password);
            }
        });
    }

    private void finishMemberPay() {
        TradeManager.getInstance().finishPay(mContext, new Callback0<Void>() {
            @Override
            public void onFinished(Void result) {
                mView.finishSelf();
            }
        });
    }

    private void doMemberPay(String memberPayMethod, String password) {
        if (!Utils.isNetworkEnabled(mContext)) {
            mView.showError(mContext.getString(R.string.network_disabled));
            return;
        }
        if (mMemberPaySubscription != null) {
            mMemberPaySubscription.unsubscribe();
        }
        mView.showLoading();
        XLogger.i(TAG, AppConstants.LOG_BIZ_NORMAL_CASHIER + "补收款发起会员支付：" + memberPayMethod);
        mMemberPaySubscription = TradeManager.getInstance().memberPay(memberPayMethod, password, new Callback<MemberRecordResult>() {
            @Override
            public void onSuccess(MemberRecordResult o) {
                XLogger.i(TAG, AppConstants.LOG_BIZ_NORMAL_CASHIER + "补收款发起会员支付---成功");
                PosFactory.getCurrentCashier().speech("买单成功");
                mView.hideLoading();
                mView.showToast("会员支付成功，正在出票...");
                TradeManager.getInstance().getCurrentTrade().tradeStatus = AppConstants.TRADE_STATUS_SUCCESS;
                TradeManager.getInstance().getCurrentTrade().memberRecordInfo = o.getRespData();
                finishMemberPay();
            }

            @Override
            public void onError(String error) {
                XLogger.e(TAG, AppConstants.LOG_BIZ_NORMAL_CASHIER + "补收款发起会员支付---失败:" + error);
                mView.hideLoading();
                mView.showError("会员支付失败:" + error);
            }
        });
    }

    @Override
    public void onNavigationBack() {
        UiNavigation.gotoConfirmActivity(mContext, null);
        mView.finishSelf();
    }
}
