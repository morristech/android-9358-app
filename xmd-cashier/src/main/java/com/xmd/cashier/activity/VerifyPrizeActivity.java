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
    private PrizeInfo mInfo;

    private TextView mVerifyCode;
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
        mInfo = getIntent().getParcelableExtra(AppConstants.EXTRA_PRIZE_VERIFY_INFO);
        if (mInfo == null) {
            showToast("无效的核销信息");
            finishSelf();
            return;
        }

        initView();
        mPresenter.onCreate();
    }

    private void initView() {
        showToolbar(R.id.toolbar, mInfo.activityName);
        mVerifyCode = (TextView) findViewById(R.id.tv_verify_code);
        mActSubTitle = (TextView) findViewById(R.id.tv_act_sub_title);
        mPrizeName = (TextView) findViewById(R.id.tv_prize_name);
        mDescription = (WebView) findViewById(R.id.wb_description);
        mDescriptionNull = (TextView) findViewById(R.id.tv_description_null);

        setCode(mInfo.verifyCode);
        mVerifyCode.setText(mInfo.verifyCode);
        mActSubTitle.setText(mInfo.activityName);
        mPrizeName.setText(String.format("奖品名称：%s", mInfo.prizeName));
        if (!TextUtils.isEmpty(mInfo.description)) {
            mDescription.loadDataWithBaseURL(null, mInfo.description, "text/html", "utf-8", null);
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
