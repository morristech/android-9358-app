package com.xmd.cashier.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.xmd.cashier.R;
import com.xmd.cashier.common.AppConstants;
import com.xmd.cashier.contract.TradeMarkPayContract;
import com.xmd.cashier.presenter.TradeMarkPayPresenter;

/**
 * Created by zr on 17-8-16.
 * 现金支付
 */

public class TradeMarkPayActivity extends BaseActivity implements TradeMarkPayContract.View {
    private TradeMarkPayContract.Presenter mPresenter;

    private TextView mMarkAmount;
    private Button mMarkConfirm;
    private TextView mChannelName;
    private TextView mChannelDesc;

    private int mTradeType;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trade_mark_pay);
        mTradeType = getIntent().getIntExtra(AppConstants.EXTRA_TRADE_TYPE, 0);
        mPresenter = new TradeMarkPayPresenter(this, this);
        initView();
        mPresenter.onCreate();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        mTradeType = getIntent().getIntExtra(AppConstants.EXTRA_TRADE_TYPE, 0);
        mPresenter.onCreate();
    }

    private void initView() {
        showToolbar(R.id.toolbar, "支付");
        mMarkAmount = (TextView) findViewById(R.id.tv_mark_amount);
        mMarkConfirm = (Button) findViewById(R.id.btn_mark_confirm);
        mChannelName = (TextView) findViewById(R.id.tv_mark_channel_name);
        mChannelDesc = (TextView) findViewById(R.id.tv_mark_channel_desc);
        mMarkConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.onMarkPay();
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
    public void setPresenter(TradeMarkPayContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void finishSelf() {
        finish();
    }

    @Override
    public void showAmount(String amount) {
        mMarkAmount.setText(amount);
    }

    @Override
    public void showChannelName(String name) {
        mChannelName.setText(name);
    }

    @Override
    public void showChannelDesc(String desc) {
        if (!TextUtils.isEmpty(desc)) {
            mChannelDesc.setVisibility(View.VISIBLE);
            mChannelDesc.setText(desc);
        } else {
            mChannelDesc.setVisibility(View.GONE);
        }
    }

    @Override
    public int getType() {
        return mTradeType;
    }

    @Override
    public boolean onKeyEventBack() {
        mPresenter.onNavigationBack();
        return true;
    }
}
