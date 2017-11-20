package com.xmd.cashier.presenter;

import android.content.Context;
import android.content.DialogInterface;

import com.shidou.commonlibrary.helper.RetryPool;
import com.shidou.commonlibrary.helper.XLogger;
import com.shidou.commonlibrary.util.DateUtils;
import com.xmd.cashier.UiNavigation;
import com.xmd.cashier.common.AppConstants;
import com.xmd.cashier.contract.InnerMethodContract;
import com.xmd.cashier.dal.bean.InnerBatchInfo;
import com.xmd.cashier.dal.bean.InnerOrderInfo;
import com.xmd.cashier.dal.bean.InnerRecordInfo;
import com.xmd.cashier.dal.bean.MemberInfo;
import com.xmd.cashier.dal.bean.Trade;
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
 * Created by zr on 17-11-2.
 */

public class InnerMethodPresenter implements InnerMethodContract.Presenter {
    private TradeManager mTradeManager;
    private Context mContext;
    private InnerMethodContract.View mView;

    private Subscription mGenerateBatchSubscription;
    private Subscription mPosTradeNoSubscription;

    private boolean mHoleSelect = true;

    public InnerMethodPresenter(Context context, InnerMethodContract.View view) {
        mContext = context;
        mView = view;
        mView.setPresenter(this);
        mTradeManager = TradeManager.getInstance();
    }

    @Override
    public void onCreate() {
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStart() {
        updateAmount();
    }

    private void updateAmount() {
        Trade trade = mTradeManager.getCurrentTrade();
        if (trade.getVerificationCount() > 0) {
            mView.showVerifyDesc("已选择" + trade.getVerificationCount() + "张");
        } else {
            mView.hideVerifyDesc();
        }
        int origin = trade.getOriginMoney();
        int discount = TradeManager.getInstance().getDiscountAmount(trade.getCouponList());
        trade.setWillDiscountMoney(discount);
        if (origin < discount) {
            trade.setWillPayMoney(0);
        } else {
            trade.setWillPayMoney(origin - discount);
        }
        mView.showNeedPayAmount(trade.getWillPayMoney());
    }

    @Override
    public void onDestroy() {
        stopCallBackBatch();
        EventBus.getDefault().unregister(this);
        if (mGenerateBatchSubscription != null) {
            mGenerateBatchSubscription.unsubscribe();
        }
        if (mPosTradeNoSubscription != null) {
            mPosTradeNoSubscription.unsubscribe();
        }
    }

    @Override
    public void onVerifySelect() {
        UiNavigation.gotoVerificationActivity(mContext);
    }

    @Override
    public void onPayClick() {
        if (AppConstants.INNER_METHOD_SOURCE_NORMAL.equals(mView.returnSource())) {
            int count = InnerManager.getInstance().getSelectCount();
            if (count > 1) {
                new CustomAlertDialogBuilder(mContext)
                        .setMessage("本次消费有" + count + "笔订单，确认合并进行支付吗?")
                        .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                showMethod();
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
            } else {
                showMethod();
            }
        } else {
            showMethod();
        }
    }

    @Override
    public void onSelectChange() {
        if (mHoleSelect) {
            // 取消
            InnerManager.getInstance().unselectedOrderInfos();
        } else {
            // 全选
            InnerManager.getInstance().selectedOrderInfos();
        }
        mHoleSelect = !mHoleSelect;
        mView.updateStatus(mHoleSelect);
        mView.updateAll();
        mView.showSelectCount(InnerManager.getInstance().getSelectCount());
        // 订单金额|核销金额
        mTradeManager.getCurrentTrade().setOriginMoney(InnerManager.getInstance().getOrderAmount());
        mTradeManager.setDiscountOriginAmount();
        updateAmount();
    }

    @Override
    public void onOrderClick(InnerOrderInfo info, int position) {
        info.selected = !info.selected;

        int select = InnerManager.getInstance().getSelectCount();
        int total = InnerManager.getInstance().getInnerOrderInfos().size();

        mView.updateItem(position);
        mView.showSelectCount(select);

        if (select == 0 || select < total) {
            mHoleSelect = false;
            mView.updateStatus(mHoleSelect);
        } else if (select == total) {
            mHoleSelect = true;
            mView.updateStatus(mHoleSelect);
        } else {
            // do nothing
        }

        // 订单金额|核销金额
        mTradeManager.getCurrentTrade().setOriginMoney(InnerManager.getInstance().getOrderAmount());
        mTradeManager.setDiscountOriginAmount();
        updateAmount();
    }

    @Override
    public void processData() {
        mView.showStepView();       //显示StepView
        mTradeManager.newTrade();   //初始化交易
        switch (mView.returnSource()) {
            case AppConstants.INNER_METHOD_SOURCE_NORMAL:   //如果是正常查找
                mView.showOrderList(InnerManager.getInstance().getInnerOrderInfos());   //显示列表
                int origin = InnerManager.getInstance().getOrderAmount();
                mTradeManager.getCurrentTrade().setOriginMoney(origin);     //设置订单金额
                mView.setStatusLayout(true);            //显示其他项
                mView.updateStatus(mHoleSelect);
                mView.showSelectCount(InnerManager.getInstance().getSelectCount());
                break;
            case AppConstants.INNER_METHOD_SOURCE_RECORD:
            case AppConstants.INNER_METHOD_SOURCE_PUSH:     //如果是推送
                InnerRecordInfo recordInfo = mView.returnRecordInfo();
                mTradeManager.getCurrentTrade().payOrderId = recordInfo.payId;
                mTradeManager.getCurrentTrade().batchNo = recordInfo.batchNo;
                mTradeManager.getCurrentTrade().setOriginMoney(recordInfo.payAmount);   //设置订单金额
                mView.showOrderList(recordInfo.details);    //显示列表
                mView.setStatusLayout(false);               //隐藏设置项
                break;
            default:
                break;
        }
        updateAmount();     //更新页面金额
    }

    private void showMethod() {
        ActionSheetDialog dialog = new ActionSheetDialog(mContext);
        dialog.setContents(new ArrayList<>(InnerManager.getInstance().getChannels().keySet()));
        dialog.setCancelText("取消");
        dialog.setEventListener(new ActionSheetDialog.OnEventListener() {
            @Override
            public void onActionItemClick(ActionSheetDialog dialog, String item, int position) {
                String innerType = InnerManager.getInstance().getChannels().get(item);

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
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    private void doCashier() {
        mView.showLoading();
        if (mGenerateBatchSubscription != null) {
            mGenerateBatchSubscription.unsubscribe();
        }
        final Trade trade = mTradeManager.getCurrentTrade();
        mGenerateBatchSubscription = mTradeManager.generateInnerBatch(
                trade.batchNo,
                trade.memberId,
                trade.currentCashierType,
                InnerManager.getInstance().getOrderIds(),
                mTradeManager.formatVerifyCodes(trade.getCouponList()),
                new Callback<InnerBatchResult>() {
                    @Override
                    public void onSuccess(InnerBatchResult o) {
                        mView.hideLoading();
                        InnerBatchInfo batchInfo = o.getRespData();
                        trade.tradeStatus = AppConstants.TRADE_STATUS_SUCCESS;
                        trade.batchNo = batchInfo.batchNo;
                        trade.payOrderId = batchInfo.payOrderId;
                        trade.payUrl = batchInfo.payUrl;
                        trade.setOriginMoney(batchInfo.oriAmount);
                        trade.setWillDiscountMoney(batchInfo.discountAmount);
                        trade.setWillPayMoney(batchInfo.payAmount);
                        trade.tradeTime = DateUtils.getCurrentDate();
                        if (AppConstants.APP_REQUEST_YES.equals(batchInfo.status)) {
                            //核销金额已完成抵扣
                            UiNavigation.gotoInnerResultActivity(mContext);
                            mView.showEnterAnim();
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
                                    posCashier(trade.getWillPayMoney());
                                    break;
                                default:
                                    break;
                            }
                        }
                    }

                    @Override
                    public void onError(String error) {
                        mView.hideLoading();
                        // FIXME 如果后台描述修改,需要相应变更
                        if (error.contains("订单已被支付锁定")) {
                            new CustomAlertDialogBuilder(mContext)
                                    .setMessage("支付订单已存在，请前往结账提醒列表完成支付")
                                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(final DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    })
                                    .setPositiveButton("去支付", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
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

    private void posCashier(int money) {
        mTradeManager.posPay(mContext, money, new Callback<Void>() {
            @Override
            public void onSuccess(Void o) {
                mView.showToast("支付成功！");
                reportTrade();
                startCallBackBatch();
            }

            @Override
            public void onError(String error) {
                mView.hideLoading();
                if (CashierManager.getInstance().isUserCancel(mTradeManager.getCurrentTrade().posPayReturn)) {
                    mView.showToast("支付失败：已取消支付");
                } else {
                    mView.showToast("支付失败：" + error);
                }
                mTradeManager.getCurrentTrade().tradeStatus = AppConstants.TRADE_STATUS_CANCEL;
                UiNavigation.gotoInnerResultActivity(mContext);
                mView.showEnterAnim();
                mView.finishSelf();
            }
        });
    }

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
        callbackBatchCall = XmdNetwork.getInstance().getService(SpaService.class)
                .callbackInnerBatchOrderSync(AccountManager.getInstance().getToken(),
                        null,
                        mTradeManager.getCurrentTrade().currentCashierType,
                        mTradeManager.getCurrentTrade().payOrderId,
                        mTradeManager.getCurrentTrade().tradeNo);
        XmdNetwork.getInstance().requestSync(callbackBatchCall, new NetworkSubscriber<BaseBean>() {
            @Override
            public void onCallbackSuccess(BaseBean result) {
                resultCallBackBatch = true;
                mView.hideLoading();
                mTradeManager.getCurrentTrade().tradeStatus = AppConstants.TRADE_STATUS_SUCCESS;
                UiNavigation.gotoInnerResultActivity(mContext);
                mView.showEnterAnim();
                mView.finishSelf();
            }

            @Override
            public void onCallbackError(Throwable e) {
                XLogger.e("callbackBatchCall" + e.getLocalizedMessage());
                resultCallBackBatch = false;
            }
        });
        return resultCallBackBatch;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(MemberInfo info) {
        // 会员支付流程
        doCashier();
    }

    // POS渠道支付时需要汇报
    private void reportTrade() {
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
