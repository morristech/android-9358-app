package com.xmd.cashier.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;

import com.xmd.cashier.R;
import com.xmd.cashier.common.AppConstants;
import com.xmd.cashier.contract.VerifyPrizeContract;
import com.xmd.cashier.dal.bean.PrizeInfo;
import com.xmd.cashier.presenter.VerifyPrizePresenter;

/**
 * Created by zr on 16-12-12.
 * 核销奖品
 */

public class VerifyPrizeActivity extends BaseActivity implements VerifyPrizeContract.View {
    private VerifyPrizeContract.Presenter mPresenter;

    private TextView mVerifyCode;
    private TextView mActType;
    private TextView mActSubTitle;
    private TextView mPrizeName;
    private WebView mDescription;
    private TextView mDescriptionNull;

    private String mCode;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_prize);
        mPresenter = new VerifyPrizePresenter(this, this);
        PrizeInfo info = getIntent().getParcelableExtra(AppConstants.EXTRA_PRIZE_VERIFY_INFO);
        if (info == null) {
            showToast("无效的核销信息");
            finishSelf();
            return;
        }

        initView();
        showPrizeInfo(info);

        mPresenter.onCreate();
    }

    private void initView() {
        showToolbar(R.id.toolbar, R.string.verify_prize_title);
        mVerifyCode = (TextView) findViewById(R.id.tv_verify_code);
        mActType = (TextView) findViewById(R.id.tv_act_type);
        mActSubTitle = (TextView) findViewById(R.id.tv_act_sub_title);
        mPrizeName = (TextView) findViewById(R.id.tv_prize_name);
        mDescription = (WebView) findViewById(R.id.wb_description);
        mDescriptionNull = (TextView) findViewById(R.id.tv_description_null);
    }

    private void showPrizeInfo(PrizeInfo info) {
        setCode(info.verifyCode);
        mVerifyCode.setText(info.verifyCode);
        //mActType.setText(info.activityName);
        mActSubTitle.setText(info.activityName);
        mPrizeName.setText(String.format("奖品名称：%s", info.prizeName));
        if (!TextUtils.isEmpty(info.description)) {
            mDescription.loadDataWithBaseURL(null, info.description, "text/html", "utf-8", null);
            mDescription.setVisibility(View.VISIBLE);
        } else {
            mDescription.setVisibility(View.GONE);
            mDescriptionNull.setVisibility(View.VISIBLE);
        }
    }

    public void onClickVerify(View view) {
        mPresenter.onClickVerify();
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
    public void setPresenter(VerifyPrizeContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void finishSelf() {
        finish();
    }
}
