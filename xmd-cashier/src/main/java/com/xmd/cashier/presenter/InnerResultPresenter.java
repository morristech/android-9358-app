package com.xmd.cashier.presenter;

import android.content.Context;
import android.content.DialogInterface;

import com.xmd.cashier.UiNavigation;
import com.xmd.cashier.cashier.PosFactory;
import com.xmd.cashier.common.AppConstants;
import com.xmd.cashier.contract.InnerResultContract;
import com.xmd.cashier.dal.bean.InnerRecordInfo;
import com.xmd.cashier.dal.event.InnerFinishEvent;
import com.xmd.cashier.dal.sp.SPManager;
import com.xmd.cashier.manager.Callback;
import com.xmd.cashier.manager.InnerManager;
import com.xmd.cashier.manager.TradeManager;
import com.xmd.cashier.widget.CustomAlertDialogBuilder;

import org.greenrobot.eventbus.EventBus;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by zr on 17-11-4.
 */

public class InnerResultPresenter implements InnerResultContract.Presenter {

    private Context mContext;
    private InnerResultContract.View mView;

    private Subscription mGetBatchOrderSubscription;
    private InnerRecordInfo mRecordInfo;

    public InnerResultPresenter(Context context, InnerResultContract.View view) {
        mContext = context;
        mView = view;
        mView.setPresenter(this);
    }

    @Override
    public void onCreate() {
        mView.showStepView();
        switch (TradeManager.getInstance().getCurrentTrade().tradeStatus) {
            case AppConstants.TRADE_STATUS_SUCCESS:
                PosFactory.getCurrentCashier().textToSound("支付完成");
                mView.showSuccess();
                break;
            case AppConstants.TRADE_STATUS_CANCEL:
            default:
                mView.showCancel();
                break;
        }
        getOrder();
    }

    private void getOrder() {
        if (mGetBatchOrderSubscription != null) {
            mGetBatchOrderSubscription.unsubscribe();
        }
        mGetBatchOrderSubscription = InnerManager.getInstance().getInnerHoleBatchSubscription(TradeManager.getInstance().getCurrentTrade().payOrderId, new Callback<InnerRecordInfo>() {
            @Override
            public void onSuccess(InnerRecordInfo o) {
                mRecordInfo = o;
            }

            @Override
            public void onError(String error) {
                mRecordInfo = null;
            }
        });
    }

    @Override
    public void onStart() {

    }

    @Override
    public void onDestroy() {
        if (mGetBatchOrderSubscription != null) {
            mGetBatchOrderSubscription.unsubscribe();
        }
    }

    @Override
    public void onDetail() {
        if (mRecordInfo == null) {
            mView.showLoading();
            if (mGetBatchOrderSubscription != null) {
                mGetBatchOrderSubscription.unsubscribe();
            }
            mGetBatchOrderSubscription = InnerManager.getInstance().getInnerHoleBatchSubscription(TradeManager.getInstance().getCurrentTrade().payOrderId, new Callback<InnerRecordInfo>() {
                @Override
                public void onSuccess(InnerRecordInfo o) {
                    mView.hideLoading();
                    mRecordInfo = o;
                    UiNavigation.gotoInnerDetailActivity(mContext, mRecordInfo);
                }

                @Override
                public void onError(String error) {
                    mView.hideLoading();
                    mView.showToast(error);
                    mRecordInfo = null;
                }
            });
        } else {
            UiNavigation.gotoInnerDetailActivity(mContext, mRecordInfo);
        }
    }

    @Override
    public void onPrint() {
        if (mRecordInfo == null) {
            mView.showLoading();
            if (mGetBatchOrderSubscription != null) {
                mGetBatchOrderSubscription.unsubscribe();
            }
            mGetBatchOrderSubscription = InnerManager.getInstance().getInnerHoleBatchSubscription(TradeManager.getInstance().getCurrentTrade().payOrderId, new Callback<InnerRecordInfo>() {
                @Override
                public void onSuccess(InnerRecordInfo o) {
                    mView.hideLoading();
                    mRecordInfo = o;
                    printNormal();
                    newInnerTrade();
                }

                @Override
                public void onError(String error) {
                    mView.hideLoading();
                    mView.showToast(error);
                    mRecordInfo = null;
                }
            });
        } else {
            printNormal();
            newInnerTrade();
        }
    }

    private void printNormal() {
        Observable
                .create(new Observable.OnSubscribe<Void>() {
                    @Override
                    public void call(Subscriber<? super Void> subscriber) {
                        InnerManager.getInstance().printInnerRecordInfo(mRecordInfo, false, true, null);
                        if (SPManager.getInstance().getPrintClientSwitch()) {
                            InnerManager.getInstance().printInnerRecordInfo(mRecordInfo, false, false, null);
                        }
                        subscriber.onNext(null);
                        subscriber.onCompleted();
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();

    }

    private void printStep() {
        mView.showLoading();
        InnerManager.getInstance().printInnerRecordInfo(mRecordInfo, false, true, new Callback() {
            @Override
            public void onSuccess(Object o) {
                mView.hideLoading();
                new CustomAlertDialogBuilder(mContext)
                        .setMessage("是否需要打印客户联小票?")
                        .setPositiveButton("打印", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                Observable
                                        .create(new Observable.OnSubscribe<Void>() {
                                            @Override
                                            public void call(Subscriber<? super Void> subscriber) {
                                                InnerManager.getInstance().printInnerRecordInfo(mRecordInfo, false, false, null);
                                                subscriber.onNext(null);
                                                subscriber.onCompleted();
                                            }
                                        })
                                        .subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe();
                                newInnerTrade();
                            }
                        })
                        .setNegativeButton("完成交易", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                newInnerTrade();
                            }
                        })
                        .create()
                        .show();
            }

            @Override
            public void onError(String error) {
                mView.hideLoading();
                mView.showToast("打印出现异常:" + error);
                newInnerTrade();
            }
        });
    }

    @Override
    public void onClose() {
        newInnerTrade();
    }

    @Override
    public void onEventBack() {
        newInnerTrade();
    }

    private void newInnerTrade() {
        TradeManager.getInstance().newTrade();
        InnerManager.getInstance().clearInnerOrderInfos();
        mView.finishSelf();
        EventBus.getDefault().post(new InnerFinishEvent());
    }
}
