package com.xmd.cashier.presenter;

import android.content.Context;
import android.content.DialogInterface;

import com.shidou.commonlibrary.helper.RetryPool;
import com.shidou.commonlibrary.helper.XLogger;
import com.xmd.app.EventBusSafeRegister;
import com.xmd.cashier.UiNavigation;
import com.xmd.cashier.common.AppConstants;
import com.xmd.cashier.common.Utils;
import com.xmd.cashier.contract.InnerModifyContract;
import com.xmd.cashier.dal.bean.MemberInfo;
import com.xmd.cashier.dal.bean.Trade;
import com.xmd.cashier.dal.event.InnerGenerateOrderEvent;
import com.xmd.cashier.dal.net.RequestConstant;
import com.xmd.cashier.dal.net.SpaService;
import com.xmd.cashier.dal.net.response.GetTradeNoResult;
import com.xmd.cashier.dal.net.response.TradeChannelListResult;
import com.xmd.cashier.manager.AccountManager;
import com.xmd.cashier.manager.Callback;
import com.xmd.cashier.manager.InnerManager;
import com.xmd.cashier.manager.MemberManager;
import com.xmd.cashier.manager.TradeManager;
import com.xmd.cashier.widget.ActionSheetDialog;
import com.xmd.cashier.widget.CustomAlertDialogBuilder;
import com.xmd.m.network.BaseBean;
import com.xmd.m.network.NetworkSubscriber;
import com.xmd.m.network.XmdNetwork;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import retrofit2.Call;
import rx.Subscription;

/**
 * Created by zr on 17-12-18.
 */

public class InnerModifyPresenter implements InnerModifyContract.Presenter {
    private static final String TAG = "InnerModifyPresenter";

    private Context mContext;
    private InnerModifyContract.View mView;
    private TradeManager mTradeManager;

    private Subscription mGenerateBatchSubscription;
    private Subscription mPosTradeNoSubscription;
    private Subscription mGetTradeChannelSubscription;

    public InnerModifyPresenter(Context context, InnerModifyContract.View view) {
        mContext = context;
        mView = view;
        mView.setPresenter(this);
        mTradeManager = TradeManager.getInstance();
    }

    @Override
    public void onCreate() {
        EventBusSafeRegister.register(this);
    }

    @Override
    public void onStart() {

    }

    @Override
    public void onDestroy() {
        stopCallBackBatch();
        EventBusSafeRegister.unregister(this);
        if (mGenerateBatchSubscription != null) {
            mGenerateBatchSubscription.unsubscribe();
        }
        if (mPosTradeNoSubscription != null) {
            mPosTradeNoSubscription.unsubscribe();
        }
        if (mGetTradeChannelSubscription != null) {
            mGetTradeChannelSubscription.unsubscribe();
        }
    }

    @Override
    public void onRealPayChange() {
        mTradeManager.getCurrentTrade().setWillPayMoney(mView.getRealPayMoney());
        mView.setDesc(mTradeManager.getCurrentTrade().getWillPayMoney(), mTradeManager.getCurrentTrade().getWillPayMoney() - mTradeManager.getCurrentTrade().getAlreadyPayMoney() - mTradeManager.getCurrentTrade().getWillPayMoney());
    }

    @Override
    public void onCashier() {
        // FIXME 拆分支付时需要限制输入金额
        /*if (mTradeManager.getCurrentTrade().getRealPayMoney() <= 0) {
            mView.showError("请输入支付金额");
            return;
        }*/
        if (mTradeManager.getTradeChannelInfos() != null && !mTradeManager.getTradeChannelInfos().isEmpty()) {
            showMethod();
        } else {
            mView.showLoading();
            if (mGetTradeChannelSubscription != null) {
                mGetTradeChannelSubscription.unsubscribe();
            }
            mGetTradeChannelSubscription = mTradeManager.getPayChannelList(new Callback<TradeChannelListResult>() {
                @Override
                public void onSuccess(TradeChannelListResult o) {
                    mView.hideLoading();
                    showMethod();
                }

                @Override
                public void onError(String error) {
                    mView.hideLoading();
                    mView.showToast("获取支付方式失败：" + error);
                }
            });
        }
    }

    @Override
    public void processData() {
        Trade trade = mTradeManager.getCurrentTrade();
        trade.setWillPayMoney(trade.getWillPayMoney());
        mView.setOrigin("￥" + Utils.moneyToStringEx(trade.getOriginMoney()));
        mView.setDiscount("￥" + Utils.moneyToStringEx(trade.getAlreadyCutMoney()));
        mView.setAlreadyPay("￥" + Utils.moneyToStringEx(trade.getAlreadyPayMoney()));
        mView.setLeftPay("￥" + Utils.moneyToStringEx(trade.getWillPayMoney() - trade.getAlreadyPayMoney()));
        mView.setInput(trade.getWillPayMoney() - trade.getAlreadyPayMoney());
        mView.setDesc(trade.getWillPayMoney(), trade.getWillPayMoney() - trade.getAlreadyPayMoney() - trade.getWillPayMoney());
    }

    @Override
    public void onEventBack() {
        new CustomAlertDialogBuilder(mContext)
                .setMessage("订单金额未付清，确认要退出支付?")
                .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        mView.finishSelf();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create()
                .show();
    }

    private void showMethod() {
        ActionSheetDialog dialog = new ActionSheetDialog(mContext);
        dialog.setContents(mTradeManager.getTradeChannelTexts());
        dialog.setCancelText("取消");
        dialog.setEventListener(new ActionSheetDialog.OnEventListener() {
            @Override
            public void onActionItemClick(ActionSheetDialog dialog, String item, int position) {
                mTradeManager.setCurrentChannel(item);
                XLogger.i(TAG, AppConstants.LOG_BIZ_NATIVE_CASHIER + "内网订单选择支付方式:" + item);
                switch (mTradeManager.getCurrentTrade().currentChannelType) {
                    case AppConstants.PAY_CHANNEL_UNION:    //银行卡
                        posTradeNo();
                        break;
                    case AppConstants.PAY_CHANNEL_ACCOUNT:  //会员
                        if (AppConstants.APP_REQUEST_YES.equals(MemberManager.getInstance().getMemberSwitch())) {
                            UiNavigation.gotoMemberReadActivity(mContext, AppConstants.MEMBER_BUSINESS_TYPE_PAYMENT);
                        } else {
                            XLogger.i(TAG, AppConstants.LOG_BIZ_NATIVE_CASHIER + "内网订单会员支付：会所会员功能未开通");
                            mView.showError("会所会员功能未开通!");
                        }
                        break;
                    case AppConstants.PAY_CHANNEL_WX:   //微信支付
                    case AppConstants.PAY_CHANNEL_ALI:  //支付宝
                    case AppConstants.PAY_CHANNEL_CASH: //现金
                    default:    //记账
                        doCashier();
                        break;
                }
                dialog.dismiss();
            }

            @Override
            public void onCancelItemClick(ActionSheetDialog dialog) {
                dialog.dismiss();
            }
        });
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }

    private void doCashier() {
        mView.showLoading();
        if (mGenerateBatchSubscription != null) {
            mGenerateBatchSubscription.unsubscribe();
        }
        XLogger.i(TAG, AppConstants.LOG_BIZ_NATIVE_CASHIER + "内网订单发起支付请求：" + RequestConstant.URL_GENERATE_BATCH_ORDER);
        final Trade trade = mTradeManager.getCurrentTrade();
        mGenerateBatchSubscription = mTradeManager.generateBatchOrder(
                trade.batchNo,
                trade.memberId,
                trade.currentChannelType,
                InnerManager.getInstance().getOrderIds(),
                mTradeManager.formatVerifyCodes(trade.getCouponList()),
                String.valueOf(trade.getWillReductionMoney()),
                null,
                String.valueOf(trade.getWillPayMoney()),
                new Callback<String>() {
                    @Override
                    public void onSuccess(String o) {
                        mView.hideLoading();
                        EventBus.getDefault().post(new InnerGenerateOrderEvent());
                        if (AppConstants.APP_REQUEST_YES.equals(o)) {
                            //核销金额已完成抵扣
                            XLogger.i(TAG, AppConstants.LOG_BIZ_NATIVE_CASHIER + "内网订单无需再支付金额");
                            trade.tradeStatus = AppConstants.TRADE_STATUS_SUCCESS;
                            UiNavigation.gotoInnerResultActivity(mContext);
                            mView.finishSelf();
                        } else {
                            // 需要支付
                            switch (mTradeManager.getCurrentTrade().currentChannelType) {
                                case AppConstants.PAY_CHANNEL_ALI:  //POS扫码
                                case AppConstants.PAY_CHANNEL_WX:    //现金
                                case AppConstants.PAY_CHANNEL_CASH:    //自定义记账
                                case AppConstants.PAY_CHANNEL_ACCOUNT:  //会员
                                    UiNavigation.gotoInnerPaymentActivity(mContext);
                                    mView.finishSelf();
                                    break;
                                case AppConstants.PAY_CHANNEL_UNION:     //银联
                                    posCashier();
                                    break;
                                default:
                                    break;
                            }
                        }
                    }

                    @Override
                    public void onError(String error) {
                        XLogger.e(TAG, AppConstants.LOG_BIZ_NATIVE_CASHIER + "内网订单发起支付---失败:" + error);
                        mView.hideLoading();
                        // FIXME 如果后台描述修改,需要相应变更
                        if (error.contains("订单已被支付锁定")) {
                            new CustomAlertDialogBuilder(mContext)
                                    .setMessage("支付订单已存在，请前往结账提醒列表完成支付")
                                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(final DialogInterface dialog, int which) {
                                            XLogger.i(TAG, "订单锁定时,继续支付");
                                            dialog.dismiss();
                                        }
                                    })
                                    .setPositiveButton("去支付", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            XLogger.i(TAG, "订单锁定时,访问订单列表");
                                            dialog.dismiss();
                                            EventBus.getDefault().post(new InnerGenerateOrderEvent());
                                            UiNavigation.gotoInnerRecordActivity(mContext);
                                            mView.finishSelf();
                                        }
                                    })
                                    .create()
                                    .show();
                        } else {
                            mView.showError(error);
                        }
                    }
                });
    }

    // 生成交易号
    private void posTradeNo() {
        mView.showLoading();
        if (mPosTradeNoSubscription != null) {
            mPosTradeNoSubscription.unsubscribe();
        }
        XLogger.i(TAG, AppConstants.LOG_BIZ_NATIVE_CASHIER + "内网订单生成交易号：" + RequestConstant.URL_GET_TRADE_NO);
        mPosTradeNoSubscription = mTradeManager.fetchTradeNo(new Callback<GetTradeNoResult>() {
            @Override
            public void onSuccess(GetTradeNoResult o) {
                XLogger.i(TAG, AppConstants.LOG_BIZ_NATIVE_CASHIER + "内网订单生成交易号---成功：" + mTradeManager.getCurrentTrade().tradeNo);
                mView.hideLoading();
                doCashier();
            }

            @Override
            public void onError(String error) {
                XLogger.e(TAG, AppConstants.LOG_BIZ_NATIVE_CASHIER + "内网订单生成交易号---失败：" + error);
                mView.hideLoading();
                mView.showError("获取订单号失败：" + error);
            }
        });
    }


    // 旺POS渠道支付
    private void posCashier() {
        XLogger.i(TAG, AppConstants.LOG_BIZ_NATIVE_CASHIER + "内网订单旺POS渠道支付");
        mTradeManager.posPay(mContext, new Callback<Void>() {
            @Override
            public void onSuccess(Void o) {
                XLogger.i(TAG, AppConstants.LOG_BIZ_NATIVE_CASHIER + "内网订单旺POS渠道支付---成功");
                mView.showToast("支付成功！");
                startCallBackBatch();
            }

            @Override
            public void onError(String error) {
                XLogger.e(TAG, AppConstants.LOG_BIZ_NATIVE_CASHIER + "内网订单旺POS渠道支付---失败:" + error);
                mView.hideLoading();
                mTradeManager.getCurrentTrade().tradeStatus = AppConstants.TRADE_STATUS_ERROR;
                mTradeManager.getCurrentTrade().tradeStatusError = error;
                UiNavigation.gotoInnerResultActivity(mContext);
                mView.finishSelf();
            }
        });
    }

    // 汇报内网支付
    private Call<BaseBean> callbackBatchCall;
    private RetryPool.RetryRunnable mRetryCallBackBatch;
    private boolean resultCallBackBatch = false;

    public void startCallBackBatch() {
        mRetryCallBackBatch = new RetryPool.RetryRunnable(AppConstants.TINNY_INTERVAL, 1.0f, new RetryPool.RetryExecutor() {
            @Override
            public boolean run() {
                return callbackBatch();
            }
        });
        RetryPool.getInstance().postWork(mRetryCallBackBatch);
    }

    public void stopCallBackBatch() {
        if (callbackBatchCall != null && !callbackBatchCall.isCanceled()) {
            callbackBatchCall.cancel();
        }
        if (mRetryCallBackBatch != null) {
            RetryPool.getInstance().removeWork(mRetryCallBackBatch);
            mRetryCallBackBatch = null;
        }
    }

    private boolean callbackBatch() {
        XLogger.i(TAG, AppConstants.LOG_BIZ_NATIVE_CASHIER + "内网订单旺POS渠道支付标记支付结果：" + RequestConstant.URL_CALLBACK_BATCH_ORDER);
        callbackBatchCall = XmdNetwork.getInstance().getService(SpaService.class)
                .callbackBatchOrderSync(AccountManager.getInstance().getToken(),
                        null,
                        mTradeManager.getCurrentTrade().currentChannelType,
                        mTradeManager.getCurrentTrade().payOrderId,
                        mTradeManager.getCurrentTrade().tradeNo,
                        mTradeManager.getCurrentTrade().posPayCashierNo,
                        String.valueOf(mTradeManager.getCurrentTrade().getWillPayMoney()));
        XmdNetwork.getInstance().requestSync(callbackBatchCall, new NetworkSubscriber<BaseBean>() {
            @Override
            public void onCallbackSuccess(BaseBean result) {
                XLogger.i(TAG, AppConstants.LOG_BIZ_NATIVE_CASHIER + "内网订单旺POS渠道支付标记支付结果---成功");
                resultCallBackBatch = true;
                mView.hideLoading();
                mTradeManager.getCurrentTrade().tradeStatus = AppConstants.TRADE_STATUS_SUCCESS;
                UiNavigation.gotoInnerResultActivity(mContext);
                mView.finishSelf();
            }

            @Override
            public void onCallbackError(Throwable e) {
                XLogger.e(TAG, AppConstants.LOG_BIZ_NATIVE_CASHIER + "内网订单旺POS渠道支付标记支付结果---失败:" + e.getLocalizedMessage());
                resultCallBackBatch = false;
            }
        });
        return resultCallBackBatch;
    }

    // 查询会员信息
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(MemberInfo info) {
        mTradeManager.getCurrentTrade().memberInfo = info;
        mTradeManager.getCurrentTrade().memberId = String.valueOf(info.id);
        doCashier();
    }
}
