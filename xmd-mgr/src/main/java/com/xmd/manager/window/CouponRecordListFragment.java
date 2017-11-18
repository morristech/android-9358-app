package com.xmd.manager.window;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xmd.manager.R;
import com.xmd.manager.SharedPreferenceHelper;
import com.xmd.manager.beans.CouponRecordBean;
import com.xmd.manager.common.DateUtil;
import com.xmd.manager.msgctrl.MsgDef;
import com.xmd.manager.msgctrl.MsgDispatcher;
import com.xmd.manager.msgctrl.RxBus;
import com.xmd.manager.service.RequestConstant;
import com.xmd.manager.service.response.CouponRecordResult;
import com.xmd.manager.widget.EmptyView;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import rx.Subscription;

/**
 * Created by Lhj on 17-10-31.
 */

public class CouponRecordListFragment extends BaseListFragment<CouponRecordBean> {

    @BindView(R.id.empty_view)
    EmptyView emptyView;
    Unbinder unbinder;

    private Map<String, String> mParams;
    private String mCouponId;
    private String mStartTime;
    private String mEndTime;
    private String mStatus;
    private String mTimeType;
    private String mPhoneNumOrCouponNo;

    public static String KEY_INTENT_COUPON_RECORD = "intentCoupon";
    private Subscription mCouponRecordSubscription;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mCouponRecordSubscription = RxBus.getInstance().toObservable(CouponRecordResult.class).subscribe(
                result -> handleCouponRecordResult(result));
        View view = inflater.inflate(R.layout.fragment_coupon_record_list, container, false);
        unbinder = ButterKnife.bind(this, view);
        mCouponId = (String) getArguments().get(RequestConstant.KEY_COUPON_ID);
        mStartTime = (String) getArguments().get(RequestConstant.KEY_START_DATE);
        mEndTime = (String) getArguments().get(RequestConstant.KEY_END_DATE);
        mStatus = (String) getArguments().get(RequestConstant.KEY_COUPON_STATUS);
        mTimeType = (String) getArguments().get(RequestConstant.KEY_COUPON_TIME_TYPE);
        return view;
    }

    private void handleCouponRecordResult(CouponRecordResult result) {
        if (result.isSearch) {
            return;
        }
        if (result.statusCode == 200 && result.respData != null) {
            emptyView.setStatus(EmptyView.Status.Gone);
            onGetListSucceeded(result.pageCount, result.respData);
        } else {
            emptyView.setStatus(EmptyView.Status.Failed);
            onGetListFailed(result.msg);
        }
    }

    @Override
    protected void dispatchRequest() {
        if (mParams == null) {
            mParams = new HashMap<>();
        } else {
            mParams.clear();
        }
        mParams.put(RequestConstant.KEY_PAGE, String.valueOf(mPages));
        mParams.put(RequestConstant.KEY_PAGE_SIZE, String.valueOf(PAGE_SIZE));
        mParams.put(RequestConstant.KEY_COUPON_ID, TextUtils.isEmpty(mCouponId) ? "" : mCouponId);
        mParams.put(RequestConstant.KEY_COUPON_START_TIME, TextUtils.isEmpty(mStartTime) ? SharedPreferenceHelper.getCurrentClubCreateTime() : mStartTime);
        mParams.put(RequestConstant.KEY_COUPON_END_TIME, TextUtils.isEmpty(mEndTime) ? DateUtil.getCurrentDate() : mEndTime);
        mParams.put(RequestConstant.KEY_COUPON_STATUS, TextUtils.isEmpty(mStatus) ? "" : mStatus);
        mParams.put(RequestConstant.KEY_COUPON_TIME_TYPE, TextUtils.isEmpty(mTimeType) ? "" : mTimeType);
        mParams.put(RequestConstant.KEY_COUPON_PHONE_NUM_OR_COUPON_NO, TextUtils.isEmpty(mPhoneNumOrCouponNo) ? "" : mPhoneNumOrCouponNo);
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_COUPON_RECORD_DATA, mParams);
    }

    @Override
    protected void initView() {
        emptyView.setStatus(EmptyView.Status.Loading);
    }

    public void notifyDataChangeRefresh(String couponId, String startTime, String endTime, String status, String timeType, String mPhoneNumOrCouponNo) {
        this.mCouponId = couponId;
        this.mStartTime = startTime;
        this.mEndTime = endTime;
        this.mStatus = status;
        this.mTimeType = timeType;
        this.mPhoneNumOrCouponNo = mPhoneNumOrCouponNo;
        mSwipeRefreshLayout.setRefreshing(true);
        onRefresh();
    }

    @Override
    public void onItemClicked(CouponRecordBean bean) {
        super.onItemClicked(bean);
        Intent intent = new Intent(getActivity(), CouponReceiveAndUseDetailActivity.class);
        intent.putExtra(KEY_INTENT_COUPON_RECORD, bean);
        startActivity(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        RxBus.getInstance().unsubscribe(mCouponRecordSubscription);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
