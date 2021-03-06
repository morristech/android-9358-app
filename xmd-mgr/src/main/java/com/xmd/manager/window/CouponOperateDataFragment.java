package com.xmd.manager.window;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xmd.manager.Constant;
import com.xmd.manager.R;
import com.xmd.manager.SharedPreferenceHelper;
import com.xmd.manager.beans.CouponBean;
import com.xmd.manager.beans.CouponStatisticsBean;
import com.xmd.manager.common.DateUtil;
import com.xmd.manager.msgctrl.MsgDef;
import com.xmd.manager.msgctrl.MsgDispatcher;
import com.xmd.manager.msgctrl.RxBus;
import com.xmd.manager.service.RequestConstant;
import com.xmd.manager.service.response.CouponOperateDataListResult;

import java.util.HashMap;
import java.util.Map;

import rx.Subscription;

/**
 * Created by Lhj on 17-10-17.
 */

public class CouponOperateDataFragment extends BaseListFragment<CouponStatisticsBean> {

    public String mFilterStartTime;
    public String mFilterEndTime;
    public Map<String, String> mParams;
    public Subscription mCouponOperateListDataSubscribe;
    public CouponBean mCouponBean;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mCouponBean = getArguments().getParcelable(Constant.KEY_INTENT_COUPON_BEAN);
        return inflater.inflate(R.layout.fragment_coupon_operate, container, false);
    }

    @Override
    protected void dispatchRequest() {
        if (mParams == null) {
            mParams = new HashMap<>();
        } else {
            mParams.clear();
        }
        mParams.clear();
        mParams.put(RequestConstant.KEY_PAGE, String.valueOf(mPages));
        mParams.put(RequestConstant.KEY_PAGE_SIZE, String.valueOf(PAGE_SIZE));
        mParams.put(RequestConstant.KEY_COUPON_ID, mCouponBean == null ? "" : mCouponBean.actId);
        mParams.put(RequestConstant.KEY_COUPON_START_DATE, TextUtils.isEmpty(mFilterStartTime) ? SharedPreferenceHelper.getCurrentClubCreateTime() : mFilterStartTime);
        mParams.put(RequestConstant.KEY_COUPON_END_DATE, TextUtils.isEmpty(mFilterEndTime) ? DateUtil.getCurrentDate() : mFilterEndTime);
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_COUPON_OPERATE_LIST_DATA, mParams);
    }

    @Override
    protected void initView() {
        mCouponOperateListDataSubscribe = RxBus.getInstance().toObservable(CouponOperateDataListResult.class).subscribe(
                result -> handleOperateDataList(result)
        );
    }

    private void handleOperateDataList(CouponOperateDataListResult result) {
        if (result.statusCode == 200) {
            onGetListSucceeded(result.pageCount, result.respData);
        } else {
            onGetListFailed(result.msg);
        }
    }

    @Override
    public boolean isPaged() {
        return true;
    }

    public void notifyDataRefresh(CouponBean couponBean, String startTime, String endTime) {
        this.mCouponBean = couponBean;
        this.mFilterStartTime = startTime;
        this.mFilterEndTime = endTime;
        onRefresh();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        RxBus.getInstance().unsubscribe(mCouponOperateListDataSubscribe);
    }

    @Override
    public void onPositiveButtonClicked(CouponStatisticsBean bean) {
        //getTotal
        CouponRecordActivity.startCouponRecordActivity(getActivity(), mCouponBean, bean.reportDate, bean.reportDate, Constant.COUPON_STATUS_CAN_USE, Constant.COUPON_TIME_TYPE_GET_TIME);
    }

    @Override
    public void onNegativeButtonClicked(CouponStatisticsBean bean) {
        //haveUseTotal
        CouponRecordActivity.startCouponRecordActivity(getActivity(), mCouponBean, bean.reportDate, bean.reportDate, Constant.COUPON_STATUS_VERIFIED, Constant.COUPON_TIME_TYPE_VERIFY_TIME);
    }
}
