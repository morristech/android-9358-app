package com.xmd.cashier.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;

import com.xmd.cashier.R;
import com.xmd.cashier.common.AppConstants;
import com.xmd.cashier.contract.VerifyServiceContract;
import com.xmd.cashier.dal.bean.CouponInfo;
import com.xmd.cashier.presenter.VerifyServicePresenter;

/**
 * Created by zr on 16-12-12.
 * 核销项目券
 */

public class VerifyServiceActivity extends BaseActivity implements VerifyServiceContract.View {
    private VerifyServiceContract.Presenter mPresenter;

    // 核销码
    private TextView mCouponNo;
    // 活动类型
    private TextView mActType;
    // 活动名称
    private TextView mActSubTitle;
    // 卡券名称
    private TextView mActTitle;
    // 卡券类型
    private TextView mUseType;
    // 卡券状态
    private TextView mActStatus;
    // 活动日期
    private TextView mActPeriod;
    // 券有效期
    private TextView mCouponPeriod;
    // 使用时段
    private TextView mUseTimePeriod;
    // 领取时间
    private TextView mCouponGetDate;
    // 活动说明
    private WebView mActContent;
    private TextView mActContentNull;

    private String mCode;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_service);
        mPresenter = new VerifyServicePresenter(this, this);
        CouponInfo info = getIntent().getParcelableExtra(AppConstants.EXTRA_SERVICE_COUPON_INFO);
        if (info == null) {
            showToast("无效的核销信息");
            finish();
            return;
        }

        initView();
        showCoupon(info);

        mPresenter.onCreate();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mPresenter != null) {
            mPresenter.onStart();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPresenter != null) {
            mPresenter.onDestroy();
        }
    }

    public void onClickVerify(View view) {
        mPresenter.onClickVerify();
    }

    private void initView() {
        showToolbar(R.id.toolbar, R.string.verify_service_title);
        mCouponNo = (TextView) findViewById(R.id.tv_coupon_no);
        mActType = (TextView) findViewById(R.id.tv_act_type_name);
        mActSubTitle = (TextView) findViewById(R.id.tv_act_sub_title);
        mActTitle = (TextView) findViewById(R.id.tv_act_title);
        mUseType = (TextView) findViewById(R.id.tv_use_type);
        mActStatus = (TextView) findViewById(R.id.tv_act_status);
        mActPeriod = (TextView) findViewById(R.id.tv_act_time_period);
        mCouponPeriod = (TextView) findViewById(R.id.tv_coupon_period);
        mUseTimePeriod = (TextView) findViewById(R.id.tv_use_time_period);
        mActContent = (WebView) findViewById(R.id.wb_act_content);
        mActContentNull = (TextView) findViewById(R.id.tv_act_content_null);
        mCouponGetDate = (TextView) findViewById(R.id.tv_coupon_get_date);
    }

    private void showCoupon(CouponInfo info) {
        setCode(info.couponNo);
        mCouponNo.setText(info.couponNo);
        mActType.setText(info.useTypeName);
        mActSubTitle.setText(info.actSubTitle);
        mActTitle.setText(info.actTitle);
        mUseType.setText(info.couponTypeName);
        mActStatus.setText(info.isTimeValid() ? "可用" : "不可用");
        mActPeriod.setText(info.couponNo);
        mCouponPeriod.setText(info.couponPeriod);
        mUseTimePeriod.setText(info.useTimePeriod);
        mCouponGetDate.setText(info.getDate);

        if (!TextUtils.isEmpty(info.actContent)) {
            mActContent.setVisibility(View.VISIBLE);
            mActContent.loadDataWithBaseURL(null, info.actContent, "text/html", "utf-8", null);
        } else {
            mActContent.setVisibility(View.GONE);
            mActContentNull.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void showLoadingView() {
        showLoading();
    }

    @Override
    public void hideLoadingView() {
        hideLoading();
    }

    private void setCode(String code) {
        mCode = code;
    }

    @Override
    public String getCode() {
        return mCode;
    }

    @Override
    public void setPresenter(VerifyServiceContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void finishSelf() {
        setResult(RESULT_OK);
        finish();
    }
}

