package com.xmd.cashier.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.xmd.cashier.R;
import com.xmd.cashier.common.AppConstants;
import com.xmd.cashier.contract.CashPayContract;
import com.xmd.cashier.presenter.CashPayPresenter;

/**
 * Created by zr on 17-8-16.
 * 现金支付
 */

public class CashPayActivity extends BaseActivity implements CashPayContract.View {
    private CashPayContract.Presenter mPresenter;
    private int mAmount;

    private TextView mCashAmount;
    private Button mCashConfirm;
    private TextView mCashSuccess;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cash_pay);
        mAmount = getIntent().getIntExtra(AppConstants.EXTRA_CASH_AMOUNT, 0);
        mPresenter = new CashPayPresenter(this, this);
        initView();
        mPresenter.onCreate();
    }

    private void initView() {
        showToolbar(R.id.toolbar, "现金支付");
        mCashAmount = (TextView) findViewById(R.id.tv_cash_amount);
        mCashConfirm = (Button) findViewById(R.id.btn_cash_confirm);
        mCashSuccess = (TextView) findViewById(R.id.tv_cash_success);
        mCashConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.onCashPay();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPresenter != null) {
            mPresenter.onDestroy();
        }
    }

    @Override
    public void setPresenter(CashPayContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void finishSelf() {
        finish();
    }

    @Override
    public void showAmount(String amount) {
        mCashAmount.setText(amount);
    }

    @Override
    public void enableCashBtn() {
        mCashConfirm.setVisibility(View.VISIBLE);
        mCashConfirm.setEnabled(true);
    }

    @Override
    public void disableCashBtn() {
        mCashConfirm.setVisibility(View.VISIBLE);
        mCashConfirm.setEnabled(false);
    }

    @Override
    public void showCashBtn() {
        mCashConfirm.setVisibility(View.VISIBLE);
        mCashConfirm.setEnabled(true);
        mCashSuccess.setVisibility(View.INVISIBLE);
    }

    @Override
    public void showCashSuccess() {
        mCashConfirm.setVisibility(View.INVISIBLE);
        mCashSuccess.setVisibility(View.VISIBLE);
    }

    @Override
    public int getAmount() {
        return mAmount;
    }

    @Override
    public boolean onKeyEventBack() {
        mPresenter.onNavigationBack();
        return super.onKeyEventBack();
    }
}
