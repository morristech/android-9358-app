package com.xmd.cashier.presenter;

import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;

import com.xmd.cashier.R;
import com.xmd.cashier.UiNavigation;
import com.xmd.cashier.common.AppConstants;
import com.xmd.cashier.common.Utils;
import com.xmd.cashier.contract.MemberCashierContract;
import com.xmd.cashier.dal.bean.MemberRecordInfo;
import com.xmd.cashier.dal.bean.Trade;
import com.xmd.cashier.dal.net.response.MemberRecordResult;
import com.xmd.cashier.manager.Callback;
import com.xmd.cashier.manager.Callback0;
import com.xmd.cashier.manager.MemberManager;
import com.xmd.cashier.manager.TradeManager;
import com.xmd.cashier.widget.CustomAlertDialogBuilder;
import com.xmd.cashier.widget.InputPasswordDialog;

import rx.Subscription;

/**
 * Created by zr on 17-7-22.
 */

public class MemberCashierPresenter implements MemberCashierContract.Presenter {
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

    private void print(MemberRecordInfo info) {
        mView.showLoading();
        MemberManager.getInstance().printInfo(info, false, true, new Callback() {
            @Override
            public void onSuccess(Object o) {
                mView.hideLoading();
                new CustomAlertDialogBuilder(mContext)
                        .setMessage("是否需要打印客户联小票?")
                        .setPositiveButton("打印", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                TradeManager.getInstance().getCurrentTrade().isMemberRemain = true;
                                finishMemberPay();
                            }
                        })
                        .setNegativeButton("完成交易", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                TradeManager.getInstance().getCurrentTrade().isMemberRemain = false;
                                finishMemberPay();
                            }
                        })
                        .create()
                        .show();
            }

            @Override
            public void onError(String error) {
                mView.hideLoading();
                mView.showToast("打印异常:" + error);
                finishMemberPay();
            }
        });
    }

    private void finishMemberPay() {
        TradeManager.getInstance().finishPay(mContext, AppConstants.TRADE_STATUS_SUCCESS, new Callback0<Void>() {
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
        mMemberPaySubscription = TradeManager.getInstance().memberPay(memberPayMethod, password, new Callback<MemberRecordResult>() {
            @Override
            public void onSuccess(MemberRecordResult o) {
                mView.hideLoading();
                mView.showToast("会员支付成功，正在出票...");
                TradeManager.getInstance().getCurrentTrade().memberRecordInfo = o.getRespData();
                print(o.getRespData());
            }

            @Override
            public void onError(String error) {
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
