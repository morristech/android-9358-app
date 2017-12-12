package com.xmd.cashier.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.xmd.cashier.R;
import com.xmd.cashier.UiNavigation;
import com.xmd.cashier.contract.AccountStatisticsSettingContract;
import com.xmd.cashier.dal.sp.SPManager;
import com.xmd.cashier.presenter.AccountStatisticsSettingPresenter;

/**
 * Created by zr on 17-9-26.
 */

public class AccountStatisticsSettingActivity extends BaseActivity implements AccountStatisticsSettingContract.View {
    private AccountStatisticsSettingContract.Presenter mPresenter;
    private TextView mTextStart;
    private TextView mTextEnd;
    private Button mBtnConfirm;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_statistics_setting);
        mPresenter = new AccountStatisticsSettingPresenter(this, this);
        initView();
        mPresenter.onCreate();
    }

    private void initView() {
        showToolbar(R.id.toolbar, "对账周期设置");
        mTextStart = (TextView) findViewById(R.id.tv_setting_start);
        mTextEnd = (TextView) findViewById(R.id.tv_setting_end);
        mBtnConfirm = (Button) findViewById(R.id.btn_setting_confirm);
        mTextStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.onStartPick(v);
            }
        });
        mTextEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.onEndPick(v);
            }
        });
        mBtnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.onConfirm();
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
    public void setPresenter(AccountStatisticsSettingContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void finishSelf() {
        finish();
    }

    @Override
    public void showStart(String startTime) {
        if (!TextUtils.isEmpty(startTime)) {
            mTextStart.setText(startTime);
        }
    }

    @Override
    public void showEnd(String endTime) {
        if (!TextUtils.isEmpty(endTime)) {
            mTextEnd.setText(endTime);
        }
    }

    @Override
    public boolean onKeyEventBack() {
        if (SPManager.getInstance().getFirstStatistic()) {
            UiNavigation.gotoAccountStatisticsActivity(this);
            SPManager.getInstance().setFirstStatistic(false);
        }
        return super.onKeyEventBack();
    }
}
