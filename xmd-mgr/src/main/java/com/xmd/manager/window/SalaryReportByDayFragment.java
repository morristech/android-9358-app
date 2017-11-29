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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xmd.manager.R;
import com.xmd.manager.adapter.TechCommissionAmountAdapter;
import com.xmd.manager.common.DateUtil;
import com.xmd.manager.common.ResourceUtils;
import com.xmd.manager.common.Utils;
import com.xmd.manager.msgctrl.MsgDef;
import com.xmd.manager.msgctrl.MsgDispatcher;
import com.xmd.manager.msgctrl.RxBus;
import com.xmd.manager.service.RequestConstant;
import com.xmd.manager.service.response.CommissionAmountListResult;
import com.xmd.manager.service.response.CommissionAmountResult;
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
 * 技师工资报表按日查看
 */

public class SalaryReportByDayFragment extends BaseFragment {
    private static final String FORMAT = "yyyy-MM-dd";
    private static final String EVENT_TYPE = "SalaryReportByDayFragment";

    @BindView(R.id.tv_show_time)
    TextView mCurrentShowTime;

    @BindView(R.id.layout_amount)
    LinearLayout mAmountLayout;
    @BindView(R.id.tv_left_title)
    TextView mServiceTitle;
    @BindView(R.id.tv_left_content)
    TextView mServiceAmount;
    @BindView(R.id.tv_right_title)
    TextView mSaleTitle;
    @BindView(R.id.tv_right_content)
    TextView mSaleAmount;
    @BindView(R.id.tv_total_title)
    TextView mTotalTitle;
    @BindView(R.id.tv_total_content)
    TextView mTotalAmount;

    @BindView(R.id.ev_empty)
    EmptyView mEmptyView;

    @BindView(R.id.layout_amount_list)
    LinearLayout mAmountListLayout;
    @BindView(R.id.rl_by_service)
    RelativeLayout mSortByService;
    @BindView(R.id.rl_by_sale)
    RelativeLayout mSortBySale;
    @BindView(R.id.rl_by_total)
    RelativeLayout mSortByTotal;
    @BindView(R.id.tv_empty_desc)
    TextView mEmptyDesc;
    @BindView(R.id.rv_salary_day_data)
    RecyclerView mSalaryDayList;

    private Unbinder unbinder;
    private View view;

    private boolean isInit; //是否已经初始化
    private boolean isLoad; //是否正在加载

    private TechCommissionAmountAdapter mAdapter;
    private String mCurrentDate;
    private Map<String, String> mParams;
    private Subscription mGetCommissionSumAmountSubscription;
    private Subscription mGetAllTechCommissionListSubscription;

    private boolean mServiceSort;
    private boolean mSaleSort;
    private boolean mTotalSort;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_salary_report_day, container, false);
        unbinder = ButterKnife.bind(this, view);
        isInit = true;
        return view;
    }

    @Override
    protected void initView() {
        mCurrentDate = DateUtil.getCurrentDate();
        mCurrentShowTime.setText(mCurrentDate);

        mEmptyView.setVisibility(View.VISIBLE);
        mEmptyView.setStatus(EmptyView.Status.Loading);

        mTotalTitle.setText(ResourceUtils.getString(R.string.report_salary_sum_title));
        mServiceTitle.setText(ResourceUtils.getString(R.string.report_service_title));
        mSaleTitle.setText(ResourceUtils.getString(R.string.report_sales_title));

        mSalaryDayList.setLayoutManager(new LinearLayoutManager(getActivity()));
        mSalaryDayList.addItemDecoration(new CustomRecycleViewDecoration(1));
        mSalaryDayList.setHasFixedSize(true);
        mAdapter = new TechCommissionAmountAdapter(getActivity());
        mAdapter.setCallBack(info -> {
            Intent intent = new Intent(getActivity(), TechSalaryDetailActivity.class);
            intent.putExtra(TechSalaryDetailActivity.EXTRA_TECH_FROM, TechSalaryDetailActivity.TECH_FROM_SALARY);
            intent.putExtra(TechSalaryDetailActivity.EXTRA_TECH_COMMISSION_INFO, info);
            intent.putExtra(TechSalaryDetailActivity.EXTRA_CURRENT_DATE, mCurrentDate);
            startActivity(intent);
        });
        mSalaryDayList.setAdapter(mAdapter);

        mGetCommissionSumAmountSubscription = RxBus.getInstance().toObservable(CommissionAmountResult.class).subscribe(
                commissionAmountResult -> handleAmountResult(commissionAmountResult));
        mGetAllTechCommissionListSubscription = RxBus.getInstance().toObservable(CommissionAmountListResult.class).subscribe(
                result -> handleAmountListResult(result)
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
        if (mParams == null) {
            mParams = new HashMap<>();
        } else {
            mParams.clear();
        }
        mParams.put(RequestConstant.KEY_START_DATE, mCurrentDate);
        mParams.put(RequestConstant.KEY_END_DATE, mCurrentDate);
        mParams.put(RequestConstant.KEY_EVENT_TYPE, EVENT_TYPE);
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET_COMMISSION_SUM_AMOUNT, mParams);
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET_ALL_TECH_COMMISSION_LIST, mParams);
    }

    private void handleAmountResult(CommissionAmountResult result) {
        if (EVENT_TYPE.equals(result.eventType)) {
            mEmptyView.setVisibility(View.GONE);
            isLoad = false;
            if (result.statusCode == 200) {
                mAmountLayout.setVisibility(View.VISIBLE);
                mTotalAmount.setText(Utils.moneyToStringEx(result.respData.getTotalCommission()));
                mServiceAmount.setText("￥" + Utils.moneyToStringEx(result.respData.serviceCommission));
                mSaleAmount.setText("￥" + Utils.moneyToStringEx(result.respData.salesCommission));
            } else {
                mAmountLayout.setVisibility(View.GONE);
            }
        }
    }

    private void handleAmountListResult(CommissionAmountListResult result) {
        if (EVENT_TYPE.equals(result.eventType)) {
            isLoad = false;
            mEmptyView.setVisibility(View.GONE);
            if (result.statusCode == 200) {
                mAmountListLayout.setVisibility(View.VISIBLE);
                if (result.respData != null && !result.respData.isEmpty()) {
                    mSalaryDayList.setVisibility(View.VISIBLE);
                    mEmptyDesc.setVisibility(View.GONE);
                    mAdapter.setData(result.respData);
                } else {
                    mSalaryDayList.setVisibility(View.GONE);
                    mEmptyDesc.setVisibility(View.VISIBLE);
                }
            } else {
                mAmountListLayout.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        initData();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        isLoad = false;
        isInit = false;
        unbinder.unbind();
        RxBus.getInstance().unsubscribe(mGetAllTechCommissionListSubscription);
        RxBus.getInstance().unsubscribe(mGetCommissionSumAmountSubscription);
    }

    @OnClick({R.id.tv_reduce_time, R.id.tv_add_time})
    public void onTimeChange(View view) {
        switch (view.getId()) {
            case R.id.tv_reduce_time:
                mCurrentDate = DateUtil.getLastDate(DateUtil.stringDateToLong(mCurrentDate), FORMAT);
                mCurrentShowTime.setText(mCurrentDate);
                break;
            case R.id.tv_add_time:
                mCurrentDate = DateUtil.getNextDate(DateUtil.stringDateToLong(mCurrentDate), FORMAT);
                mCurrentShowTime.setText(mCurrentDate);
                break;
            default:
                break;
        }
        mAdapter.clearData();
        mSalaryDayList.removeAllViews();
        mEmptyView.setVisibility(View.VISIBLE);
        mEmptyView.setStatus(EmptyView.Status.Loading);
        initData();
    }

    @OnClick({R.id.rl_by_service, R.id.rl_by_sale, R.id.rl_by_total})
    public void onSortClick(View view) {
        switch (view.getId()) {
            case R.id.rl_by_service:
                mServiceSort = !mServiceSort;
                mAdapter.sortByService(mServiceSort);
                break;
            case R.id.rl_by_sale:
                mSaleSort = !mSaleSort;
                mAdapter.sortBySale(mSaleSort);
                break;
            case R.id.rl_by_total:
                mTotalSort = !mTotalSort;
                mAdapter.sortByTotal(mTotalSort);
                break;
            default:
                break;
        }
    }
}
