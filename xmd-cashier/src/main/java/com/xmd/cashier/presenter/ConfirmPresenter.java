package com.xmd.cashier.presenter;

import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;

import com.shidou.commonlibrary.helper.XLogger;
import com.xmd.cashier.UiNavigation;
import com.xmd.cashier.common.AppConstants;
import com.xmd.cashier.common.Utils;
import com.xmd.cashier.contract.ConfirmContract;
import com.xmd.cashier.dal.bean.Trade;
import com.xmd.cashier.dal.bean.VerificationItem;
import com.xmd.cashier.manager.Callback;
import com.xmd.cashier.manager.Callback0;
import com.xmd.cashier.manager.CashierManager;
import com.xmd.cashier.manager.TradeManager;
import com.xmd.cashier.widget.CustomAlertDialogBuilder;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by heyangya on 16-8-22.
 */

public class ConfirmPresenter implements ConfirmContract.Presenter {
    private static final String TAG = "ConfirmPresenter";
    private Context mContext;
    private ConfirmContract.View mView;
    private CashierManager mCashierManager = CashierManager.getInstance();
    private TradeManager mTradeManager = TradeManager.getInstance();
    private AtomicBoolean mProcessLocker = new AtomicBoolean(false);

    public ConfirmPresenter(Context context, ConfirmContract.View view) {
        mContext = context;
        mView = view;
        mView.setPresenter(this);
    }

    @Override
    public void onCreate() {
        Trade trade = mTradeManager.getCurrentTrade();
        mView.setOriginMoney(trade.getOriginMoney());
        mView.setDiscountMoney(trade.getWillDiscountMoney());
        mView.setFinallyMoney(trade.getNeedPayMoney());
        mView.showTradeStatusInfo(trade);

        String message = mView.getStartShowMessage();
        if (!TextUtils.isEmpty(message)) {
            Utils.showAlertDialogMessage(mContext, message);
        }

        if (trade.getNeedPayMoney() == 0) {
            //已成功核销的金额>需要支付的金额，那么不再需要显示支付按钮
            mView.hideCouponView();
            mView.hideCancelButton();
        }
    }

    @Override
    public void onStart() {
        mTradeManager.checkAndProcessPosStatus(mContext);
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public void onClickCancel() {
        XLogger.i(TAG, AppConstants.LOG_BIZ_NORMAL_CASHIER + "补收款是否取消交易？");
        mView.hideKeyboard();
        String message;
        Trade trade = mTradeManager.getCurrentTrade();
        trade.tradeStatus = AppConstants.TRADE_STATUS_CANCEL;
        if (trade.getVerificationSuccessfulMoney() > 0) {
            message = "选择的优惠券已经核销无法再次使用，确定退出本次交易？";
        } else {
            message = "确定退出交易？";
        }
        new CustomAlertDialogBuilder(mContext)
                .setMessage(message)
                .setNegativeButton("退出交易", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, int which) {
                        //取消支付，汇报数据
                        mTradeManager.finishPay(mContext, new Callback0<Void>() {
                            @Override
                            public void onFinished(Void result) {
                                XLogger.i(TAG, AppConstants.LOG_BIZ_NORMAL_CASHIER + "补收款退出交易");
                                dialog.dismiss();
                                mView.finishSelf();
                            }
                        });
                    }
                })
                .setPositiveButton("继续交易", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        XLogger.i(TAG, AppConstants.LOG_BIZ_NORMAL_CASHIER + "补收款继续交易");
                        dialog.dismiss();
                    }
                })
                .create()
                .show();
    }

    @Override
    public void onClickOk() {
        mView.hideKeyboard();
        Trade trade = mTradeManager.getCurrentTrade();
        if (trade.getNeedPayMoney() == 0) {
            //不需要支付
            mTradeManager.getCurrentTrade().currentCashier = AppConstants.CASHIER_TYPE_ERROR;
            mTradeManager.getCurrentTrade().tradeStatus = AppConstants.TRADE_STATUS_SUCCESS;
            mTradeManager.finishPay(mContext, new Callback0<Void>() {
                @Override
                public void onFinished(Void result) {
                    mView.finishSelf();
                }
            });
            return;
        }
        XLogger.i(TAG, AppConstants.LOG_BIZ_NORMAL_CASHIER + "补收款继续支付：" + trade.currentCashier);
        switch (trade.currentCashier) {
            case AppConstants.CASHIER_TYPE_MEMBER:
                UiNavigation.gotoMemberCashierActivity(mContext);
                mView.finishSelf();
                break;
            case AppConstants.CASHIER_TYPE_QRCODE:
                UiNavigation.gotoScanPayActivity(mContext);
                mView.finishSelf();
                break;
            case AppConstants.CASHIER_TYPE_POS:
                posPay(trade.getNeedPayMoney());
                break;
            case AppConstants.CASHIER_TYPE_CASH:
                UiNavigation.gotoCashPayActivity(mContext, trade.getNeedPayMoney());
                mView.finishSelf();
                break;
            default:
                break;
        }
    }

    private void posPay(int money) {
        if (!mProcessLocker.compareAndSet(false, true)) {
            return;
        }
        XLogger.i(TAG, AppConstants.LOG_BIZ_NORMAL_CASHIER + "补收款继续支付旺POS渠道");
        mTradeManager.posPay(mContext, money, new Callback<Void>() {
            @Override
            public void onSuccess(Void o) {
                XLogger.i(TAG, AppConstants.LOG_BIZ_NORMAL_CASHIER + "补收款继续支付旺POS渠道支付---成功");
                mTradeManager.finishPay(mContext, new Callback0<Void>() {
                    @Override
                    public void onFinished(Void result) {
                        mView.finishSelf();
                    }
                });
            }

            @Override
            public void onError(String error) {
                XLogger.e(TAG, AppConstants.LOG_BIZ_NORMAL_CASHIER + "补收款继续支付旺POS渠道支付---失败：" + error);
                if (!mCashierManager.isUserCancel(mTradeManager.getCurrentTrade().posPayReturn)) {
                    mView.showError("支付失败：" + error);
                }
                mProcessLocker.set(false);
            }
        });
    }

    @Override
    public void onCouponMoneyChanged() {
        Trade trade = mTradeManager.getCurrentTrade();
        trade.setWillDiscountMoney(mView.getDiscountMoney());
        int finallyMoney = trade.getNeedPayMoney();
        if (finallyMoney < 0) {
            finallyMoney = 0;
        }
        mView.setFinallyMoney(finallyMoney);
    }

    @Override
    public void onVerificationItemClicked(VerificationItem item) {
        mView.hideKeyboard();
        switch (item.type) {
            case AppConstants.TYPE_COUPON:
            case AppConstants.TYPE_CASH_COUPON:
            case AppConstants.TYPE_PAID_COUPON:
            case AppConstants.TYPE_DISCOUNT_COUPON:
                UiNavigation.gotoVerifyCouponActivity(mContext, item.couponInfo, false);
                break;
            case AppConstants.TYPE_ORDER:
                UiNavigation.gotoVerifyOrderActivity(mContext, item.order, false);
                break;
            default:
                break;
        }
    }
}
