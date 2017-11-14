package com.xmd.cashier.presenter;

import android.content.Context;
import android.text.TextUtils;

import com.xmd.cashier.contract.InnerOrderTechContract;
import com.xmd.cashier.dal.bean.InnerOrderInfo;
import com.xmd.cashier.dal.net.SpaService;
import com.xmd.cashier.dal.net.response.InnerOrderListResult;
import com.xmd.cashier.manager.AccountManager;
import com.xmd.cashier.manager.InnerManager;
import com.xmd.m.network.NetworkSubscriber;
import com.xmd.m.network.XmdNetwork;

import rx.Observable;
import rx.Subscription;

/**
 * Created by zr on 17-11-8.
 */

public class InnerOrderTechPresenter implements InnerOrderTechContract.Presenter {
    private Subscription mGetTechOrderListSubscription;

    private Context mContext;
    private InnerOrderTechContract.View mView;

    public InnerOrderTechPresenter(Context context, InnerOrderTechContract.View view) {
        mContext = context;
        mView = view;
        mView.setPresenter(this);
    }

    @Override
    public void onCreate() {
        if (TextUtils.isEmpty(mView.returnEmpId())) {
            mView.showToast("查询订单异常...");
            mView.finishSelf();
            return;
        } else {
            getOrderList(mView.returnEmpId());
        }
    }

    private void getOrderList(String empId) {
        mView.showLoading();
        if (mGetTechOrderListSubscription != null) {
            mGetTechOrderListSubscription.unsubscribe();
        }
        Observable<InnerOrderListResult> observable = XmdNetwork.getInstance().getService(SpaService.class)
                .getInnerOrderList(AccountManager.getInstance().getToken(), null, empId, null);
        mGetTechOrderListSubscription = XmdNetwork.getInstance().request(observable, new NetworkSubscriber<InnerOrderListResult>() {
            @Override
            public void onCallbackSuccess(InnerOrderListResult result) {
                mView.hideLoading();
                if (result != null && result.getRespData() != null && !result.getRespData().isEmpty()) {
                    mView.showOrderData(result.getRespData());
                } else {
                    mView.showToast("暂无有效订单...");
                    mView.finishSelf();
                }
            }

            @Override
            public void onCallbackError(Throwable e) {
                mView.hideLoading();
                mView.showToast(e.getLocalizedMessage());
            }
        });
    }

    @Override
    public void onStart() {

    }

    @Override
    public void onDestroy() {
        if (mGetTechOrderListSubscription != null) {
            mGetTechOrderListSubscription.unsubscribe();
        }
    }

    @Override
    public void onNegative() {
        mView.finishSelf();
    }

    @Override
    public void onPositive() {
        mView.finishSelf();
    }

    @Override
    public void onItemSelect(InnerOrderInfo info, int position) {
        if (info.selected) {
            // 选中 -> 未选中
            InnerManager.getInstance().removeInnerOrderInfo(info);
        } else {
            // 未选中 -> 选中
            InnerManager.getInstance().addInnerOrderInfo(info);
        }
        info.selected = !info.selected;
        mView.updateItem(position);
    }
}
