package com.xmd.cashier.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.xmd.cashier.R;
import com.xmd.cashier.contract.MemberPayContract;
import com.xmd.cashier.presenter.MemberPayPresenter;

public class MemberPayActivity extends BaseActivity implements MemberPayContract.View {
    private MemberPayContract.Presenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_pay);

        showToolbar(R.id.toolbar, R.string.member_pay_title);

        mPresenter = new MemberPayPresenter(this, this);

        mPresenter.onCreate();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mPresenter.onStart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mPresenter.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onKeyEventBack() {
        mPresenter.onNavigationBack();
        return true;
    }

    public void onClickOk(View view) {
        mPresenter.onClickOk();
    }

    public void onClickOtherPay(View view) {
        mPresenter.onClickOtherPay();
    }

    @Override
    public void setBalance(String balance) {
        ((TextView) findViewById(R.id.tv_balance)).setText(balance);
    }

    @Override
    public void setCardNumber(String cardNumber) {
        ((TextView) findViewById(R.id.tv_card)).setText(cardNumber);
    }

    @Override
    public void setPhone(String phone) {
        ((TextView) findViewById(R.id.tv_phone)).setText(phone);
    }

    @Override
    public void setInfo(String info) {
        ((TextView) findViewById(R.id.tv_info)).setText(info);
    }

    @Override
    public void setDiscount(String discount) {
        ((TextView) findViewById(R.id.tv_discount)).setText(discount);
    }

    @Override
    public void setOriginPayMoney(String money) {
        ((TextView) findViewById(R.id.tv_origin_pay)).setText(money);
    }

    @Override
    public void setNeedPayMoneyEnough(String money) {
        findViewById(R.id.layout_enough).setVisibility(View.VISIBLE);
        ((TextView) findViewById(R.id.tv_need_pay)).setText(money);
    }

    @Override
    public void setNeedpayMoneyNotEnough(String money) {
        findViewById(R.id.layout_not_enough).setVisibility(View.VISIBLE);
        ((TextView) findViewById(R.id.tv_need_pay2)).setText(money);
    }


    @Override
    public void setPresenter(MemberPayContract.Presenter presenter) {

    }

    @Override
    public void finishSelf() {
        finish();
    }
}
