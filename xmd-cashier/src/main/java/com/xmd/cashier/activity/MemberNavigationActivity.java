package com.xmd.cashier.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.RelativeLayout;

import com.xmd.cashier.R;
import com.xmd.cashier.contract.MemberNavigationContract;
import com.xmd.cashier.presenter.MemberNavigationPresenter;

/**
 * Created by zr on 17-7-10.
 */

public class MemberNavigationActivity extends BaseActivity implements MemberNavigationContract.View {
    private MemberNavigationContract.Presenter mPresenter;
    private RelativeLayout mRechargeLayout;
    private RelativeLayout mCardLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_navigation);
        mPresenter = new MemberNavigationPresenter(this, this);
        initView();
        mPresenter.onStart();
    }

    private void initView() {
        showToolbar(R.id.toolbar, "会员卡");
        mRechargeLayout = (RelativeLayout) findViewById(R.id.layout_member_recharge);
        mCardLayout = (RelativeLayout) findViewById(R.id.layout_member_card);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPresenter != null) {
            mPresenter.onDestroy();
        }
    }

    @Override
    public void setPresenter(MemberNavigationContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void finishSelf() {
        finish();
    }

    public void onClickRecord(View view) {
        mPresenter.onRecord();
    }

    public void onClickPayment(View view) {
        mPresenter.onPayment();
    }

    public void onClickCard(View view) {
        mPresenter.onCard();
    }

    public void onClickRecharge(View view) {
        mPresenter.onRecharge();
    }

    @Override
    public void enterRecharge(boolean enter) {
        mRechargeLayout.setVisibility(enter ? View.VISIBLE : View.GONE);
    }

    @Override
    public void enterCard(boolean enter) {
        mCardLayout.setVisibility(enter ? View.VISIBLE : View.GONE);
    }

    @Override
    public void showEnterAnim() {
        overridePendingTransition(R.anim.activity_in_from_right, R.anim.activity_out_to_left);
    }
}
