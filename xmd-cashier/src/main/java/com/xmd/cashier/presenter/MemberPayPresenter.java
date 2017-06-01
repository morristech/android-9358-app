package com.xmd.cashier.presenter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.TextUtils;

import com.google.zxing.client.android.Intents;
import com.shidou.commonlibrary.helper.XLogger;
import com.xmd.cashier.R;
import com.xmd.cashier.UiNavigation;
import com.xmd.cashier.common.AppConstants;
import com.xmd.cashier.common.Utils;
import com.xmd.cashier.contract.MemberPayContract;
import com.xmd.cashier.dal.bean.MemberInfo;
import com.xmd.cashier.dal.bean.Trade;
import com.xmd.cashier.dal.net.response.MemberPayResult;
import com.xmd.cashier.manager.Callback;
import com.xmd.cashier.manager.Callback0;
import com.xmd.cashier.manager.CashierManager;
import com.xmd.cashier.manager.TradeManager;
import com.xmd.cashier.widget.CustomAlertDialogBuilder;

import java.util.Locale;

import rx.Subscription;

/**
 * Created by heyangya on 16-8-22.
 */

public class MemberPayPresenter implements MemberPayContract.Presenter {
    private Context mContext;
    private MemberPayContract.View mView;
    private TradeManager mTradeManager = TradeManager.getInstance();
    private CashierManager mCashierManager = CashierManager.getInstance();

    private Subscription mMemberPaySubscription;
    private Subscription mGetMemberInfoSubscription;

    public MemberPayPresenter(Context context, MemberPayContract.View view) {
        mContext = context;
        mView = view;
        mView.setPresenter(this);
    }


    @Override
    public void onCreate() {
        Trade trade = mTradeManager.getCurrentTrade();
        mView.setBalance("￥" + Utils.moneyToString(trade.memberInfo.balance));
        mView.setCardNumber(trade.memberInfo.cardNo);
        mView.setPhone(trade.memberInfo.phone);
        mView.setInfo(trade.memberInfo.memberTypeName);
        mView.setDiscount(String.format("%.02f折", trade.memberInfo.discount / 100.0f));
        mView.setOriginPayMoney("￥" + Utils.moneyToString(trade.getNeedPayMoney()));
    }

    @Override
    public void onStart() {
        //检查POS收银程序是否在支付，如果在支付则切换到POS机收银程序
        if (mTradeManager.checkAndProcessPosStatus(mContext)) {
            return;
        }
        //从其他界面回来，重新计算会员要支付的金额
        Trade trade = mTradeManager.getCurrentTrade();
        int payMoney = (int) (trade.getNeedPayMoney() * (trade.memberInfo.discount / 1000.0f)); //计算折扣
        XLogger.i("member pay money = " + payMoney);
        if (trade.memberInfo.balance >= payMoney) {
            trade.memberCanDiscount = "Y";
            trade.memberNeedPayMoney = trade.getNeedPayMoney(); //未折扣
            mView.setNeedPayMoneyEnough("￥" + Utils.moneyToString(payMoney));
        } else {
            //会员卡余额不足，不进行打折
            trade.memberCanDiscount = "N";
            trade.memberNeedPayMoney = trade.memberInfo.balance;
            mView.setNeedpayMoneyNotEnough(String.format(
                    Locale.getDefault(),
                    mContext.getResources().getString(R.string.member_pay_part_pay),
                    Utils.moneyToString(trade.memberNeedPayMoney),
                    Utils.moneyToString(trade.getNeedPayMoney() - trade.memberNeedPayMoney)));
        }
    }

    @Override
    public void onDestroy() {
        if (mMemberPaySubscription != null) {
            mMemberPaySubscription.unsubscribe();
        }
        if (mGetMemberInfoSubscription != null) {
            mGetMemberInfoSubscription.unsubscribe();
        }
    }

    @Override
    public void onClickOk() {
        final Trade trade = mTradeManager.getCurrentTrade();
        if (mMemberPaySubscription != null) {
            mMemberPaySubscription.unsubscribe();
        }
        mView.showLoading();
        mMemberPaySubscription = mTradeManager.memberPay(new Callback<MemberPayResult.PayResult>() {
            @Override
            public void onSuccess(MemberPayResult.PayResult o) {
                if (trade.getNeedPayMoney() > 0) {
                    cashierPay();
                } else {
                    mTradeManager.printVerificationList();
                    mTradeManager.finishPay(mContext, AppConstants.TRADE_STATUS_SUCCESS, new Callback0<Void>() {
                        @Override
                        public void onFinished(Void result) {
                            mView.hideLoading();
                            mView.showToast("支付成功！");
                            mView.finishSelf();
                        }
                    });
                }
            }

            @Override
            public void onError(String error) {
                mView.hideLoading();
                if (error != null && error.equals("二维码已失效")) {
                    onQrcodeInvalid();
                } else {
                    mView.showError("会员支付失败:" + error);
                }
                trade.memberPayError = error;
            }
        });
    }

    @Override
    public void onClickOtherPay() {
        new CustomAlertDialogBuilder(mContext)
                .setMessage("使用其他付款方式不能享受打折优惠，确定吗？")
                .setNegativeButton("使用其他", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        mView.showLoading();
                        cashierPay();
                    }
                })
                .setPositiveButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create()
                .show();
    }

    @Override
    public void onNavigationBack() {
        //用户接下返回键,表示取消支付
        if (mTradeManager.getCurrentTrade().memberPayResult == 0) {
            mTradeManager.getCurrentTrade().memberPayResult = AppConstants.PAY_RESULT_CANCEL;
        }
        UiNavigation.gotoConfirmActivity(mContext, null);
        mView.finishSelf();
    }

    //POS机支付
    private void cashierPay() {
        mTradeManager.getCurrentTrade().currentCashier = AppConstants.CASHIER_TYPE_POS;
        mTradeManager.posPay(mContext, mTradeManager.getCurrentTrade().getNeedPayMoney(), new Callback<Void>() {
            @Override
            public void onSuccess(Void o) {
                mTradeManager.printVerificationList();
                mTradeManager.finishPay(mContext, AppConstants.TRADE_STATUS_SUCCESS, new Callback0<Void>() {
                    @Override
                    public void onFinished(Void o) {
                        mView.hideLoading();
                        mView.showToast("支付成功！");
                        mView.finishSelf();
                    }
                });
            }

            @Override
            public void onError(String error) {
                //支付失败
                mView.hideLoading();
                Trade trade = mTradeManager.getCurrentTrade();
                if (mTradeManager.getCurrentTrade().getMemberPaidMoney() > 0) {
                    //会员已收款成功，出错时要跳到错误页面
                    if (!mCashierManager.isUserCancel(trade.posPayReturn)) {
                        UiNavigation.gotoConfirmActivity(mContext, "支付失败:" + error);
                    } else {
                        UiNavigation.gotoConfirmActivity(mContext);
                    }
                    mView.finishSelf();
                } else {
                    //全部用POS付款，出错只仅在当前页面提示
                    if (!mCashierManager.isUserCancel(trade.posPayReturn)) {
                        mView.showError(error);
                    }
                    mTradeManager.getCurrentTrade().currentCashier = AppConstants.CASHIER_TYPE_MEMBER;
                }
            }
        });
    }


    private void onQrcodeInvalid() {
        new CustomAlertDialogBuilder(mContext)
                .setMessage("二维码已失败，请重新扫描二维码")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        UiNavigation.gotoScanCodeActivity(mContext);
                    }
                })
                .create()
                .show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == UiNavigation.REQUEST_CODE_MEMBER_SCAN) {
            if (data != null && data.getAction().equals(Intents.Scan.ACTION)) {
                String result = data.getStringExtra(Intents.Scan.RESULT);
                getMemberInfo(result);
            }
        }
    }

    private void getMemberInfo(String memberToken) {
        if (TextUtils.isEmpty(memberToken)) {
            mView.showError("无法获取会员信息!");
            return;
        }
        if (mGetMemberInfoSubscription != null) {
            mGetMemberInfoSubscription.unsubscribe();
        }
        mGetMemberInfoSubscription = mTradeManager.fetchMemberInfo(memberToken, new Callback<MemberInfo>() {
            @Override
            public void onSuccess(MemberInfo o) {
                mTradeManager.getCurrentTrade().memberInfo = o;
                onCreate();
                onStart();
            }

            @Override
            public void onError(String error) {
                mView.showError("无法获取会员信息:" + error);
            }
        });
    }
}
