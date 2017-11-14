package com.xmd.cashier.presenter;

import android.content.Context;

import com.xmd.cashier.contract.InnerDetailContract;
import com.xmd.cashier.dal.bean.InnerRecordInfo;

/**
 * Created by zr on 17-11-7.
 */

public class InnerDetailPresenter implements InnerDetailContract.Presenter {
    private Context mContext;
    private InnerDetailContract.View mView;

    public InnerDetailPresenter(Context context, InnerDetailContract.View view) {
        mContext = context;
        mView = view;
        mView.setPresenter(this);
    }

    @Override
    public void onCreate() {
        InnerRecordInfo info = mView.returnRecordInfo();
        mView.showAmount(info);
        if (info != null && info.details != null && !info.details.isEmpty()) {
            mView.showRecordDetail(info.details);
        } else {
            mView.showToast("未查询到相应详情");
            return;
        }
    }

    @Override
    public void onStart() {

    }

    @Override
    public void onDestroy() {

    }

    @Override
    public void onDetailNegative() {
        mView.finishSelf();
    }

    @Override
    public void onDetailPositive() {
        mView.finishSelf();
    }
}
