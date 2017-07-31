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
import com.xmd.cashier.contract.DiscountCouponContract;
import com.xmd.cashier.dal.bean.PayCouponInfo;
import com.xmd.cashier.presenter.DiscountCouponPresenter;

/**
 * Created by zr on 17-7-27.
 */

public class DiscountCouponActivity extends BaseActivity implements DiscountCouponContract.View {
    private DiscountCouponContract.Presenter mPresenter;

    private TextView mCouponName;
    private TextView mCouponDescription;
    private TextView mCouponNo;
    private TextView mCouponCode;
    private TextView mCouponType;
    private TextView mServiceItems;
    private TextView mUseTime;
    private WebView mActContent;
    private TextView mActContentNull;
    private Button mBtnReturn;

    private String mCode;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discount_coupon);
        mCode = getIntent().getStringExtra(AppConstants.EXTRA_COUPON_CODE);
        mPresenter = new DiscountCouponPresenter(this, this);
        initView();
        mPresenter.onCreate();
    }

    private void initView() {
        mCouponName = (TextView) findViewById(R.id.tv_discount_coupon_name);
        mCouponDescription = (TextView) findViewById(R.id.tv_coupon_description);
        mCouponNo = (TextView) findViewById(R.id.tv_coupon_no);
        mCouponCode = (TextView) findViewById(R.id.tv_coupon_code);
        mCouponType = (TextView) findViewById(R.id.tv_coupon_type);
        mServiceItems = (TextView) findViewById(R.id.tv_coupon_service);
        mUseTime = (TextView) findViewById(R.id.tv_coupon_use_time);
        mActContent = (WebView) findViewById(R.id.wb_act_content);
        mActContentNull = (TextView) findViewById(R.id.tv_act_content_null);
        mBtnReturn = (Button) findViewById(R.id.btn_return);

        mBtnReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void showCouponInfo(PayCouponInfo info) {
        mCouponName.setText(info.actTitle);
        mCouponDescription.setText(info.consumeMoneyDescription);
        mCouponNo.setText(info.businessNo);
        mCouponCode.setText(info.couponNo);
        mCouponType.setText(info.couponTypeName);
        mUseTime.setText(info.couponPeriod);
        if (TextUtils.isEmpty(info.actContent)) {
            mActContent.setVisibility(View.GONE);
            mActContentNull.setVisibility(View.VISIBLE);
        } else {
            mActContent.setVisibility(View.VISIBLE);
            mActContentNull.setVisibility(View.GONE);
            mActContent.loadDataWithBaseURL(null, info.actContent, "text/html", "utf-8", null);
        }
        if (info.items != null && !info.items.isEmpty()) {
            StringBuilder builder = new StringBuilder();
            for (PayCouponInfo.ServiceItem item : info.items) {
                builder.append(item.name).append("，");
            }
            builder.setLength(builder.length() - 1);
            mServiceItems.setText(builder.toString());
        } else {
            mServiceItems.setText("未指定");
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
    public void setPresenter(DiscountCouponContract.Presenter presenter) {
        this.mPresenter = presenter;
    }

    @Override
    public void finishSelf() {
        finish();
    }

    @Override
    public String getCode() {
        return mCode;
    }
}
