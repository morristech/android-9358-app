package com.xmd.cashier.presenter;

import android.content.Context;

import com.xmd.cashier.common.Utils;
import com.xmd.cashier.contract.TechnicianContract;
import com.xmd.cashier.dal.bean.TechInfo;
import com.xmd.cashier.dal.net.SpaService;
import com.xmd.cashier.dal.net.response.TechListResult;
import com.xmd.cashier.manager.AccountManager;
import com.xmd.m.network.NetworkSubscriber;
import com.xmd.m.network.XmdNetwork;

import org.greenrobot.eventbus.EventBus;

import rx.Observable;
import rx.Subscription;

/**
 * Created by zr on 17-7-11.
 */

public class TechnicianPresenter implements TechnicianContract.Presenter {
    private Context mContext;
    private TechnicianContract.View mView;
    private boolean isRefreshing;

    private Subscription mGetTechListSubscription;

    public TechnicianPresenter(Context context, TechnicianContract.View view) {
        mContext = context;
        mView = view;
        mView.setPresenter(this);
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onStart() {

    }

    @Override
    public void onDestroy() {
        if (mGetTechListSubscription != null) {
            mGetTechListSubscription.unsubscribe();
        }
    }

    @Override
    public void load(boolean init) {
        if (isRefreshing) {
            return;
        }
        isRefreshing = true;
        if (!init) {
            mView.showRefreshIng();
        }
        if (!Utils.isNetworkEnabled(mContext)) {
            isRefreshing = false;
            mView.showRefreshNoNetwork();
            return;
        }
        if (mGetTechListSubscription != null) {
            mGetTechListSubscription.unsubscribe();
        }
        Observable<TechListResult> observable = XmdNetwork.getInstance().getService(SpaService.class)
                .getTechList(AccountManager.getInstance().getToken());
        mGetTechListSubscription = XmdNetwork.getInstance().request(observable, new NetworkSubscriber<TechListResult>() {
            @Override
            public void onCallbackSuccess(TechListResult result) {
                mView.clearData();
                if (result != null && result.getRespData() != null && !result.getRespData().isEmpty()) {
                    mView.showRefreshSuccess();
                    mView.showData(result.getRespData());
                } else {
                    mView.showRefreshEmpty();
                }
                isRefreshing = false;
            }

            @Override
            public void onCallbackError(Throwable e) {
                isRefreshing = false;
                mView.showRefreshError();
            }
        });
    }

    @Override
    public void onTechSelect(TechInfo info) {
        EventBus.getDefault().post(info);
        mView.finishSelf();
    }
}
