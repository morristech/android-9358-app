package com.xmd.cashier.presenter;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;

import com.google.zxing.WriterException;
import com.shidou.commonlibrary.helper.XLogger;
import com.xmd.cashier.cashier.PosFactory;
import com.xmd.cashier.common.Utils;
import com.xmd.cashier.contract.MemberScanContract;
import com.xmd.cashier.dal.bean.MemberRecordInfo;
import com.xmd.cashier.dal.event.RechargeFinishEvent;
import com.xmd.cashier.dal.net.response.MemberRecordResult;
import com.xmd.cashier.manager.Callback;
import com.xmd.cashier.manager.MemberManager;

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
                    PosFactory.getCurrentCashier().textToSound("充值成功");
                    EventBus.getDefault().post(new RechargeFinishEvent());
                    mView.showScanSuccess();
                    memberRecordInfo = o.getRespData();
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
        if (MemberManager.getInstance().getAmount() > 0) {
            // 直接充值金额
            mView.showScanInfo(MemberManager.getInstance().getDescription(), Utils.moneyToStringEx(MemberManager.getInstance().getAmount()), null);
        } else {
            // 充值套餐
            mView.showScanInfo(MemberManager.getInstance().getDescription(), Utils.moneyToStringEx(MemberManager.getInstance().getPackageAmount()), MemberManager.getInstance().getPackageName());
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
        Observable
                .create(new Observable.OnSubscribe<Void>() {
                    @Override
                    public void call(Subscriber<? super Void> subscriber) {
                        MemberManager.getInstance().printInfo(memberRecordInfo, true);
                        subscriber.onNext(null);
                        subscriber.onCompleted();
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();
    }
}
