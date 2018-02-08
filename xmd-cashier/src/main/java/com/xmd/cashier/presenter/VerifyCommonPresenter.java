package com.xmd.cashier.presenter;

import android.content.Context;

import com.xmd.cashier.R;
import com.xmd.cashier.common.AppConstants;
import com.xmd.cashier.common.Utils;
import com.xmd.cashier.contract.VerifyCommonContract;
import com.xmd.cashier.dal.bean.CommonVerifyInfo;
import com.xmd.cashier.dal.bean.TreatInfo;
import com.xmd.cashier.dal.sp.SPManager;
import com.xmd.cashier.manager.Callback;
import com.xmd.cashier.manager.VerifyManager;
import com.xmd.m.network.BaseBean;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by zr on 16-12-12.
 */

public class VerifyCommonPresenter implements VerifyCommonContract.Presenter {
    private Context mContext;
    private VerifyCommonContract.View mView;
    private Subscription mVerifyCommonSubscription;

    public VerifyCommonPresenter(Context context, VerifyCommonContract.View view) {
        mContext = context;
        mView = view;
        mView.setPresenter(this);
    }

    @Override
    public void onClickVerify(final CommonVerifyInfo info) {
        if (mView.needAmount() && !Utils.matchAmountNumFormat(mView.getAmount())) {
            mView.showError("请输入正确的金额");
            return;
        }
        if (!Utils.isNetworkEnabled(mContext)) {
            mView.showError(mContext.getString(R.string.network_disabled));
            return;
        }
        if (mVerifyCommonSubscription != null) {
            mVerifyCommonSubscription.unsubscribe();
        }
        mView.showLoadingView();
        mVerifyCommonSubscription = VerifyManager.getInstance().verifyWithMoney(Utils.stringToMoney(mView.getAmount()), mView.getCode(), mView.getType(), new Callback<BaseBean>() {
            @Override
            public void onSuccess(BaseBean o) {
                mView.hideLoadingView();
                mView.showToast("操作成功");
                if (AppConstants.TYPE_PAY_FOR_OTHER.equals(info.type)) {
                    TreatInfo treatInfo = new TreatInfo();
                    treatInfo.userName = info.userName;
                    treatInfo.userPhone = info.userPhone;
                    treatInfo.amount = Integer.parseInt(info.info.amount);
                    treatInfo.useMoney = Utils.stringToMoney(mView.getAmount());
                    treatInfo.authorizeCode = info.code;
                    treatInfo.setExtraMemberInfo(info.info.extra);
                    printNormal(treatInfo);
                    mView.finishSelf();
                } else {
                    mView.finishSelf();
                }
            }

            @Override
            public void onError(String error) {
                mView.hideLoadingView();
                mView.showError("操作失败：" + error);
            }
        });
    }

    private void printNormal(final TreatInfo info) {
        Observable
                .create(new Observable.OnSubscribe<Void>() {
                    @Override
                    public void call(Subscriber<? super Void> subscriber) {
                        VerifyManager.getInstance().printTreat(info, true);
                        if (SPManager.getInstance().getPrintClientSwitch()) {
                            VerifyManager.getInstance().printTreat(info, false);
                        }
                        subscriber.onNext(null);
                        subscriber.onCompleted();
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onStart() {

    }

    @Override
    public void onDestroy() {
        if (mVerifyCommonSubscription != null) {
            mVerifyCommonSubscription.unsubscribe();
        }
    }
}
