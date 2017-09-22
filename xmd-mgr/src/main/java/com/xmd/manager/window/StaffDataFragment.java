package com.xmd.manager.window;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xmd.manager.R;
import com.xmd.manager.beans.StaffDataBean;
import com.xmd.manager.msgctrl.MsgDef;
import com.xmd.manager.msgctrl.MsgDispatcher;
import com.xmd.manager.msgctrl.RxBus;
import com.xmd.manager.service.RequestConstant;
import com.xmd.manager.service.response.StaffDataResult;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import rx.Subscription;

/**
 * Created by Lhj on 17-9-18.
 */

public class StaffDataFragment extends BaseListFragment<StaffDataBean> {

    @BindView(R.id.tv_empty_view)
    TextView tvEmptyView;

    Unbinder unbinder;
    private Map<String, String> mParams;
    private String mStartDate;
    private String mEndDate;
    private Subscription mStaffDataSubscription;
    private Map<String, List<StaffDataBean>> mStaffDataListMap;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_staff_data, container, false);
        unbinder = ButterKnife.bind(this, view);
        initData();
        return view;
    }

    private void initData() {
        mStartDate = getArguments().getString(RequestConstant.KEY_START_DATE);
        mEndDate = getArguments().getString(RequestConstant.KEY_END_DATE);

    }

    @Override
    protected void initView() {

        mStaffDataListMap = new HashMap<>();
        mStaffDataSubscription = RxBus.getInstance().toObservable(StaffDataResult.class).subscribe(
                result -> handlerStaffDateResult(result)
        );
    }


    public void setDateChanged(String startDate, String endDate) {
        mStartDate = startDate;
        mEndDate = endDate;
        if (mStaffDataListMap != null && mStaffDataListMap.containsKey(startDate + endDate)) {
            if (mStaffDataListMap.get(startDate + endDate).size() == 0) {
                tvEmptyView.setVisibility(View.VISIBLE);
                mSwipeRefreshLayout.setVisibility(View.GONE);
            } else {
                tvEmptyView.setVisibility(View.GONE);
                mSwipeRefreshLayout.setVisibility(View.VISIBLE);
                onGetListSucceeded(1, mStaffDataListMap.get(startDate + endDate));
            }

        } else {
            onRefresh();
        }

    }

    private void handlerStaffDateResult(StaffDataResult result) {
        if (result.statusCode == 200) {
            if (result.respData.size() > 0) {
                mSwipeRefreshLayout.setVisibility(View.VISIBLE);
                tvEmptyView.setVisibility(View.GONE);
                onGetListSucceeded(result.pageCount, result.respData);
            } else {
                mSwipeRefreshLayout.setRefreshing(false);
                mSwipeRefreshLayout.setVisibility(View.GONE);
                tvEmptyView.setVisibility(View.VISIBLE);
            }
            mStaffDataListMap.put(mStartDate + mEndDate, result.respData);
        } else {
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
        mSwipeRefreshLayout.setRefreshing(true);
        mParams.put(RequestConstant.KEY_START_DATE, mStartDate);
        mParams.put(RequestConstant.KEY_END_DATE, mEndDate);
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET_TECH_ORDER_RANK, mParams);
    }

    @Override
    public boolean isPaged() {
        return false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        RxBus.getInstance().unsubscribe(mStaffDataSubscription);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
