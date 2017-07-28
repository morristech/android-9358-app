package com.xmd.cashier.presenter;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Handler;

import com.google.zxing.WriterException;
import com.shidou.commonlibrary.helper.XLogger;
import com.xmd.cashier.cashier.PosFactory;
import com.xmd.cashier.common.AppConstants;
import com.xmd.cashier.common.Utils;
import com.xmd.cashier.contract.MemberScanContract;
import com.xmd.cashier.dal.bean.MemberRecordInfo;
import com.xmd.cashier.dal.bean.PackagePlanItem;
import com.xmd.cashier.dal.event.RechargeFinishEvent;
import com.xmd.cashier.dal.net.response.MemberRecordResult;
import com.xmd.cashier.manager.Callback;
import com.xmd.cashier.manager.MemberManager;
import com.xmd.cashier.widget.CustomAlertDialogBuilder;

import org.greenrobot.eventbus.EventBus;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by zr on 17-7-11.
 */

public class MemberScanPresenter implements MemberScanContract.Presenter {
    private static final int INTERVAL = 5 * 1000;
    private Context mContext;
    private MemberScanContract.View mView;
    private MemberRecordInfo memberRecordInfo;
    private boolean success = false;

    private Subscription mGetRechargeOrderDetailSubscription;
    private Handler mHandler;
    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            if (mGetRechargeOrderDetailSubscription != null) {
                mGetRechargeOrderDetailSubscription.unsubscribe();
            }
            mGetRechargeOrderDetailSubscription = MemberManager.getInstance().requestRechargeDetail(new Callback<MemberRecordResult>() {
                @Override
                public void onSuccess(MemberRecordResult o) {
                    success = true;
                    PosFactory.getCurrentCashier().textToSound("充值成功");
                    EventBus.getDefault().post(new RechargeFinishEvent());
                    mView.showScanSuccess();
                    memberRecordInfo = o.getRespData();
                    memberRecordInfo.packageInfo = MemberManager.getInstance().getPackageInfo();
                }

                @Override
                public void onError(String error) {
                    XLogger.d("查询失败：" + error);
                    mHandler.postDelayed(mRunnable, INTERVAL);
                }
            });
        }
    };

    public MemberScanPresenter(Context context, MemberScanContract.View view) {
        mContext = context;
        mView = view;
        mView.setPresenter(this);
        mHandler = new Handler();
    }

    @Override
    public void onCreate() {
        switch (MemberManager.getInstance().getAmountType()) {
            case AppConstants.MEMBER_RECHARGE_AMOUNT_TYPE_MONEY:
                String amount = Utils.moneyToStringEx(MemberManager.getInstance().getAmount());
                mView.showScanInfo("会员充值", "充值金额", amount + "元", amount);
                break;
            case AppConstants.MEMBER_RECHARGE_AMOUNT_TYPE_PACKAGE:
                PackagePlanItem info = MemberManager.getInstance().getPackageInfo();
                if (info != null) {
                    String packageAmount = Utils.moneyToStringEx(info.amount);
                    mView.showScanInfo("会员充值", "充值套餐", "套餐" + info.name, packageAmount);
                }
                break;
            case AppConstants.MEMBER_RECHARGE_AMOUNT_TYPE_NONE:
            default:
                XLogger.i("amount type exception");
                break;
        }

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
            mHandler.postDelayed(mRunnable, INTERVAL);
        }
    }

    @Override
    public void onStart() {

    }

    @Override
    public void onDestroy() {
        mHandler.removeCallbacks(mRunnable);
        if (mGetRechargeOrderDetailSubscription != null) {
            mGetRechargeOrderDetailSubscription.unsubscribe();
        }
    }

    @Override
    public void printResult() {
        mView.showLoading();
        MemberManager.getInstance().printInfo(memberRecordInfo, false, false, new Callback<MemberRecordInfo>() {
            @Override
            public void onSuccess(MemberRecordInfo o) {
                mView.hideLoading();
                new CustomAlertDialogBuilder(mContext)
                        .setMessage("是否打印商户存根?")
                        .setPositiveButton("打印", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                Observable
                                        .create(new Observable.OnSubscribe<Void>() {
                                            @Override
                                            public void call(Subscriber<? super Void> subscriber) {
                                                // 扫码充值
                                                MemberManager.getInstance().printInfo(memberRecordInfo, false, true, null);
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
