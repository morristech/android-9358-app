package com.xmd.cashier.presenter;

import android.content.Context;

import com.shidou.commonlibrary.helper.RetryPool;
import com.shidou.commonlibrary.helper.XLogger;
import com.xmd.app.EventBusSafeRegister;
import com.xmd.cashier.R;
import com.xmd.cashier.UiNavigation;
import com.xmd.cashier.common.AppConstants;
import com.xmd.cashier.common.Utils;
import com.xmd.cashier.contract.CashierContract;
import com.xmd.cashier.dal.bean.MemberInfo;
import com.xmd.cashier.dal.bean.Trade;
import com.xmd.cashier.dal.event.TradeDoneEvent;
import com.xmd.cashier.dal.net.SpaService;
import com.xmd.cashier.dal.net.response.GetTradeNoResult;
import com.xmd.cashier.dal.net.response.TradeChannelListResult;
import com.xmd.cashier.manager.AccountManager;
import com.xmd.cashier.manager.Callback;
import com.xmd.cashier.manager.MemberManager;
import com.xmd.cashier.manager.TradeManager;
import com.xmd.cashier.widget.ActionSheetDialog;
import com.xmd.m.network.BaseBean;
import com.xmd.m.network.NetworkSubscriber;
import com.xmd.m.network.XmdNetwork;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import retrofit2.Call;
import rx.Subscription;

/**
 * Created by zr on 17-4-13.
 */

public class CashierPresenter implements CashierContract.Presenter {
    private static final String TAG = "CashierPresenter";
    private Context mContext;
    private CashierContract.View mView;

    private Subscription mGetPosTradeNoSubscription;
    private Subscription mGenerateBatchOrderSubscription;
    private Subscription mGetTradeChannelSubscription;

    private TradeManager mTradeManager;

    public CashierPresenter(Context context, CashierContract.View view) {
        mContext = context;
        mView = view;
        mView.setPresenter(this);
    }

    @Override
    public void onCreate() {
        EventBusSafeRegister.register(this);
        mTradeManager = TradeManager.getInstance();
        mTradeManager.newTrade();
    }

    @Override
    public void onStart() {
        reset();
    }

    @Override
    public void onDestroy() {
        EventBusSafeRegister.unregister(this);
        stopCallBackPos();
        if (mGetPosTradeNoSubscription != null) {
            mGetPosTradeNoSubscription.unsubscribe();
        }
        if (mGetTradeChannelSubscription != null) {
            mGetTradeChannelSubscription.unsubscribe();
        }
        if (mGenerateBatchOrderSubscription != null) {
            mGenerateBatchOrderSubscription.unsubscribe();
        }
    }

    @Override
    public void onChannel() {       // 发起支付
        // 当前网络
        if (!Utils.isNetworkEnabled(mContext)) {
            mView.showError(mContext.getString(R.string.network_disabled));
            return;
        }

        // 消费金额
        Trade trade = mTradeManager.getCurrentTrade();
        if (trade.getOriginMoney() == 0) {
            mView.showToast("请输入收款信息");
            return;
        }

        if (mTradeManager.getTradeChannelInfos() != null && !mTradeManager.getTradeChannelInfos().isEmpty()) {
            showChannelSheet();
        } else {
            mView.showLoading();
            if (mGetTradeChannelSubscription != null) {
                mGetTradeChannelSubscription.unsubscribe();
            }
            mGetTradeChannelSubscription = mTradeManager.getPayChannelList(new Callback<TradeChannelListResult>() {
                @Override
                public void onSuccess(TradeChannelListResult o) {
                    mView.hideLoading();
                    showChannelSheet();
                }

                @Override
                public void onError(String error) {
                    mView.hideLoading();
                    mView.showToast("获取支付方式失败：" + error);
                }
            });
        }
    }

    // 选择支付方式
    private void showChannelSheet() {
        ActionSheetDialog dialog = new ActionSheetDialog(mContext);
        dialog.setContents(mTradeManager.getTradeChannelTexts());
        dialog.setCancelText("取消");
        dialog.setCanceledOnTouchOutside(true);
        dialog.setEventListener(new ActionSheetDialog.OnEventListener() {
            @Override
            public void onActionItemClick(ActionSheetDialog dialog, String item, int position) {
                XLogger.i(TAG, AppConstants.LOG_BIZ_NORMAL_CASHIER + "选择支付方式：" + item);
                mTradeManager.setCurrentChannel(item);
                switch (mTradeManager.getCurrentTrade().currentChannelType) {
                    case AppConstants.PAY_CHANNEL_UNION:    //银联
                        getPosTradeNo();
                        break;
                    case AppConstants.PAY_CHANNEL_ACCOUNT:  //会员
                        if (AppConstants.APP_REQUEST_YES.equals(MemberManager.getInstance().getMemberSwitch())) {
                            UiNavigation.gotoMemberReadActivity(mContext, AppConstants.MEMBER_BUSINESS_TYPE_PAYMENT);
                        } else {
                            mView.showError("会所未开通会员功能!");
                        }
                        break;
                    case AppConstants.PAY_CHANNEL_WX:   //微信
                    case AppConstants.PAY_CHANNEL_ALI:  //支付宝
                    case AppConstants.PAY_CHANNEL_CASH: //现金
                    default:
                        generateBatchOrder();
                        break;
                }
                dialog.dismiss();
            }

            public void onCancelItemClick(ActionSheetDialog dialog) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    // 会员支付:获取会员信息
    @Subscribe
    public void onEvent(MemberInfo info) {
        mTradeManager.getCurrentTrade().memberInfo = info;
        mTradeManager.getCurrentTrade().memberId = String.valueOf(info.id);
        generateBatchOrder();
    }

    // 银联支付:生成交易号
    private void getPosTradeNo() {
        mView.showLoading();
        if (mGetPosTradeNoSubscription != null) {
            mGetPosTradeNoSubscription.unsubscribe();
        }
        mGetPosTradeNoSubscription = mTradeManager.getTradeNo(new Callback<GetTradeNoResult>() {
            @Override
            public void onSuccess(GetTradeNoResult o) {
                mView.hideLoading();
                generateBatchOrder();
            }

            @Override
            public void onError(String error) {
                mView.hideLoading();
                mView.showToast("生成交易号失败：" + error);
            }
        });
    }

    // 生成交易记录
    private void generateBatchOrder() {
        final Trade trade = mTradeManager.getCurrentTrade();
        mView.showLoading();
        if (mGenerateBatchOrderSubscription != null) {
            mGenerateBatchOrderSubscription.unsubscribe();
        }
        mGenerateBatchOrderSubscription = mTradeManager.generateBatchOrder(
                trade.batchNo,
                trade.memberId,
                trade.currentChannelType,
                null,
                mTradeManager.formatVerifyCodes(trade.getCouponList()),
                null,
                String.valueOf(trade.getOriginMoney()),
                null,
                new Callback<String>() {
                    @Override
                    public void onSuccess(String o) {
                        mView.hideLoading();
                        if (AppConstants.APP_REQUEST_YES.equals(o)) {
                            trade.tradeStatus = AppConstants.TRADE_STATUS_SUCCESS;
                            EventBus.getDefault().post(new TradeDoneEvent(AppConstants.TRADE_TYPE_NORMAL));
                        } else {
                            switch (trade.currentChannelType) {
                                case AppConstants.PAY_CHANNEL_WX:   //微信
                                case AppConstants.PAY_CHANNEL_ALI:  //支付宝
                                    UiNavigation.gotoTradeQrcodePayActivity(mContext, AppConstants.TRADE_TYPE_NORMAL);
                                    break;
                                case AppConstants.PAY_CHANNEL_UNION:    //银联
                                    posPay();
                                    break;
                                case AppConstants.PAY_CHANNEL_ACCOUNT:  //会员
                                    UiNavigation.gotoTradeMemberPayActivity(mContext, AppConstants.TRADE_TYPE_NORMAL);
                                    break;
                                case AppConstants.PAY_CHANNEL_CASH: //现金
                                default:
                                    UiNavigation.gotoTradeMarkPayActivity(mContext, AppConstants.TRADE_TYPE_NORMAL);
                                    break;
                            }
                        }
                    }

                    @Override
                    public void onError(String error) {
                        mView.hideLoading();
                        mView.showToast(error);
                    }
                });
    }

    @Override
    public void onClickSetCouponInfo() {
        UiNavigation.gotoVerificationActivity(mContext);
    }

    @Override
    public void onOriginMoneyChanged() {
        Trade trade = mTradeManager.getCurrentTrade();
        trade.setOriginMoney(mView.getOriginMoney()); //设置交易的消费金额
        mTradeManager.setDiscountOriginAmount();    //更新折扣券的消费金额
        trade.setWillDiscountMoney(mTradeManager.getDiscountAmount(trade.getCouponList()));
        mView.setDiscountMoney(trade.getWillDiscountMoney()); //更新优惠金额
        updateFinallyMoney();   //更新实收金额
    }

    // POS支付: 银联
    private void posPay() {
        mTradeManager.posPay(mContext, new Callback<Void>() {
            @Override
            public void onSuccess(Void o) {
                startCallBackPos();
            }

            @Override
            public void onError(String error) {
                EventBus.getDefault().post(new TradeDoneEvent(AppConstants.TRADE_TYPE_NORMAL));
            }
        });
    }

    // 汇报Pos支付结果
    private Call<BaseBean> callbackPosCall;
    private RetryPool.RetryRunnable mRetryCallBackPos;
    private boolean resultCallBackPos = false;

    public void startCallBackPos() {
        mRetryCallBackPos = new RetryPool.RetryRunnable(AppConstants.TINNY_INTERVAL, 1.0f, new RetryPool.RetryExecutor() {
            @Override
            public boolean run() {
                return callbackPos();
            }
        });
        RetryPool.getInstance().postWork(mRetryCallBackPos);
    }

    public void stopCallBackPos() {
        if (callbackPosCall != null && !callbackPosCall.isCanceled()) {
            callbackPosCall.cancel();
        }
        if (mRetryCallBackPos != null) {
            RetryPool.getInstance().removeWork(mRetryCallBackPos);
            mRetryCallBackPos = null;
        }
    }

    private boolean callbackPos() {
        Trade trade = mTradeManager.getCurrentTrade();
        callbackPosCall = XmdNetwork.getInstance().getService(SpaService.class)
                .callbackBatchOrderSync(AccountManager.getInstance().getToken(),
                        trade.memberId,
                        trade.currentChannelType,
                        trade.payOrderId,
                        trade.tradeNo,
                        trade.posPayCashierNo,
                        String.valueOf(trade.getWillPayMoney()));
        XmdNetwork.getInstance().requestSync(callbackPosCall, new NetworkSubscriber<BaseBean>() {
            @Override
            public void onCallbackSuccess(BaseBean result) {
                // 汇报成功
                resultCallBackPos = true;
                EventBus.getDefault().post(new TradeDoneEvent(AppConstants.TRADE_TYPE_NORMAL));
            }

            @Override
            public void onCallbackError(Throwable e) {
                resultCallBackPos = false;
            }
        });
        return resultCallBackPos;
    }

    private void updateFinallyMoney() {
        Trade trade = mTradeManager.getCurrentTrade();
        int finallyMoney = trade.getOriginMoney() - trade.getWillDiscountMoney();
        if (finallyMoney < 0) {
            finallyMoney = 0;
        }
        trade.setWillPayMoney(finallyMoney);
        mView.setFinallyMoney(Utils.moneyToString(finallyMoney));
    }

    private void reset() {
        XLogger.i(TAG, AppConstants.LOG_BIZ_NORMAL_CASHIER + "===============reset================");
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(TradeDoneEvent tradeDoneEvent) {
        if (tradeDoneEvent.type == AppConstants.TRADE_TYPE_NORMAL) {
            stopCallBackPos();
            UiNavigation.gotoCashierResultActivity(mContext);
        }
    }
}
