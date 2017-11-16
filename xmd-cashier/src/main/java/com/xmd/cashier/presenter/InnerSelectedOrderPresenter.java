package com.xmd.cashier.presenter;

import android.content.Context;

import com.xmd.cashier.contract.InnerSelectedOrderContract;
import com.xmd.cashier.dal.bean.InnerOrderInfo;
import com.xmd.cashier.manager.InnerManager;

/**
 * Created by zr on 17-11-8.
 */

public class InnerSelectedOrderPresenter implements InnerSelectedOrderContract.Presenter {
    public Context mContext;
    public InnerSelectedOrderContract.View mView;

    public InnerSelectedOrderPresenter(Context context, InnerSelectedOrderContract.View view) {
        mContext = context;
        mView = view;
        mView.setPresenter(this);
    }

    @Override
    public void onCreate() {
        mView.showOrderData(InnerManager.getInstance().getInnerOrderInfos());
        updateCount();
    }

    private void updateCount() {
        int selectCount = InnerManager.getInstance().getInnerOrderInfos().size();
        if (selectCount > 0) {
            mView.showCountText(String.valueOf(selectCount));
        } else {
            mView.hideCountText();
        }
    }

    @Override
    public void onStart() {

    }

    @Override
    public void onDestroy() {

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
        updateCount();
    }
}
