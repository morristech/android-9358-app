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
import com.xmd.cashier.dal.bean.TradeChannelInfo;
import com.xmd.cashier.dal.event.TradeDoneEvent;
import com.xmd.cashier.dal.net.RequestConstant;
import com.xmd.cashier.dal.net.SpaService;
import com.xmd.cashier.dal.net.response.TradeChannelListResult;
import com.xmd.cashier.dal.net.response.TradeOrderInfoResult;
import com.xmd.cashier.manager.AccountManager;
import com.xmd.cashier.manager.Callback;
import com.xmd.cashier.manager.ChannelManager;
import com.xmd.cashier.manager.MemberManager;
import com.xmd.cashier.manager.TradeManager;
import com.xmd.cashier.widget.ActionSheetDialog;
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

    private Subscription mGenerateBatchOrderSubscription;

    private TradeManager mTradeManager;
    private ChannelManager mChannelManager;

    public CashierPresenter(Context context, CashierContract.View view) {
        mContext = context;
        mView = view;
        mView.setPresenter(this);
    }

    @Override
    public void onCreate() {
        EventBusSafeRegister.register(this);
        mChannelManager = ChannelManager.getInstance();
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

        if (mChannelManager.getTradeChannelInfos() != null && !mChannelManager.getTradeChannelInfos().isEmpty()) {
            showChannelSheet();
        } else {
            XLogger.i(TAG, AppConstants.LOG_BIZ_NORMAL_CASHIER + "补收款获取会所支付方式：" + RequestConstant.URL_GET_PAY_CHANNEL_LIST);
            mView.showLoading();
            mChannelManager.getPayChannelList(new Callback<TradeChannelListResult>() {
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
        dialog.setContents(mChannelManager.getCashierChannelTexts());
        dialog.setCancelText("取消");
        dialog.setCanceledOnTouchOutside(true);
        dialog.setEventListener(new ActionSheetDialog.OnEventListener() {
            @Override
            public void onActionItemClick(ActionSheetDialog dialog, String item, int position) {
                XLogger.i(TAG, AppConstants.LOG_BIZ_NORMAL_CASHIER + "补收款选择支付方式：" + item);
                TradeChannelInfo channel = mChannelManager.getCurrentChannelByText(item);
                if (channel == null) {
                    mView.showError("选择支付方式出现未知异常");
                } else {
                    mTradeManager.setCurrentChannel(channel);
                    switch (mTradeManager.getCurrentTrade().currentChannelType) {
                        case AppConstants.PAY_CHANNEL_ACCOUNT:  //会员
                            if (AppConstants.APP_REQUEST_YES.equals(MemberManager.getInstance().getMemberSwitch())) {
                                UiNavigation.gotoMemberReadActivity(mContext, AppConstants.MEMBER_BUSINESS_TYPE_PAYMENT);
                            } else {
                                mView.showError("会所未开通会员功能!");
                            }
                            break;
                        case AppConstants.PAY_CHANNEL_UNION:    //银联
                        case AppConstants.PAY_CHANNEL_QRCODE:
                        case AppConstants.PAY_CHANNEL_WX:   //微信
                        case AppConstants.PAY_CHANNEL_ALI:  //支付宝
                        case AppConstants.PAY_CHANNEL_CASH: //现金
                        default:
                            generateBatchOrder();
                            break;
                    }
                }
                dialog.dismiss();
            }

            public void onCancelItemClick(ActionSheetDialog dialog) {
                XLogger.i(TAG, AppConstants.LOG_BIZ_NORMAL_CASHIER + "补收款选择支付方式后取消");
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    // 会员支付:获取会员信息
    @Subscribe
    public void onEvent(MemberInfo info) {
        mTradeManager.getCurrentTrade().memberInfo = info;
        mTradeManager.getCurrentTrade().memberId = info.id;
        generateBatchOrder();
    }

    // 生成交易记录
    private void generateBatchOrder() {
        XLogger.i(TAG, AppConstants.LOG_BIZ_NORMAL_CASHIER + "补收款生成订单交易：" + RequestConstant.URL_GENERATE_BATCH_ORDER);
        final Trade trade = mTradeManager.getCurrentTrade();
        mView.showLoading();
        if (mGenerateBatchOrderSubscription != null) {
            mGenerateBatchOrderSubscription.unsubscribe();
        }
        mGenerateBatchOrderSubscription = mTradeManager.generateBatchOrder(
                trade.batchNo,
                trade.memberId,
                AppConstants.PAY_CHANNEL_QRCODE.equals(trade.currentChannelType) ? null : trade.currentChannelType,
                null,//订单数据
                mTradeManager.formatVerifyCodes(trade.getCouponList()),
                null,//直减金额
                String.valueOf(trade.getOriginMoney()),
                null,//拆分支付下实际支付金额
                new Callback<String>() {
                    @Override
                    public void onSuccess(String o) {
                        XLogger.i(TAG, AppConstants.LOG_BIZ_NORMAL_CASHIER + "补收款生成订单交易---成功：" + o);
                        mView.hideLoading();
                        if (AppConstants.APP_REQUEST_YES.equals(o)) {
                            trade.tradeStatus = AppConstants.TRADE_STATUS_SUCCESS;
                            EventBus.getDefault().post(new TradeDoneEvent(AppConstants.TRADE_TYPE_NORMAL));
                        } else {
                            switch (trade.currentChannelType) {
                                case AppConstants.PAY_CHANNEL_QRCODE:
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
                        XLogger.e(TAG, AppConstants.LOG_BIZ_NORMAL_CASHIER + "补收款生成订单交易---失败：" + error);
                        mView.hideLoading();
                        mView.showToast(error);
                    }
                });
    }

    @Override
    public void onClickSetCouponInfo() {
        XLogger.i(TAG, AppConstants.LOG_BIZ_NORMAL_CASHIER + "补收款添加优惠");
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
        XLogger.i(TAG, AppConstants.LOG_BIZ_NORMAL_CASHIER + "补收款订单旺POS渠道支付");
        mTradeManager.posPay(mContext, new Callback<Void>() {
            @Override
            public void onSuccess(Void o) {
                XLogger.i(TAG, AppConstants.LOG_BIZ_NORMAL_CASHIER + "补收款订单旺POS渠道支付---成功");
                mView.showLoading();
                startCallBackPos();
            }

            @Override
            public void onError(String error) {
                XLogger.e(TAG, AppConstants.LOG_BIZ_NORMAL_CASHIER + "补收款订单旺POS渠道支付---失败：" + error);
                EventBus.getDefault().post(new TradeDoneEvent(AppConstants.TRADE_TYPE_NORMAL));
            }
        });
    }

    // 汇报Pos支付结果
    private Call<TradeOrderInfoResult> callbackPosCall;
    private RetryPool.RetryRunnable mRetryCallBackPos;
    private boolean resultCallBackPos = false;

    public void startCallBackPos() {
        mRetryCallBackPos = new RetryPool.RetryRunnable(1000, 1.0f, new RetryPool.RetryExecutor() {
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
        XLogger.i(TAG, AppConstants.LOG_BIZ_NORMAL_CASHIER + "补收款订单旺POS渠道支付结果汇报");
        final Trade trade = mTradeManager.getCurrentTrade();
        callbackPosCall = XmdNetwork.getInstance().getService(SpaService.class)
                .callbackHoleOrderSync(AccountManager.getInstance().getToken(),
                        null,
                        trade.currentChannelType,
                        trade.payOrderId,
                        trade.payNo,
                        trade.posPayCashierNo,
                        String.valueOf(trade.getWillPayMoney()));
        XmdNetwork.getInstance().requestSync(callbackPosCall, new NetworkSubscriber<TradeOrderInfoResult>() {
            @Override
            public void onCallbackSuccess(TradeOrderInfoResult result) {
                // 汇报成功
                XLogger.i(TAG, AppConstants.LOG_BIZ_NORMAL_CASHIER + "补收款订单旺POS渠道支付结果汇报---成功");
                resultCallBackPos = true;
                trade.tradeStatus = AppConstants.TRADE_STATUS_SUCCESS;
                trade.resultOrderInfo = result.getRespData().orderDetail;
                EventBus.getDefault().post(new TradeDoneEvent(AppConstants.TRADE_TYPE_NORMAL));
            }

            @Override
            public void onCallbackError(Throwable e) {
                XLogger.e(TAG, AppConstants.LOG_BIZ_NORMAL_CASHIER + "补收款订单旺POS渠道支付结果汇报---失败：" + e.getLocalizedMessage());
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
            mView.hideLoading();
            stopCallBackPos();
            UiNavigation.gotoCashierResultActivity(mContext);
        }
    }
}
