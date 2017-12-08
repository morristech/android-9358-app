package com.xmd.manager.window;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xmd.manager.R;
import com.xmd.manager.adapter.ReportNormalAdapter;
import com.xmd.manager.beans.CommissionNormalInfo;
import com.xmd.manager.common.DateUtil;
import com.xmd.manager.common.ResourceUtils;
import com.xmd.manager.common.Utils;
import com.xmd.manager.msgctrl.MsgDef;
import com.xmd.manager.msgctrl.MsgDispatcher;
import com.xmd.manager.msgctrl.RxBus;
import com.xmd.manager.service.RequestConstant;
import com.xmd.manager.service.response.CommissionAmountResult;
import com.xmd.manager.service.response.CommissionNormalListResult;
import com.xmd.manager.widget.CustomRecycleViewDecoration;
import com.xmd.manager.widget.EmptyView;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import rx.Subscription;

/**
 * Created by zr on 17-11-21.
 * 技师工资报表按月查看
 */

public class SalaryReportByMonthFragment extends BaseFragment {
    private static final String FORMAT = "yyyy-MM-dd";
    private static final String EVENT_TYPE = "SalaryReportByMonthFragment";
    public static final String TYPE_SERVICE = "serviceCommission";
    public static final String TYPE_SALES = "salesCommission";
    public static final String TYPE_ALL = TYPE_SERVICE + "," + TYPE_SALES;

    private boolean mServiceSelected;
    private boolean mSaleSelected;

    @BindView(R.id.tv_show_time)
    TextView mCurrentTime;

    @BindView(R.id.ev_empty)
    EmptyView mEmptyView;

    @BindView(R.id.layout_amount)
    LinearLayout mAmountLayout;
    @BindView(R.id.layout_left_data)
    LinearLayout mServiceLayout;
    @BindView(R.id.tv_left_title)
    TextView mServiceTitle;
    @BindView(R.id.tv_left_content)
    TextView mServiceAmount;
    @BindView(R.id.layout_right_data)
    LinearLayout mSaleLayout;
    @BindView(R.id.tv_right_title)
    TextView mSaleTitle;
    @BindView(R.id.tv_right_content)
    TextView mSaleAmount;
    @BindView(R.id.tv_total_title)
    TextView mTotalTitle;
    @BindView(R.id.tv_total_content)
    TextView mTotalAmount;

    @BindView(R.id.rv_salary_month_data)
    RecyclerView mSalaryMonthList;

    private View view;
    private Unbinder unbinder;

    private boolean isInit;
    private boolean isLoad;

    private String mStartDate;
    private String mEndDate;
    private String mFilterType;

    private ReportNormalAdapter<CommissionNormalInfo> mAdapter;
    private Subscription mGetCommissionSumAmountSubscription;
    private Subscription mGetCustomDateSumCommissionSubscription;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_salary_report_month, container, false);
        unbinder = ButterKnife.bind(this, view);
        isInit = true;
        return view;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        initData();
    }

    @Override
    protected void initView() {
        String current = DateUtil.getCurrentDate();
        mStartDate = DateUtil.getFirstDayOfMonth(current, FORMAT);
        mEndDate = DateUtil.getLastDayOfMonth(current, FORMAT);
        mCurrentTime.setText(mStartDate.substring(0, 7));

        mEmptyView.setVisibility(View.VISIBLE);
        mEmptyView.setStatus(EmptyView.Status.Loading);

        mTotalTitle.setText(ResourceUtils.getString(R.string.report_salary_sum_title));
        mServiceTitle.setText(ResourceUtils.getString(R.string.report_service_title));
        mSaleTitle.setText(ResourceUtils.getString(R.string.report_sales_title));

        mSalaryMonthList.setLayoutManager(new LinearLayoutManager(getActivity()));
        mSalaryMonthList.setHasFixedSize(true);
        mSalaryMonthList.addItemDecoration(new CustomRecycleViewDecoration(1));
        mAdapter = new ReportNormalAdapter<>(getActivity());
        mAdapter.setCallBack(date -> {
            Intent intent = new Intent(getActivity(), TechSalaryTotalActivity.class);
            intent.putExtra(TechSalaryTotalActivity.EXTRA_CURRENT_DATE, date);
            startActivity(intent);
        });
        mSalaryMonthList.setAdapter(mAdapter);

        mGetCommissionSumAmountSubscription = RxBus.getInstance().toObservable(CommissionAmountResult.class).subscribe(
                commissionAmountResult -> handleAmountResult(commissionAmountResult));
        mGetCustomDateSumCommissionSubscription = RxBus.getInstance().toObservable(CommissionNormalListResult.class).subscribe(
                result -> handleNormalListResult(result)
        );

        initData();
    }

    private void initData() {
        if (!isInit) {
            return;
        }
        if (getUserVisibleHint() && !isLoad) {
            isLoad = true;
            dispatchRequest();
        }
    }

    private void dispatchRequest() {
        requestAmount();
        requestList();
    }

    private void requestAmount() {
        Map<String, String> mParams = new HashMap<>();
        mParams.put(RequestConstant.KEY_START_DATE, mStartDate);
        mParams.put(RequestConstant.KEY_END_DATE, mEndDate);
        mParams.put(RequestConstant.KEY_EVENT_TYPE, EVENT_TYPE);
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET_COMMISSION_SUM_AMOUNT, mParams);
    }

    private void requestList() {
        Map<String, String> mParams = new HashMap<>();
        mParams.put(RequestConstant.KEY_START_DATE, mStartDate);
        mParams.put(RequestConstant.KEY_END_DATE, mEndDate);
        mParams.put(RequestConstant.KEY_TYPE, mFilterType);
        mParams.put(RequestConstant.KEY_EVENT_TYPE, EVENT_TYPE);
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET_COMMISSION_SUM_LIST, mParams);
    }

    private void handleAmountResult(CommissionAmountResult result) {
        if (EVENT_TYPE.equals(result.eventType)) {
            mEmptyView.setVisibility(View.GONE);
            if (result.statusCode == 200) {
                mAmountLayout.setVisibility(View.VISIBLE);
                mTotalAmount.setText(Utils.moneyToStringEx(result.respData.getTotalCommission()));
                mServiceAmount.setText(Utils.moneyToStringEx(result.respData.serviceCommission));
                mSaleAmount.setText(Utils.moneyToStringEx(result.respData.salesCommission));
            } else {
                mAmountLayout.setVisibility(View.GONE);
            }
        }
    }

    private void handleNormalListResult(CommissionNormalListResult result) {
        if (EVENT_TYPE.equals(result.eventType)) {
            mEmptyView.setVisibility(View.GONE);
            mSalaryMonthList.removeAllViews();
            mAdapter.clearData();
            if (result.statusCode == 200) {
                mAdapter.setData(result.respData);
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        isInit = false;
        isLoad = false;
        unbinder.unbind();
        RxBus.getInstance().unsubscribe(mGetCommissionSumAmountSubscription);
        RxBus.getInstance().unsubscribe(mGetCustomDateSumCommissionSubscription);
    }

    @OnClick({R.id.tv_reduce_time, R.id.tv_add_time})
    public void onTimeChange(View view) {
        switch (view.getId()) {
            case R.id.tv_reduce_time:
                mStartDate = DateUtil.getFirstDayOfLastMonth(mStartDate, FORMAT);
                mEndDate = DateUtil.getLastDayOfMonth(mStartDate, FORMAT);
                mCurrentTime.setText(mStartDate.substring(0, 7));
                break;
            case R.id.tv_add_time:
                mStartDate = DateUtil.getFirstDayOfNextMonth(mStartDate, FORMAT);
                mEndDate = DateUtil.getLastDayOfMonth(mStartDate, FORMAT);
                mCurrentTime.setText(mStartDate.substring(0, 7));
                break;
            default:
                break;
        }
        mAdapter.clearData();
        mSalaryMonthList.removeAllViews();
        mSaleSelected = false;
        mServiceSelected = false;
        updateServiceLayout(mServiceSelected);
        updateSaleLayout(mSaleSelected);
        mEmptyView.setVisibility(View.VISIBLE);
        mEmptyView.setStatus(EmptyView.Status.Loading);
        mFilterType = TYPE_ALL;
        dispatchRequest();
    }

    @OnClick({R.id.layout_left_data, R.id.layout_right_data})
    public void onLayoutClick(View view) {
        switch (view.getId()) {
            case R.id.layout_left_data:
                mServiceSelected = !mServiceSelected;
                if (mServiceSelected) {
                    mSaleSelected = !mServiceSelected;
                    mFilterType = TYPE_SERVICE;
                } else {
                    mFilterType = TYPE_ALL;
                }
                break;
            case R.id.layout_right_data:
                mSaleSelected = !mSaleSelected;
                if (mSaleSelected) {
                    mServiceSelected = !mSaleSelected;
                    mFilterType = TYPE_SALES;
                } else {
                    mFilterType = TYPE_ALL;
                }
                break;
            default:
                break;
        }

        updateServiceLayout(mServiceSelected);
        updateSaleLayout(mSaleSelected);
        requestList();
    }


    private void updateServiceLayout(boolean selected) {
        if (selected) {
            mServiceLayout.setBackgroundResource(R.drawable.bg_report_select);
            mServiceAmount.setTextColor(ResourceUtils.getColor(R.color.colorStatusYellow));
            mServiceTitle.setTextColor(ResourceUtils.getColor(R.color.colorText5));
        } else {
            mServiceLayout.setBackgroundResource(R.color.colorWhite);
            mServiceAmount.setTextColor(ResourceUtils.getColor(R.color.colorBlue));
            mServiceTitle.setTextColor(ResourceUtils.getColor(R.color.colorText3));
        }
    }

    private void updateSaleLayout(boolean selected) {
        if (selected) {
            mSaleLayout.setBackgroundResource(R.drawable.bg_report_select);
            mSaleAmount.setTextColor(ResourceUtils.getColor(R.color.colorStatusYellow));
            mSaleTitle.setTextColor(ResourceUtils.getColor(R.color.colorText5));
        } else {
            mSaleLayout.setBackgroundResource(R.color.colorWhite);
            mSaleAmount.setTextColor(ResourceUtils.getColor(R.color.colorBlue));
            mSaleTitle.setTextColor(ResourceUtils.getColor(R.color.colorText3));
        }
    }
}
