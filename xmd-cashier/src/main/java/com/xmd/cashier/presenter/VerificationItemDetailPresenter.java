package com.xmd.cashier.presenter;

import android.content.Context;

import com.xmd.cashier.contract.VerificationItemDetailContract;

/**
 * Created by heyangya on 16-8-24.
 */

public class VerificationItemDetailPresenter implements VerificationItemDetailContract.Presenter {
    private Context mContext;
    private VerificationItemDetailContract.View mView;

    public VerificationItemDetailPresenter(Context context, VerificationItemDetailContract.View view) {
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

    }
}
