package com.xmd.cashier.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xmd.cashier.R;
import com.xmd.cashier.common.AppConstants;
import com.xmd.cashier.common.Utils;
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
    private boolean isShow;

    private TextView mCouponTypeName;
    private TextView mCouponName;
    private TextView mCouponStatus;
    private TextView mCouponDescription;
    private TextView mCouponEnableTime;
    private TextView mCouponUseTime;
    private LinearLayout mCouponLimitLayout;
    private TextView mCouponLimitItem;

    private TextView mCouponNo;
    private Button mVerifyBtn;
    private WebView mActContentView;
    private TextView mActContentNull;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_coupon);
        mPresenter = new VerifyCouponPresenter(this, this);
        mInfo = getIntent().getParcelableExtra(AppConstants.EXTRA_COUPON_VERIFY_INFO);
        isShow = getIntent().getBooleanExtra(AppConstants.EXTRA_IS_SHOW, true);
        if (mInfo == null) {
            showToast("无效的核销信息");
            finish();
            return;
        }
        initView();
    }

    private void initView() {
        showToolbar(R.id.toolbar, mInfo.actTitle);
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
        mCouponLimitLayout = (LinearLayout) findViewById(R.id.ly_coupon_limit_item);
        mCouponLimitItem = (TextView) findViewById(R.id.tv_coupon_limit_item);

        mCouponNo.setText(mInfo.couponNo);
        mCouponTypeName.setText(mInfo.useTypeName);
        mCouponName.setText(mInfo.actTitle);
        mCouponDescription.setText(mInfo.consumeMoneyDescription);
        mCouponEnableTime.setText(mInfo.couponPeriod);
        mCouponUseTime.setText(Utils.getTimePeriodDes(mInfo.useTimePeriod));
        if (TextUtils.isEmpty(mInfo.actContent)) {
            mActContentNull.setVisibility(View.VISIBLE);
            mActContentView.setVisibility(View.GONE);
        } else {
            mActContentNull.setVisibility(View.GONE);
            mActContentView.setVisibility(View.VISIBLE);
            mActContentView.loadDataWithBaseURL(null, mInfo.actContent, "text/html", "utf-8", null);
        }

        if (isShow) {
            mVerifyBtn.setVisibility(View.VISIBLE);
            mVerifyBtn.setEnabled(mInfo.valid);
        } else {
            mVerifyBtn.setVisibility(View.GONE);
        }
        mCouponStatus.setText(mInfo.valid ? "可用" : "不可用");
        mCouponStatus.setTextColor(mInfo.valid ? getResources().getColor(R.color.colorText4) : getResources().getColor(R.color.colorPink));

        // 限定项目
        if (mInfo.itemNames != null && !mInfo.itemNames.isEmpty()) {
            StringBuilder itemsBuild = new StringBuilder();
            for (String item : mInfo.itemNames) {
                itemsBuild.append(item).append(",");
            }
            itemsBuild.setLength(itemsBuild.length() - 1);
            mCouponLimitLayout.setVisibility(View.VISIBLE);
            mCouponLimitItem.setText(itemsBuild.toString());
        } else {
            mCouponLimitLayout.setVisibility(View.GONE);
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
