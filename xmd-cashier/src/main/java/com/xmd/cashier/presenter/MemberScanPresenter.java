package com.xmd.cashier.presenter;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.text.TextUtils;

import com.google.zxing.WriterException;
import com.shidou.commonlibrary.helper.RetryPool;
import com.shidou.commonlibrary.helper.XLogger;
import com.xmd.cashier.cashier.PosFactory;
import com.xmd.cashier.common.AppConstants;
import com.xmd.cashier.common.Utils;
import com.xmd.cashier.contract.MemberScanContract;
import com.xmd.cashier.dal.bean.MemberRecordInfo;
import com.xmd.cashier.dal.bean.PackagePlanItem;
import com.xmd.cashier.dal.event.RechargeFinishEvent;
import com.xmd.cashier.dal.net.RequestConstant;
import com.xmd.cashier.dal.net.SpaService;
import com.xmd.cashier.dal.net.response.MemberRecordResult;
import com.xmd.cashier.dal.sp.SPManager;
import com.xmd.cashier.manager.AccountManager;
import com.xmd.cashier.manager.Callback;
import com.xmd.cashier.manager.MemberManager;
import com.xmd.cashier.widget.CustomAlertDialogBuilder;
import com.xmd.m.network.NetworkSubscriber;
import com.xmd.m.network.XmdNetwork;

import org.greenrobot.eventbus.EventBus;

import retrofit2.Call;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by zr on 17-7-11.
 */

public class MemberScanPresenter implements MemberScanContract.Presenter {
    private Context mContext;
    private MemberScanContract.View mView;
    private MemberRecordInfo memberRecordInfo;
    private boolean success = false;

    private Subscription mRechargeByCashSubscription;

    private Call<MemberRecordResult> callDetailRecharge;
    private RetryPool.RetryRunnable mRetryDetailRecharge;
    private boolean resultDetailRecharge = false;

    public void startDetailRecharge() {
        mRetryDetailRecharge = new RetryPool.RetryRunnable(5000, 1.0f, new RetryPool.RetryExecutor() {
            @Override
            public boolean run() {
                return detailRechargeTrade();
            }
        });
        RetryPool.getInstance().postWork(mRetryDetailRecharge);
    }

    public void stopDetailRecharge() {
        if (callDetailRecharge != null && !callDetailRecharge.isCanceled()) {
            callDetailRecharge.cancel();
        }
        if (mRetryDetailRecharge != null) {
            RetryPool.getInstance().removeWork(mRetryDetailRecharge);
            mRetryDetailRecharge = null;
        }
    }

    private boolean detailRechargeTrade() {
        callDetailRecharge = XmdNetwork.getInstance().getService(SpaService.class)
                .detailMemberRecharge(AccountManager.getInstance().getToken(), MemberManager.getInstance().getRechargeOrderId());
        XmdNetwork.getInstance().requestSync(callDetailRecharge, new NetworkSubscriber<MemberRecordResult>() {
            @Override
            public void onCallbackSuccess(MemberRecordResult result) {
                success = true;
                PosFactory.getCurrentCashier().textToSound("充值成功");
                memberRecordInfo = result.getRespData();
                resultDetailRecharge = true;
                EventBus.getDefault().post(new RechargeFinishEvent());
            }

            @Override
            public void onCallbackError(Throwable e) {
                XLogger.d("查询失败：" + e.getLocalizedMessage());
                resultDetailRecharge = false;
            }
        });
        return resultDetailRecharge;
    }

    public MemberScanPresenter(Context context, MemberScanContract.View view) {
        mContext = context;
        mView = view;
        mView.setPresenter(this);
    }

    @Override
    public void onCreate() {
        if (!showRechargeInfo()) {
            mView.finishSelf();
            return;
        }

        if (TextUtils.isEmpty(mView.getCashierMethod())) {
            mView.showToast("充值支付方式未知，请重试...");
            return;
        }

        switch (mView.getCashierMethod()) {
            case AppConstants.MEMBER_CASHIER_METHOD_CASH:
                // 现金
                mView.showCash();
                break;
            case AppConstants.MEMBER_CASHIER_METHOD_SCAN:
                // 扫码
                mView.showScan();
                showBitMap();
                break;
            default:
                break;
        }
    }

    private void showBitMap() {
        Bitmap bitmap;
        try {
            bitmap = Utils.getQRBitmap(MemberManager.getInstance().getRechargeUrl());
        } catch (WriterException e) {
            bitmap = null;
        }
        if (bitmap == null) {
            mView.showToast("二维码解析异常");
        } else {
            mView.showQrcode(bitmap);
            startDetailRecharge();
        }
    }

    private boolean showRechargeInfo() {
        // 显示充值内容
        switch (MemberManager.getInstance().getAmountType()) {
            case AppConstants.MEMBER_RECHARGE_AMOUNT_TYPE_MONEY:    // 充值金额
                String amount = Utils.moneyToStringEx(MemberManager.getInstance().getAmount());
                if (MemberManager.getInstance().getAmountGive() > 0) {
                    String giveAmount = Utils.moneyToStringEx(MemberManager.getInstance().getAmountGive());
                    mView.showScanInfo("会员充值", "充值金额", "充" + amount + "送" + giveAmount, amount);
                } else {
                    mView.showScanInfo("会员充值", "充值金额", amount + "元", amount);
                }
                return true;
            case AppConstants.MEMBER_RECHARGE_AMOUNT_TYPE_PACKAGE:  // 充值套餐
                PackagePlanItem info = MemberManager.getInstance().getPackageInfo();
                if (info != null) {
                    String packageAmount = Utils.moneyToStringEx(info.amount);
                    mView.showScanInfo("会员充值", "充值套餐", "套餐" + info.name, packageAmount);
                }
                return true;
            case AppConstants.MEMBER_RECHARGE_AMOUNT_TYPE_NONE:
            default:
                mView.showToast("充值支付数据异常，请重试...");
                return false;
        }
    }

    @Override
    public void onStart() {

    }

    @Override
    public void onDestroy() {
        stopDetailRecharge();
        if (mRechargeByCashSubscription != null) {
            mRechargeByCashSubscription.unsubscribe();
            mRechargeByCashSubscription = null;
        }
    }

    @Override
    public void rechargeByCash() {
        mView.showLoading();
        mView.disableCash();
        if (mRechargeByCashSubscription != null) {
            mRechargeByCashSubscription.unsubscribe();
        }
        Observable<MemberRecordResult> reportCash = XmdNetwork.getInstance().getService(SpaService.class)
                .doMemberRecharge(AccountManager.getInstance().getToken(), MemberManager.getInstance().getRechargeOrderId(), AppConstants.PAY_CHANNEL_CASH, null, RequestConstant.DEFAULT_SIGN_VALUE);
        mRechargeByCashSubscription = XmdNetwork.getInstance().request(reportCash, new NetworkSubscriber<MemberRecordResult>() {
            @Override
            public void onCallbackSuccess(MemberRecordResult result) {
                mView.enableCash();
                mView.hideLoading();
                mView.showSuccess();
                success = true;
                PosFactory.getCurrentCashier().textToSound("充值成功");
                memberRecordInfo = result.getRespData();
                EventBus.getDefault().post(new RechargeFinishEvent());
            }

            @Override
            public void onCallbackError(Throwable e) {
                mView.hideLoading();
                mView.enableCash();
                mView.showToast("交易处理失败:" + e.getLocalizedMessage());
            }
        });
    }

    @Override
    public void printStep() {
        mView.showLoading();
        MemberManager.getInstance().printMemberRecordInfo(memberRecordInfo, false, true, new Callback() {
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
                                                // 扫码充值
                                                MemberManager.getInstance().printMemberRecordInfo(memberRecordInfo, false, false, null);
                                                subscriber.onNext(null);
                                                subscriber.onCompleted();
                                            }
                                        })
                                        .subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe();
                                MemberManager.getInstance().newTrade();
                                MemberManager.getInstance().newRechargeProcess();
                                mView.finishSelf();
                            }
                        })
                        .setNegativeButton("完成交易", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                MemberManager.getInstance().newTrade();
                                MemberManager.getInstance().newRechargeProcess();
                                mView.finishSelf();
                            }
                        })
                        .create()
                        .show();
            }

            @Override
            public void onError(String error) {
                mView.hideLoading();
                mView.showToast("打印异常:" + error);
                MemberManager.getInstance().newTrade();
                MemberManager.getInstance().newRechargeProcess();
                mView.finishSelf();
            }
        });
    }

    @Override
    public void printNormal() {
        Observable
                .create(new Observable.OnSubscribe<Void>() {
                    @Override
                    public void call(Subscriber<? super Void> subscriber) {
                        // 扫码充值
                        MemberManager.getInstance().printMemberRecordInfo(memberRecordInfo, false, true, null);
                        if (SPManager.getInstance().getPrintClientSwitch()) {
                            MemberManager.getInstance().printMemberRecordInfo(memberRecordInfo, false, false, null);
                        }
                        subscriber.onNext(null);
                        subscriber.onCompleted();
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();
        MemberManager.getInstance().newTrade();
        MemberManager.getInstance().newRechargeProcess();
        mView.finishSelf();
    }

    @Override
    public void onKeyEventBack() {
        if (success) {
            MemberManager.getInstance().newTrade();
            MemberManager.getInstance().newRechargeProcess();
            mView.finishSelf();
        } else {
            mView.finishSelf();
        }
    }
}
