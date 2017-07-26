package com.xmd.cashier.presenter;

import android.content.Context;

import com.shidou.commonlibrary.helper.XLogger;
import com.xmd.cashier.R;
import com.xmd.cashier.UiNavigation;
import com.xmd.cashier.common.AppConstants;
import com.xmd.cashier.common.Utils;
import com.xmd.cashier.contract.CashierContract;
import com.xmd.cashier.dal.bean.Trade;
import com.xmd.cashier.dal.bean.VerificationItem;
import com.xmd.cashier.dal.net.response.GetTradeNoResult;
import com.xmd.cashier.dal.net.response.StringResult;
import com.xmd.cashier.manager.Callback;
import com.xmd.cashier.manager.Callback0;
import com.xmd.cashier.manager.CashierManager;
import com.xmd.cashier.manager.MemberManager;
import com.xmd.cashier.manager.TradeManager;

import java.util.List;

import rx.Subscription;

/**
 * Created by zr on 17-4-13.
 */

public class CashierPresenter implements CashierContract.Presenter {
    private Context mContext;
    private CashierContract.View mView;

    private Subscription mVerificationSubscription;
    private Subscription mGetTradeNoSubscription;
    private Subscription mGetOnlinePayOrderIdSubscription;

    private TradeManager mTradeManager = TradeManager.getInstance();
    private CashierManager mCashierManager = CashierManager.getInstance();

    public CashierPresenter(Context context, CashierContract.View view) {
        mContext = context;
        mView = view;
        mView.setPresenter(this);
    }

    @Override
    public void onClickSetCouponInfo() {
        UiNavigation.gotoVerificationActivity(mContext);
    }

    @Override
    public void onDiscountMoneyChanged() {
        mTradeManager.getCurrentTrade().setWillDiscountMoney(mView.getDiscountMoney());
        updateFinallyMoney();
    }

    @Override
    public void onOriginMoneyChanged() {
        mTradeManager.getCurrentTrade().setOriginMoney(mView.getOriginMoney());
        updateFinallyMoney();
    }

    /**
     * *******************************************Pos收银*******************************************
     * 设置收银类型->获取TradeNo->核销->Pos支付
     */
    @Override
    public void onClickCashier() {
        mTradeManager.getCurrentTrade().currentCashier = AppConstants.CASHIER_TYPE_POS;
        processTradeNo();
    }

    /**
     * Pos收银台支付
     */
    private void payCashier(int money) {
        mTradeManager.posPay(mContext, money, new Callback<Void>() {
            @Override
            public void onSuccess(Void o) {
                mTradeManager.finishPay(mContext, AppConstants.TRADE_STATUS_SUCCESS, new Callback0<Void>() {
                    @Override
                    public void onFinished(Void result) {
                        mView.hideLoading();
                        mView.showToast("支付成功！");
                        reset();//交易成功，重置界面
                    }
                });
            }

            @Override
            public void onError(String error) {
                mView.hideLoading();
                if (mCashierManager.isUserCancel(mTradeManager.getCurrentTrade().posPayReturn)) {
                    UiNavigation.gotoConfirmActivity(mContext);
                } else {
                    UiNavigation.gotoConfirmActivity(mContext, "支付失败：" + error);
                }
            }
        });
    }


    /**
     * *******************************************会员支付*******************************************
     * 扫描会员二维码->获取会员信息->设置收银类型->获取TradeNo->核销->会员支付
     */
    @Override
    public void onClickMemberPay() {
        if (AppConstants.APP_REQUEST_YES.equals(MemberManager.getInstance().getmMemberSwitch())) {
            UiNavigation.gotoMemberReadActivity(mContext, AppConstants.MEMBER_BUSINESS_TYPE_PAYMENT);
        } else {
            mView.showError("会所会员功能未开通!");
        }
    }

    /**
     * 会员支付
     */
    private void payMember() {
        mView.hideLoading();
        UiNavigation.gotoMemberCashierActivity(mContext);
    }

    /**
     * **********************************************小摩豆在线买单***********************************
     * 获取买单编号->设置收银类型->核销->微信扫描二维码买单
     */
    @Override
    public void onClickXMDOnlinePay() {
        processOnlinePayTradeNo();
    }

    /**
     * 在线买单
     */
    private void payXMDOnline() {
        mView.hideLoading();
        UiNavigation.gotoScanPayActivity(mContext);
    }

    /**
     * 生成小摩豆在线买单ID
     */
    private void processOnlinePayTradeNo() {
        mView.showLoading();
        if (mGetOnlinePayOrderIdSubscription != null) {
            mGetOnlinePayOrderIdSubscription.unsubscribe();
        }
        mGetOnlinePayOrderIdSubscription = mTradeManager.fetchOnlinePayId(new Callback<StringResult>() {
            @Override
            public void onSuccess(StringResult o) {
                mTradeManager.getCurrentTrade().currentCashier = AppConstants.CASHIER_TYPE_XMD_ONLINE;
                if (mTradeManager.haveSelected()) {
                    processCoupon();
                } else {
                    processPay();
                }
            }

            @Override
            public void onError(String error) {
                mView.hideLoading();
                mView.showError("生成买单信息失败，请重试");
            }
        });
    }


    /**
     * 检查网络和收款金额
     */
    @Override
    public boolean checkInput() {
        if (!Utils.isNetworkEnabled(mContext)) {
            mView.showError(mContext.getString(R.string.network_disabled));
            return false;
        }
        Trade trade = mTradeManager.getCurrentTrade();
        // 收银流程只处理消费金额有效(>0)的交易,不处理单独核销(首页核销入口处理)
        if (trade.getOriginMoney() == 0) {
            mView.showToast("请先输入收款信息");
            return false;
        }
        return true;
    }

    /**
     * 生成Pos流水记录
     */
    public void processTradeNo() {
        mView.showLoading();
        if (mGetTradeNoSubscription != null) {
            mGetTradeNoSubscription.unsubscribe();
        }
        mGetTradeNoSubscription = mTradeManager.fetchTradeNo(new Callback<GetTradeNoResult>() {
            @Override
            public void onSuccess(GetTradeNoResult o) {
                if (mTradeManager.haveSelected()) {
                    //处理优惠券信息
                    processCoupon();
                } else {
                    //没有优惠券
                    processPay();
                }
            }

            @Override
            public void onError(String error) {
                mView.hideLoading();
                mView.showError("获取订单号失败，请重试");
            }
        });
    }


    /**
     * 核销优惠券
     */
    private void processCoupon() {
        if (mVerificationSubscription != null) {
            mVerificationSubscription.unsubscribe();
        }
        final Trade trade = mTradeManager.getCurrentTrade();
        mVerificationSubscription = mTradeManager.doVerify(
                mTradeManager.getCurrentTrade().getOriginMoney(),
                new Callback<List<VerificationItem>>() {
                    @Override
                    public void onSuccess(List<VerificationItem> o) {
                        //核销成功，计算成功核销的金额
                        mTradeManager.calculateSuccessVerificationValue();
                        if (mTradeManager.haveFailed()) {
                            //有失败的情况，需要进入错误处理页面
                            mView.hideLoading();
                            trade.setWillDiscountMoney(trade.getVerificationSuccessfulMoney());
                            UiNavigation.gotoConfirmActivity(mContext, "部分券核销失败,请确认");
                        } else {
                            // 全部核销成功
                            // 进入支付环节
                            processPay();
                        }
                    }

                    @Override
                    public void onError(String error) {
                        mView.hideLoading();
                        UiNavigation.gotoConfirmActivity(mContext, "优惠券核销错误：" + error);
                    }
                });
    }

    /**
     * 处理支付
     */
    private void processPay() {
        Trade trade = mTradeManager.getCurrentTrade();
        int needPayMoney = trade.getNeedPayMoney();
        if (needPayMoney == 0) {
            trade.withoutPay = true;
            //当前不需要支付
            mView.hideLoading();
            mView.showToast("支付成功！");
            mTradeManager.finishPay(mContext, AppConstants.TRADE_STATUS_SUCCESS, new Callback0<Void>() {
                @Override
                public void onFinished(Void result) {
                    reset();
                }
            });
            return;
        }

        switch (mTradeManager.getCurrentTrade().currentCashier) {
            case AppConstants.CASHIER_TYPE_MEMBER:
                // 会员
                payMember();
                break;
            case AppConstants.CASHIER_TYPE_XMD_ONLINE:
                // 在线扫码
                payXMDOnline();
                break;
            case AppConstants.CASHIER_TYPE_POS:
            default:
                // pos收银
                payCashier(needPayMoney);
                break;
        }
    }

    @Override
    public void onCreate() {
    }

    @Override
    public void onStart() {
        if (mTradeManager.checkAndProcessPosStatus(mContext)) {
            return;
        }
        reset();
    }

    @Override
    public void onDestroy() {
        mView.hideLoading();
        if (mVerificationSubscription != null) {
            mVerificationSubscription.unsubscribe();
        }
        if (mGetTradeNoSubscription != null) {
            mGetTradeNoSubscription.unsubscribe();
        }
        if (mGetOnlinePayOrderIdSubscription != null) {
            mGetOnlinePayOrderIdSubscription.unsubscribe();
        }
    }

    private void updateFinallyMoney() {
        Trade trade = mTradeManager.getCurrentTrade();
        int finallyMoney = trade.getOriginMoney() - trade.getWillDiscountMoney();
        if (finallyMoney < 0) {
            finallyMoney = 0;
        }
        mView.setFinallyMoney(Utils.moneyToString(finallyMoney));
    }

    private void reset() {
        XLogger.i("===============reset================");
        Trade trade = mTradeManager.getCurrentTrade();
        mView.setOriginMoney(trade.getOriginMoney());
        mView.setDiscountMoney(trade.getWillDiscountMoney());
        updateFinallyMoney();
        if (trade.getVerificationCount() > 0) {
            mView.setCouponButtonText("优惠券X" + trade.getVerificationCount());
        } else {
            mView.setCouponButtonText(mContext.getString(R.string.btn_add_coupon));
        }
    }
}
