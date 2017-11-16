package com.xmd.manager.window;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.xmd.manager.Constant;
import com.xmd.manager.R;
import com.xmd.manager.beans.CouponBean;
import com.xmd.manager.common.ResourceUtils;
import com.xmd.manager.event.CouponRecordFilterEvent;
import com.xmd.manager.service.RequestConstant;

import org.greenrobot.eventbus.Subscribe;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Lhj on 17-10-31.
 */

public class CouponRecordActivity extends BaseActivity {
    @BindView(R.id.tv_coupon_title)
    TextView tvCouponTitle;
    @BindView(R.id.tv_coupon_time)
    TextView tvCouponTime;
    @BindView(R.id.tv_coupon_status)
    TextView tvCouponStatus;

    private CouponRecordListFragment mCouponRecordListFragment;
    private CouponBean mCouponBean;
    private String mFilterStatusType;
    private String mStartTime;
    private String mEndTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coupon_record);
        ButterKnife.bind(this);
        getIntentData();
        initView();
        initFragmentView();
    }

    public void getIntentData() {
        mCouponBean = getIntent().getParcelableExtra(CouponOperateDataActivity.KEY_INTENT_COUPON_BEAN);
        mStartTime = getIntent().getStringExtra(CouponOperateDataActivity.FILTER_COUPON_TIME_START_TIME);
        mEndTime = getIntent().getStringExtra(CouponOperateDataActivity.FILTER_COUPON_TIME_END_TIME);
        mFilterStatusType = getIntent().getStringExtra(CouponOperateDataActivity.FILTER_COUPON_STATUS_TYPE);
    }

    private void initView() {
        setTitle(ResourceUtils.getString(R.string.coupon_record_activity_title));
        setRightVisible(true, ResourceUtils.getDrawable(R.drawable.ic_record_filter), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CouponRecordActivity.this, CouponRecordFilterActivity.class));
            }
        });
        setViewData(TextUtils.isEmpty(mFilterStatusType) ? "" : mFilterStatusType);
    }

    private void initFragmentView() {
        mCouponRecordListFragment = new CouponRecordListFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(RequestConstant.KEY_COUPON_ID, mCouponBean == null ? "" : mCouponBean.actId);
        bundle.putSerializable(RequestConstant.KEY_START_DATE, mStartTime);
        bundle.putSerializable(RequestConstant.KEY_END_DATE, mEndTime);
        bundle.putSerializable(RequestConstant.KEY_COUPON_TIME_TYPE, mFilterStatusType);
        mCouponRecordListFragment.setArguments(bundle);
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.fm_coupon_record_list, mCouponRecordListFragment);
        ft.commit();
    }


    private void setViewData(String viewStatus) {
        if (mCouponBean == null) {
            tvCouponTitle.setText(ResourceUtils.getString(R.string.coupon_data_all_coupon));
        } else {
            tvCouponTitle.setText(TextUtils.isEmpty(mCouponBean.actTitle) ? "优惠券" : mCouponBean.actTitle);
        }
        tvCouponTime.setText(String.format("%s - %s", mStartTime, mEndTime));
        setViewStatus(viewStatus);
    }

    @Subscribe
    public void onCouponRecordFilter(CouponRecordFilterEvent event) {
        tvCouponTitle.setText(event.couponTitle);
        setViewStatus(event.couponStatus);
        mCouponRecordListFragment.notifyDataChangeRefresh(event.couponId, event.filterStartTime, event.filterEndTime, event.couponStatus, event.timeFilter, "");
    }


    @OnClick(R.id.ll_coupon_search)
    public void onViewClicked() {
        startActivity(new Intent(CouponRecordActivity.this, SearchCouponActivity.class));
    }

    private void setViewStatus(String viewStatus) {
        switch (viewStatus) {
            case Constant.COUPON_STATUS_ALL:
                tvCouponStatus.setText(ResourceUtils.getString(R.string.coupon_data_receive));
                break;
            case Constant.COUPON_STATUS_CAN_USE:
                tvCouponStatus.setText(ResourceUtils.getString(R.string.coupon_data_can_use));
                break;
            case Constant.COUPON_STATUS_VERIFIED:
                tvCouponStatus.setText(ResourceUtils.getString(R.string.coupon_data_verification));
                break;
            case Constant.COUPON_STATUS_EXPIRED:
                tvCouponStatus.setText(ResourceUtils.getString(R.string.coupon_data_over_time));
                break;
        }
    }

}
