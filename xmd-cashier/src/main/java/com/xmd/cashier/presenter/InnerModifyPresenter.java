package com.xmd.cashier.presenter;

import android.content.Context;
import android.content.DialogInterface;

import com.shidou.commonlibrary.helper.RetryPool;
import com.shidou.commonlibrary.helper.XLogger;
import com.shidou.commonlibrary.util.DateUtils;
import com.xmd.app.EventBusSafeRegister;
import com.xmd.cashier.UiNavigation;
import com.xmd.cashier.common.AppConstants;
import com.xmd.cashier.common.Utils;
import com.xmd.cashier.contract.InnerModifyContract;
import com.xmd.cashier.dal.bean.InnerBatchInfo;
import com.xmd.cashier.dal.bean.MemberInfo;
import com.xmd.cashier.dal.bean.Trade;
import com.xmd.cashier.dal.event.InnerGenerateOrderEvent;
import com.xmd.cashier.dal.net.SpaService;
import com.xmd.cashier.dal.net.response.GetTradeNoResult;
import com.xmd.cashier.dal.net.response.InnerBatchResult;
import com.xmd.cashier.manager.AccountManager;
import com.xmd.cashier.manager.Callback;
import com.xmd.cashier.manager.CashierManager;
import com.xmd.cashier.manager.DataReportManager;
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

import java.util.ArrayList;

import retrofit2.Call;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

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
    }

    @Override
    public void onRealPayChange() {
        mTradeManager.getCurrentTrade().setRealPayMoney(mView.getRealPayMoney());
        mView.setDesc(mTradeManager.getCurrentTrade().getRealPayMoney(), mTradeManager.getCurrentTrade().getWillPayMoney() - mTradeManager.getCurrentTrade().getAlreadyPayMoney() - mTradeManager.getCurrentTrade().getRealPayMoney());
    }

    @Override
    public void onCashier() {
        // FIXME 拆分支付时需要限制输入金额
        /*if (mTradeManager.getCurrentTrade().getRealPayMoney() <= 0) {
            mView.showError("请输入支付金额");
            return;
        }*/
        showMethod();
    }

    @Override
    public void processData() {
        Trade trade = mTradeManager.getCurrentTrade();
        trade.setRealPayMoney(trade.getWillPayMoney());
        mView.setOrigin("￥" + Utils.moneyToStringEx(trade.getOriginMoney()));
        mView.setDiscount("￥" + Utils.moneyToStringEx(trade.getAlreadyCutMoney()));
        mView.setAlreadyPay("￥" + Utils.moneyToStringEx(trade.getAlreadyPayMoney()));
        mView.setLeftPay("￥" + Utils.moneyToStringEx(trade.getWillPayMoney() - trade.getAlreadyPayMoney()));
        mView.setInput(trade.getWillPayMoney() - trade.getAlreadyPayMoney());
        mView.setDesc(trade.getRealPayMoney(), trade.getWillPayMoney() - trade.getAlreadyPayMoney() - trade.getRealPayMoney());
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
        dialog.setContents(new ArrayList<>(InnerManager.getInstance().getChannels().keySet()));
        dialog.setCancelText("取消");
        dialog.setEventListener(new ActionSheetDialog.OnEventListener() {
            @Override
            public void onActionItemClick(ActionSheetDialog dialog, String item, int position) {
                String innerType = InnerManager.getInstance().getChannels().get(item);
                XLogger.i(TAG, "内网订单选择支付方式:" + innerType);
                mTradeManager.getCurrentTrade().currentCashierName = item;  //支付方式名称
                mTradeManager.getCurrentTrade().currentCashierType = innerType;
                switch (innerType) {
                    case AppConstants.PAY_CHANNEL_WX:   //微信支付
                    case AppConstants.PAY_CHANNEL_ALI:  //支付宝
                        mTradeManager.getCurrentTrade().currentCashier = AppConstants.CASHIER_TYPE_QRCODE;
                        doCashier();
                        break;
                    case AppConstants.PAY_CHANNEL_UNION:    //银行卡
                        mTradeManager.getCurrentTrade().currentCashier = AppConstants.CASHIER_TYPE_POS;
                        posTradeNo();
                        break;
                    case AppConstants.PAY_CHANNEL_CASH: //现金
                        mTradeManager.getCurrentTrade().currentCashier = AppConstants.CASHIER_TYPE_CASH;
                        doCashier();
                        break;
                    case AppConstants.PAY_CHANNEL_ACCOUNT:  //会员
                        if (AppConstants.APP_REQUEST_YES.equals(MemberManager.getInstance().getMemberSwitch())) {
                            mTradeManager.getCurrentTrade().currentCashier = AppConstants.CASHIER_TYPE_MEMBER;
                            UiNavigation.gotoMemberReadActivity(mContext, AppConstants.MEMBER_BUSINESS_TYPE_PAYMENT);
                        } else {
                            XLogger.i(TAG, "内网订单会员支付:会所会员功能未开通");
                            mView.showError("会所会员功能未开通!");
                        }
                        break;
                    default:    //记账
                        mTradeManager.getCurrentTrade().currentCashier = AppConstants.CASHIER_TYPE_MARK;
                        mTradeManager.getCurrentTrade().currentCashierMark = InnerManager.getInstance().getChannelMark(innerType);
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
        XLogger.i(TAG, "内网订单发起支付请求");
        final Trade trade = mTradeManager.getCurrentTrade();
        mGenerateBatchSubscription = mTradeManager.generateInnerBatch(
                trade.batchNo,
                trade.memberId,
                trade.currentCashierType,
                InnerManager.getInstance().getOrderIds(),
                mTradeManager.formatVerifyCodes(trade.getCouponList()),
                String.valueOf(trade.getWillReductionMoney()),
                String.valueOf(trade.getRealPayMoney()),
                new Callback<InnerBatchResult>() {
                    @Override
                    public void onSuccess(InnerBatchResult o) {
                        mView.hideLoading();
                        if (o != null && o.getRespData() != null) {
                            EventBus.getDefault().post(new InnerGenerateOrderEvent());
                            InnerBatchInfo batchInfo = o.getRespData();
                            trade.batchNo = batchInfo.batchNo;
                            trade.payOrderId = batchInfo.payOrderId;
                            trade.subPayOrderId = batchInfo.payNo;
                            trade.payUrl = batchInfo.payUrl;
                            trade.setOriginMoney(batchInfo.oriAmount);
                            trade.setWillDiscountMoney(batchInfo.discountAmount);
                            trade.setWillPayMoney(batchInfo.payAmount);
                            trade.tradeTime = DateUtils.getCurrentDate();

                            if (AppConstants.APP_REQUEST_YES.equals(batchInfo.status)) {
                                //核销金额已完成抵扣
                                XLogger.i(TAG, "内网订单无需再支付金额");
                                trade.tradeStatus = AppConstants.TRADE_STATUS_SUCCESS;
                                UiNavigation.gotoInnerResultActivity(mContext);
                                mView.finishSelf();
                            } else {
                                // 需要支付
                                switch (mTradeManager.getCurrentTrade().currentCashier) {
                                    case AppConstants.CASHIER_TYPE_QRCODE:  //POS扫码
                                    case AppConstants.CASHIER_TYPE_CASH:    //现金
                                    case AppConstants.CASHIER_TYPE_MARK:    //自定义记账
                                    case AppConstants.CASHIER_TYPE_MEMBER:  //会员
                                        UiNavigation.gotoInnerPaymentActivity(mContext);
                                        mView.finishSelf();
                                        break;
                                    case AppConstants.CASHIER_TYPE_POS:     //银联
                                        posCashier(trade.getRealPayMoney());
                                        break;
                                    default:
                                        break;
                                }
                            }
                        } else {
                            mView.showToast("数据异常，请联系业务管理员");
                        }
                    }

                    @Override
                    public void onError(String error) {
                        XLogger.e(TAG, "内网订单发起支付---失败:" + error);
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
        mPosTradeNoSubscription = mTradeManager.fetchTradeNo(new Callback<GetTradeNoResult>() {
            @Override
            public void onSuccess(GetTradeNoResult o) {
                mView.hideLoading();
                doCashier();
            }

            @Override
            public void onError(String error) {
                mView.hideLoading();
                mView.showError("获取订单号失败，请重试");
            }
        });
    }


    // 旺POS渠道支付
    private void posCashier(int money) {
        XLogger.i(TAG, "内网订单旺POS渠道支付");
        mTradeManager.posPay(mContext, money, new Callback<Void>() {
            @Override
            public void onSuccess(Void o) {
                XLogger.i(TAG, "内网订单旺POS渠道支付---成功");
                mView.showToast("支付成功！");
                reportTrade();
                startCallBackBatch();
            }

            @Override
            public void onError(String error) {
                XLogger.e(TAG, "内网订单旺POS渠道支付---失败:" + error);
                mView.hideLoading();
                if (CashierManager.getInstance().isUserCancel(mTradeManager.getCurrentTrade().posPayReturn)) {
                    mView.showToast("支付失败：已取消支付");
                } else {
                    mView.showToast("支付失败：" + error);
                }
                mTradeManager.getCurrentTrade().tradeStatus = AppConstants.TRADE_STATUS_CANCEL;
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
        mRetryCallBackBatch = new RetryPool.RetryRunnable(3000, 1.0f, new RetryPool.RetryExecutor() {
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
        XLogger.i(TAG, "内网订单旺POS渠道支付标记支付结果");
        callbackBatchCall = XmdNetwork.getInstance().getService(SpaService.class)
                .callbackInnerBatchOrderSync(AccountManager.getInstance().getToken(),
                        null,
                        mTradeManager.getCurrentTrade().currentCashierType,
                        mTradeManager.getCurrentTrade().payOrderId,
                        mTradeManager.getCurrentTrade().tradeNo,
                        CashierManager.getInstance().getTradeNo(mTradeManager.getCurrentTrade().posPayReturn),
                        String.valueOf(mTradeManager.getCurrentTrade().getRealPayMoney()));
        XmdNetwork.getInstance().requestSync(callbackBatchCall, new NetworkSubscriber<BaseBean>() {
            @Override
            public void onCallbackSuccess(BaseBean result) {
                XLogger.i(TAG, "内网订单旺POS渠道支付标记支付结果---成功");
                resultCallBackBatch = true;
                mView.hideLoading();
                mTradeManager.getCurrentTrade().tradeStatus = AppConstants.TRADE_STATUS_SUCCESS;
                UiNavigation.gotoInnerResultActivity(mContext);
                mView.finishSelf();
            }

            @Override
            public void onCallbackError(Throwable e) {
                XLogger.e(TAG, "内网订单旺POS渠道支付标记支付结果---失败:" + e.getLocalizedMessage());
                resultCallBackBatch = false;
            }
        });
        return resultCallBackBatch;
    }

    // 查询会员信息
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(MemberInfo info) {
        doCashier();
    }

    // POS渠道支付时需要汇报
    private void reportTrade() {
        XLogger.i(TAG, "内网订单旺POS渠道支付进行汇报PosPayDeal");
        Observable
                .create(new Observable.OnSubscribe<Void>() {
                    @Override
                    public void call(Subscriber<? super Void> subscriber) {
                        DataReportManager.getInstance().reportData(TradeManager.getInstance().getCurrentTrade(), AppConstants.REPORT_DATA_BIZ_INNER);
                        subscriber.onNext(null);
                        subscriber.onCompleted();
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();
    }
}
