package com.xmd.manager.window;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.shidou.commonlibrary.helper.XLogger;
import com.shidou.commonlibrary.widget.XToast;
import com.xmd.manager.Constant;
import com.xmd.manager.R;
import com.xmd.manager.beans.SummaryDataBean;
import com.xmd.manager.beans.TechBadComment;
import com.xmd.manager.common.DateUtil;
import com.xmd.manager.msgctrl.MsgDef;
import com.xmd.manager.msgctrl.MsgDispatcher;
import com.xmd.manager.msgctrl.RxBus;
import com.xmd.manager.service.RequestConstant;
import com.xmd.manager.service.response.OnlinePayListResult;
import com.xmd.manager.service.response.OrderManageResult;
import com.xmd.manager.service.response.OrderSummaryResult;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import rx.Subscription;

/**
 * Created by Lhj on 17-9-18.
 */

public class SummaryDataFragment extends BaseFragment {

    @BindView(R.id.tv_order_total)
    TextView tvOrderTotal;
    @BindView(R.id.tv_untreated_orders)
    TextView tvUntreatedOrders;
    @BindView(R.id.tv_refused_orders)
    TextView tvRefusedOrders;
    @BindView(R.id.tv_overtime_orders)
    TextView tvOvertimeOrders;
    @BindView(R.id.tv_accept_orders)
    TextView tvAcceptOrders;
    @BindView(R.id.tv_completed_orders)
    TextView tvCompletedOrders;
    @BindView(R.id.tv_nullity_orders)
    TextView tvNullityOrders;
    Unbinder unbinder;


    private Map<String, String> mParams;
    private String mStartTime;
    private String mEndTime;
    private Map<String, String> mDataMap;
    private Map<String, List<SummaryDataBean>> mSummaryDataMap;
    private Subscription mGetOrderDetailDataSubscription;
    private Subscription mOrderManagerSubscription;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_summary_data, container, false);
        unbinder = ButterKnife.bind(this, view);
        mParams = new HashMap<>();
        mDataMap = new HashMap<>();
        mSummaryDataMap = new HashMap<>();
        return view;
    }

    @Override
    protected void initView() {
        mStartTime = mArguments.getString(RequestConstant.KEY_START_DATE);
        mEndTime = mArguments.getString(RequestConstant.KEY_END_DATE);
        mGetOrderDetailDataSubscription = RxBus.getInstance().toObservable(OrderSummaryResult.class).subscribe(
                result -> handlerOrderSummaryData(result)
        );
        mOrderManagerSubscription = RxBus.getInstance().toObservable(OrderManageResult.class).subscribe(
                result -> handlerOrderManagerResult(result)
        );
    }

    @Override
    public void onResume() {
        super.onResume();
        XLogger.i(">>>", "resume");
        onRefreshDate(TextUtils.isEmpty(mStartTime) ? DateUtil.getFirstDayOfMonth() : mStartTime, TextUtils.isEmpty(mEndTime) ? DateUtil.getCurrentDate() : mEndTime,true);
    }


    private void handlerOrderSummaryData(OrderSummaryResult result) {
        ((ReserveDataActivity) getActivity()).hideLoading();
        if (result.statusCode == 200) {
            if (mSummaryDataMap != null) {
                mSummaryDataMap.put(mStartTime + mEndTime, result.respData);
            }
            getDataSuccess(result.respData);
        } else {
            XToast.show(result.msg);
        }
    }

    private void handlerOrderManagerResult(OrderManageResult result) {
        if (result.isSuccessful) {
            mParams.clear();
            mParams.put(RequestConstant.KEY_START_DATE, mStartTime);
            mParams.put(RequestConstant.KEY_END_DATE, mEndTime);
            MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET_SUMMARY_ORDER_DATA, mParams);
        }
    }

    public void onRefreshDate(String startDate, String endDate,boolean refresh) {
        mStartTime = startDate;
        mEndTime = endDate;

        if (mSummaryDataMap.containsKey(mStartTime + mEndTime) && !refresh) {
            getDataSuccess(mSummaryDataMap.get(mStartTime + mEndTime));
        } else {
            ((ReserveDataActivity) getActivity()).showLoading("正在加载...", false);
            mParams.clear();
            mParams.put(RequestConstant.KEY_START_DATE, startDate);
            mParams.put(RequestConstant.KEY_END_DATE, endDate);
            MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET_SUMMARY_ORDER_DATA, mParams);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        RxBus.getInstance().unsubscribe(mGetOrderDetailDataSubscription, mOrderManagerSubscription);

    }

    @OnClick({R.id.order_submit, R.id.order_refused, R.id.order_over_time, R.id.order_accept, R.id.order_complete, R.id.order_failure})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.order_submit:
                //待接新单
                OnlineOrderActivity.startOnlineOrderActivity(getActivity(), mStartTime, mEndTime, Constant.ORDER_STATUS_SUBMIT);
                break;
            case R.id.order_refused:
                //已拒绝
                OnlineOrderActivity.startOnlineOrderActivity(getActivity(), mStartTime, mEndTime, Constant.ORDER_STATUS_REJECTED);
                break;
            case R.id.order_over_time:
                //已超时
                OnlineOrderActivity.startOnlineOrderActivity(getActivity(), mStartTime, mEndTime, Constant.ORDER_STATUS_OVERTIME);
                break;
            case R.id.order_accept:
                //即将到店
                OnlineOrderActivity.startOnlineOrderActivity(getActivity(), mStartTime, mEndTime, Constant.ORDER_STATUS_ACCEPT);
                break;
            case R.id.order_complete:
                //已核销
                OnlineOrderActivity.startOnlineOrderActivity(getActivity(), mStartTime, mEndTime, Constant.ORDER_STATUS_COMPLETE);
                break;
            case R.id.order_failure:
                //爽约
                OnlineOrderActivity.startOnlineOrderActivity(getActivity(), mStartTime, mEndTime, Constant.ORDER_STATUS_FAILURE);
                break;
        }
    }

    public void getDataSuccess(List<SummaryDataBean> mSummaryDate) {
        mDataMap.clear();
        if (mSummaryDate == null || mSummaryDate.size() == 0) {
            return;
        }
        for (int i = 0; i < mSummaryDate.size(); i++) {
            mDataMap.put(mSummaryDate.get(i).key, mSummaryDate.get(i).value);
        }
        tvOrderTotal.setText(mDataMap.get("全部订单"));
        tvUntreatedOrders.setText(mDataMap.get("待接受订单"));
        tvRefusedOrders.setText(mDataMap.get("拒绝订单"));
        tvOvertimeOrders.setText(mDataMap.get("超时订单"));
        tvAcceptOrders.setText(mDataMap.get("已接受订单"));
        tvCompletedOrders.setText(mDataMap.get("完成订单"));
        tvNullityOrders.setText(mDataMap.get("失效订单"));
    }
}
