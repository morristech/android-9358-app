package com.xmd.cashier.presenter;

import android.content.Context;
import android.text.TextUtils;

import com.shidou.commonlibrary.helper.XLogger;
import com.xmd.cashier.common.AppConstants;
import com.xmd.cashier.contract.InnerTechedOrderContract;
import com.xmd.cashier.dal.bean.InnerOrderInfo;
import com.xmd.cashier.dal.net.RequestConstant;
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

public class InnerTechedOrderPresenter implements InnerTechedOrderContract.Presenter {
    private static final String TAG = "InnerTechedOrderPresenter";
    private Subscription mGetTechOrderListSubscription;

    private Context mContext;
    private InnerTechedOrderContract.View mView;

    public InnerTechedOrderPresenter(Context context, InnerTechedOrderContract.View view) {
        mContext = context;
        mView = view;
        mView.setPresenter(this);
    }

    @Override
    public void onCreate() {
        if (TextUtils.isEmpty(mView.returnEmpId())) {
            mView.showDesc("查询订单错误...");
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
        XLogger.i(TAG, AppConstants.LOG_BIZ_NATIVE_CASHIER + "根据技师ID获取订单：" + RequestConstant.URL_GET_INNER_ORDER_LIST + " (" + empId + ") ");
        Observable<InnerOrderListResult> observable = XmdNetwork.getInstance().getService(SpaService.class)
                .getInnerOrderList(AccountManager.getInstance().getToken(), null, empId, null);
        mGetTechOrderListSubscription = XmdNetwork.getInstance().request(observable, new NetworkSubscriber<InnerOrderListResult>() {
            @Override
            public void onCallbackSuccess(InnerOrderListResult result) {
                mView.hideLoading();
                if (result != null && result.getRespData() != null && !result.getRespData().isEmpty()) {
                    mView.hideDesc();
                    mView.showOrderData(result.getRespData());
                } else {
                    mView.showDesc("暂无订单...");
                }
            }

            @Override
            public void onCallbackError(Throwable e) {
                XLogger.e(TAG, AppConstants.LOG_BIZ_NATIVE_CASHIER + "根据技师ID获取订单---失败：" + e.getLocalizedMessage());
                mView.hideLoading();
                mView.showDesc(e.getLocalizedMessage());
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
