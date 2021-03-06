package com.xmd.cashier.presenter;

import android.content.Context;
import android.content.DialogInterface;

import com.shidou.commonlibrary.helper.RetryPool;
import com.shidou.commonlibrary.helper.XLogger;
import com.shidou.commonlibrary.widget.XToast;
import com.xmd.app.EventBusSafeRegister;
import com.xmd.cashier.UiNavigation;
import com.xmd.cashier.common.AppConstants;
import com.xmd.cashier.common.Utils;
import com.xmd.cashier.contract.InnerModifyContract;
import com.xmd.cashier.dal.bean.MemberInfo;
import com.xmd.cashier.dal.bean.Trade;
import com.xmd.cashier.dal.bean.TradeBatchInfo;
import com.xmd.cashier.dal.bean.TradeChannelInfo;
import com.xmd.cashier.dal.event.InnerGenerateOrderEvent;
import com.xmd.cashier.dal.event.TradeDoneEvent;
import com.xmd.cashier.dal.net.GeneOrderRetrofit;
import com.xmd.cashier.dal.net.RequestConstant;
import com.xmd.cashier.dal.net.SpaService;
import com.xmd.cashier.dal.net.response.TradeBatchResult;
import com.xmd.cashier.dal.net.response.TradeChannelListResult;
import com.xmd.cashier.dal.net.response.TradeOrderInfoResult;
import com.xmd.cashier.manager.AccountManager;
import com.xmd.cashier.manager.Callback;
import com.xmd.cashier.manager.ChannelManager;
import com.xmd.cashier.manager.InnerManager;
import com.xmd.cashier.manager.MemberManager;
import com.xmd.cashier.manager.TradeManager;
import com.xmd.cashier.widget.ActionSheetDialog;
import com.xmd.cashier.widget.CustomAlertDialogBuilder;
import com.xmd.m.network.NetworkException;
import com.xmd.m.network.NetworkSubscriber;
import com.xmd.m.network.XmdNetwork;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import retrofit2.Call;
import rx.Observable;
import rx.Subscription;

/**
 * Created by zr on 17-12-18.
 */

public class InnerModifyPresenter implements InnerModifyContract.Presenter {
    private static final String TAG = "InnerModifyPresenter";

    private Context mContext;
    private InnerModifyContract.View mView;
    private TradeManager mTradeManager;
    private ChannelManager mChannelManager;

    private Subscription mGenerateBatchSubscription;

    public InnerModifyPresenter(Context context, InnerModifyContract.View view) {
        mContext = context;
        mView = view;
        mView.setPresenter(this);
        mTradeManager = TradeManager.getInstance();
        mChannelManager = ChannelManager.getInstance();
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
        if (mChannelManager.getTradeChannelInfos() != null && !mChannelManager.getTradeChannelInfos().isEmpty()) {
            showMethod();
        } else {
            XLogger.i(TAG, AppConstants.LOG_BIZ_NATIVE_CASHIER + "内网收银获取会所支付方式：" + RequestConstant.URL_GET_PAY_CHANNEL_LIST);
            mView.showLoading();
            mChannelManager.getPayChannelList(new Callback<TradeChannelListResult>() {
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
        dialog.setContents(mChannelManager.getCashierChannelTexts());
        dialog.setCancelText("取消");
        dialog.setEventListener(new ActionSheetDialog.OnEventListener() {
            @Override
            public void onActionItemClick(ActionSheetDialog dialog, String item, int position) {
                XLogger.i(TAG, AppConstants.LOG_BIZ_NATIVE_CASHIER + "内网订单选择支付方式:" + item);
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
                                XLogger.i(TAG, AppConstants.LOG_BIZ_NATIVE_CASHIER + "内网订单会员支付：会所会员功能未开通");
                                mView.showError("会所会员功能未开通!");
                            }
                            break;
                        case AppConstants.PAY_CHANNEL_UNION://银行卡
                        case AppConstants.PAY_CHANNEL_QRCODE:
                        case AppConstants.PAY_CHANNEL_WX:   //微信支付
                        case AppConstants.PAY_CHANNEL_ALI:  //支付宝
                        case AppConstants.PAY_CHANNEL_CASH: //现金
                        default:    //记账
                            doCashier();
                            break;
                    }
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
        XLogger.i(TAG, AppConstants.LOG_BIZ_NATIVE_CASHIER + "内网订单发起支付请求：" + RequestConstant.URL_GENERATE_BATCH_ORDER);
        final Trade trade = mTradeManager.getCurrentTrade();
        mView.showLoading();
        if (mGenerateBatchSubscription != null) {
            mGenerateBatchSubscription.unsubscribe();
        }
        Observable<TradeBatchResult> observable = GeneOrderRetrofit.getService()
                .generateBatchOrder(AccountManager.getInstance().getToken(),
                        trade.batchNo,
                        AppConstants.PAY_CHANNEL_ACCOUNT.equals(trade.currentChannelType) ? trade.memberId : null,
                        InnerManager.getInstance().getOrderIds(),
                        AppConstants.PAY_CHANNEL_QRCODE.equals(trade.currentChannelType) ? null : trade.currentChannelType,
                        mTradeManager.formatVerifyCodes(trade.getCouponList()),
                        String.valueOf(trade.getWillReductionMoney()),
                        null,
                        String.valueOf(trade.getWillPayMoney()));
        mGenerateBatchSubscription = XmdNetwork.getInstance().request(observable, new NetworkSubscriber<TradeBatchResult>() {
            @Override
            public void onCallbackSuccess(TradeBatchResult result) {
                mView.hideLoading();
                TradeBatchInfo tradeBatchInfo = result.getRespData();
                trade.batchNo = tradeBatchInfo.batchNo;
                trade.payNo = tradeBatchInfo.payNo;
                trade.payOrderId = tradeBatchInfo.payOrderId;
                trade.payUrl = tradeBatchInfo.payUrl;
                trade.setOriginMoney(tradeBatchInfo.oriAmount);
                trade.setWillDiscountMoney(tradeBatchInfo.discountAmount);
                trade.setWillPayMoney(tradeBatchInfo.payAmount);
                EventBus.getDefault().post(new InnerGenerateOrderEvent());
                if (AppConstants.APP_REQUEST_YES.equals(tradeBatchInfo.status)) {
                    //核销金额已完成抵扣
                    XLogger.i(TAG, AppConstants.LOG_BIZ_NATIVE_CASHIER + "内网订单无需再支付金额");
                    trade.tradeStatus = AppConstants.TRADE_STATUS_SUCCESS;
                    EventBus.getDefault().post(new TradeDoneEvent(AppConstants.TRADE_TYPE_INNER));
                } else {
                    // 需要支付
                    switch (mTradeManager.getCurrentTrade().currentChannelType) {
                        case AppConstants.PAY_CHANNEL_QRCODE:
                        case AppConstants.PAY_CHANNEL_ALI:
                        case AppConstants.PAY_CHANNEL_WX:
                            UiNavigation.gotoTradeQrcodePayActivity(mContext, AppConstants.TRADE_TYPE_INNER);
                            break;
                        case AppConstants.PAY_CHANNEL_ACCOUNT:  //会员
                            UiNavigation.gotoTradeMemberPayActivity(mContext, AppConstants.TRADE_TYPE_INNER);
                            break;
                        case AppConstants.PAY_CHANNEL_UNION:     //银联
                            posCashier();
                            break;
                        case AppConstants.PAY_CHANNEL_CASH:
                        default:
                            UiNavigation.gotoTradeMarkPayActivity(mContext, AppConstants.TRADE_TYPE_INNER);
                            break;
                    }
                }
            }

            @Override
            public void onCallbackError(Throwable e) {
                if (e instanceof NetworkException) {
                    mView.showToast("网络状况不佳，正在努力加载...");
                    doCashierAgain();
                } else {
                    XLogger.e(TAG, AppConstants.LOG_BIZ_NATIVE_CASHIER + "内网订单发起支付---失败:" + e.getMessage());
                    mView.hideLoading();
                    // FIXME 如果后台描述修改,需要相应变更
                    if (e.getMessage().contains("订单已被支付锁定")) {
                        new CustomAlertDialogBuilder(mContext)
                                .setMessage("支付订单已存在，请前往结账提醒列表完成支付")
                                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(final DialogInterface dialog, int which) {
                                        XLogger.i(TAG, AppConstants.LOG_BIZ_NATIVE_CASHIER + "订单锁定时,继续支付");
                                        dialog.dismiss();
                                    }
                                })
                                .setPositiveButton("去支付", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        XLogger.i(TAG, AppConstants.LOG_BIZ_NATIVE_CASHIER + "订单锁定时,访问订单列表");
                                        dialog.dismiss();
                                        EventBus.getDefault().post(new InnerGenerateOrderEvent());
                                        UiNavigation.gotoInnerRecordActivity(mContext);
                                        mView.finishSelf();
                                    }
                                })
                                .create()
                                .show();
                    } else {
                        mView.showError(e.getMessage());
                    }
                }
            }
        });
    }

    private void doCashierAgain() {
        XLogger.i(TAG, AppConstants.LOG_BIZ_NATIVE_CASHIER + "内网订单发起支付请求 again：" + RequestConstant.URL_GENERATE_BATCH_ORDER);
        final Trade trade = mTradeManager.getCurrentTrade();
        if (mGenerateBatchSubscription != null) {
            mGenerateBatchSubscription.unsubscribe();
        }
        Observable<TradeBatchResult> observable = GeneOrderRetrofit.getService()
                .generateBatchOrder(AccountManager.getInstance().getToken(),
                        trade.batchNo,
                        AppConstants.PAY_CHANNEL_ACCOUNT.equals(trade.currentChannelType) ? trade.memberId : null,
                        InnerManager.getInstance().getOrderIds(),
                        AppConstants.PAY_CHANNEL_QRCODE.equals(trade.currentChannelType) ? null : trade.currentChannelType,
                        mTradeManager.formatVerifyCodes(trade.getCouponList()),
                        String.valueOf(trade.getWillReductionMoney()),
                        null,
                        String.valueOf(trade.getWillPayMoney()));
        mGenerateBatchSubscription = XmdNetwork.getInstance().request(observable, new NetworkSubscriber<TradeBatchResult>() {
            @Override
            public void onCallbackSuccess(TradeBatchResult result) {
                mView.hideLoading();
                TradeBatchInfo tradeBatchInfo = result.getRespData();
                trade.batchNo = tradeBatchInfo.batchNo;
                trade.payNo = tradeBatchInfo.payNo;
                trade.payOrderId = tradeBatchInfo.payOrderId;
                trade.payUrl = tradeBatchInfo.payUrl;
                trade.setOriginMoney(tradeBatchInfo.oriAmount);
                trade.setWillDiscountMoney(tradeBatchInfo.discountAmount);
                trade.setWillPayMoney(tradeBatchInfo.payAmount);
                EventBus.getDefault().post(new InnerGenerateOrderEvent());
                if (AppConstants.APP_REQUEST_YES.equals(tradeBatchInfo.status)) {
                    //核销金额已完成抵扣
                    XLogger.i(TAG, AppConstants.LOG_BIZ_NATIVE_CASHIER + "内网订单无需再支付金额");
                    trade.tradeStatus = AppConstants.TRADE_STATUS_SUCCESS;
                    EventBus.getDefault().post(new TradeDoneEvent(AppConstants.TRADE_TYPE_INNER));
                } else {
                    // 需要支付
                    switch (mTradeManager.getCurrentTrade().currentChannelType) {
                        case AppConstants.PAY_CHANNEL_QRCODE:
                        case AppConstants.PAY_CHANNEL_ALI:
                        case AppConstants.PAY_CHANNEL_WX:
                            UiNavigation.gotoTradeQrcodePayActivity(mContext, AppConstants.TRADE_TYPE_INNER);
                            break;
                        case AppConstants.PAY_CHANNEL_ACCOUNT:  //会员
                            UiNavigation.gotoTradeMemberPayActivity(mContext, AppConstants.TRADE_TYPE_INNER);
                            break;
                        case AppConstants.PAY_CHANNEL_UNION:     //银联
                            posCashier();
                            break;
                        case AppConstants.PAY_CHANNEL_CASH:
                        default:
                            UiNavigation.gotoTradeMarkPayActivity(mContext, AppConstants.TRADE_TYPE_INNER);
                            break;
                    }
                }
            }

            @Override
            public void onCallbackError(Throwable e) {
                XLogger.e(TAG, AppConstants.LOG_BIZ_NATIVE_CASHIER + "内网订单发起支付---失败:" + e.getMessage());
                mView.hideLoading();
                // FIXME 如果后台描述修改,需要相应变更
                if (e.getMessage().contains("订单已被支付锁定")) {
                    new CustomAlertDialogBuilder(mContext)
                            .setMessage("支付订单已存在，请前往结账提醒列表完成支付")
                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(final DialogInterface dialog, int which) {
                                    XLogger.i(TAG, AppConstants.LOG_BIZ_NATIVE_CASHIER + "订单锁定时,继续支付");
                                    dialog.dismiss();
                                }
                            })
                            .setPositiveButton("去支付", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    XLogger.i(TAG, AppConstants.LOG_BIZ_NATIVE_CASHIER + "订单锁定时,访问订单列表");
                                    dialog.dismiss();
                                    EventBus.getDefault().post(new InnerGenerateOrderEvent());
                                    UiNavigation.gotoInnerRecordActivity(mContext);
                                    mView.finishSelf();
                                }
                            })
                            .create()
                            .show();
                } else {
                    mView.showError(e.getMessage());
                }
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
                mView.showLoading();
                startCallBackBatch();
            }

            @Override
            public void onError(String error) {
                XLogger.e(TAG, AppConstants.LOG_BIZ_NATIVE_CASHIER + "内网订单旺POS渠道支付---失败:" + error);
                EventBus.getDefault().post(new TradeDoneEvent(AppConstants.TRADE_TYPE_INNER));
            }
        });
    }

    // 汇报内网支付
    private Call<TradeOrderInfoResult> callbackBatchCall;
    private RetryPool.RetryRunnable mRetryCallBackBatch;
    private boolean resultCallBackBatch = false;

    public void startCallBackBatch() {
        mRetryCallBackBatch = new RetryPool.RetryRunnable(1000, 1.0f, new RetryPool.RetryExecutor() {
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
                .callbackHoleOrderSync(AccountManager.getInstance().getToken(),
                        null,
                        mTradeManager.getCurrentTrade().currentChannelType,
                        mTradeManager.getCurrentTrade().payOrderId,
                        mTradeManager.getCurrentTrade().payNo,
                        mTradeManager.getCurrentTrade().posPayCashierNo,
                        String.valueOf(mTradeManager.getCurrentTrade().getWillPayMoney()));
        XmdNetwork.getInstance().requestSync(callbackBatchCall, new NetworkSubscriber<TradeOrderInfoResult>() {
            @Override
            public void onCallbackSuccess(TradeOrderInfoResult result) {
                XLogger.i(TAG, AppConstants.LOG_BIZ_NATIVE_CASHIER + "内网订单旺POS渠道支付标记支付结果---成功");
                resultCallBackBatch = true;
                mTradeManager.getCurrentTrade().tradeStatus = AppConstants.TRADE_STATUS_SUCCESS;
                mTradeManager.getCurrentTrade().resultOrderInfo = result.getRespData().orderDetail;
                EventBus.getDefault().post(new TradeDoneEvent(AppConstants.TRADE_TYPE_INNER));
            }

            @Override
            public void onCallbackError(Throwable e) {
                XLogger.e(TAG, AppConstants.LOG_BIZ_NATIVE_CASHIER + "内网订单旺POS渠道支付标记支付结果---失败:" + e.getLocalizedMessage());
                if ((e instanceof NetworkException) && !e.getLocalizedMessage().equals("Canceled")) {
                    XToast.show("网络状况不佳，正在努力加载...");
                }
                resultCallBackBatch = false;
            }
        });
        return resultCallBackBatch;
    }

    // 查询会员信息
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(MemberInfo info) {
        mTradeManager.getCurrentTrade().memberInfo = info;
        mTradeManager.getCurrentTrade().memberId = info.id;
        doCashier();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(TradeDoneEvent tradeDoneEvent) {
        if (tradeDoneEvent.type == AppConstants.TRADE_TYPE_INNER) {
            mView.hideLoading();
            UiNavigation.gotoInnerResultActivity(mContext);
            stopCallBackBatch();
            mView.finishSelf();
        }
    }
}
