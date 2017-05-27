package com.xmd.cashier.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;

import com.xmd.cashier.R;
import com.xmd.cashier.common.AppConstants;
import com.xmd.cashier.contract.VerifyCouponContract;
import com.xmd.cashier.dal.bean.CouponInfo;
import com.xmd.cashier.presenter.VerifyCouponPresenter;

/**
 * Created by zr on 2017/4/16 0016.
 * 核销优惠券
 */

public class VerifyCouponActivity extends BaseActivity implements VerifyCouponContract.View {
    private VerifyCouponContract.Presenter mPresenter;
    private CouponInfo mInfo;

    private TextView mCouponTypeName;
    private TextView mCouponName;
    private TextView mCouponStatus;
    private TextView mCouponDescription;
    private TextView mCouponEnableTime;
    private TextView mCouponUseTime;

    private TextView mCouponNo;
    private Button mVerifyBtn;
    private WebView mActContentView;
    private TextView mActContentNull;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_coupon);
        mPresenter = new VerifyCouponPresenter(this, this);
        mInfo = getIntent().getParcelableExtra(AppConstants.EXTRA_NORMAL_COUPON_INFO);
        if (mInfo == null) {
            showToast("无效的核销信息");
            finish();
            return;
        }
        initView();
    }

    private void initView() {
        showToolbar(R.id.toolbar, "核销");
        mCouponNo = (TextView) findViewById(R.id.tv_coupon_no);
        mVerifyBtn = (Button) findViewById(R.id.btn_verify);
        mActContentView = (WebView) findViewById(R.id.wb_act_content);
        mActContentNull = (TextView) findViewById(R.id.tv_act_content_null);

        mCouponTypeName = (TextView) findViewById(R.id.tv_coupon_type_name);
        mCouponName = (TextView) findViewById(R.id.tv_coupon_name);
        mCouponStatus = (TextView) findViewById(R.id.tv_coupon_status);
        mCouponDescription = (TextView) findViewById(R.id.tv_coupon_description);
        mCouponEnableTime = (TextView) findViewById(R.id.tv_coupon_enable_time);
        mCouponUseTime = (TextView) findViewById(R.id.tv_coupon_use_time);

        mCouponNo.setText(mInfo.couponNo);
        mCouponTypeName.setText(mInfo.useTypeName);
        mCouponName.setText(mInfo.actTitle);
        mCouponDescription.setText(mInfo.consumeMoneyDescription);
        mCouponEnableTime.setText(mInfo.couponPeriod);
        mCouponUseTime.setText(mInfo.useTimePeriod);
        if (TextUtils.isEmpty(mInfo.actContent)) {
            mActContentNull.setVisibility(View.VISIBLE);
            mActContentView.setVisibility(View.GONE);
        } else {
            mActContentNull.setVisibility(View.GONE);
            mActContentView.setVisibility(View.VISIBLE);
            mActContentView.loadDataWithBaseURL(null, mInfo.actContent, "text/html", "utf-8", null);
        }
        if (mInfo.isTimeValid()) {
            mCouponStatus.setText("可用");
            mVerifyBtn.setEnabled(true);
        } else {
            mCouponStatus.setText("不可用");
            mVerifyBtn.setEnabled(false);
        }

        mVerifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.onVerify(mInfo);
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
    public void setPresenter(VerifyCouponContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void finishSelf() {
        setResult(RESULT_OK);
        finish();
    }
}