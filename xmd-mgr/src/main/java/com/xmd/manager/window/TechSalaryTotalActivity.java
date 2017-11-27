package com.xmd.manager.window;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xmd.manager.R;
import com.xmd.manager.adapter.TechCommissionAmountAdapter;
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
import rx.Subscription;

/**
 * Created by zr on 17-11-21.
 * 某天所有技师提成列表
 */

public class TechSalaryTotalActivity extends BaseActivity {
    public static final String EXTRA_CURRENT_DATE = "current_date";
    private static final String EVENT_TYPE = "TechSalaryTotalActivity";

    @BindView(R.id.ev_empty)
    EmptyView mEmptyView;

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

    @BindView(R.id.layout_data)
    LinearLayout mDataLayout;
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

    private boolean mServiceSort;
    private boolean mSaleSort;
    private boolean mTotalSort;

    private String mCurrentDate;
    private TechCommissionAmountAdapter mAdapter;
    private Subscription mGetCommissionSumAmountSubscription;
    private Subscription mGetAllTechCommissionListSubscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tech_salary_total);
        mCurrentDate = getIntent().getStringExtra(EXTRA_CURRENT_DATE);
        ButterKnife.bind(this);

        setTitle(mCurrentDate);

        mEmptyView.setVisibility(View.VISIBLE);
        mEmptyView.setStatus(EmptyView.Status.Loading);

        mTotalTitle.setText(ResourceUtils.getString(R.string.report_sum_title));
        mServiceTitle.setText(ResourceUtils.getString(R.string.report_service_title));
        mSaleTitle.setText(ResourceUtils.getString(R.string.report_sales_title));

        mSalaryDayList.setLayoutManager(new LinearLayoutManager(this));
        mSalaryDayList.setHasFixedSize(true);
        mSalaryDayList.addItemDecoration(new CustomRecycleViewDecoration(1));
        mAdapter = new TechCommissionAmountAdapter(this);
        mAdapter.setCallBack(info -> {
            Intent intent = new Intent(TechSalaryTotalActivity.this, TechSalaryDetailActivity.class);
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

        Map<String, String> mParams = new HashMap<>();
        mParams.put(RequestConstant.KEY_START_DATE, mCurrentDate);
        mParams.put(RequestConstant.KEY_END_DATE, mCurrentDate);
        mParams.put(RequestConstant.KEY_EVENT_TYPE, EVENT_TYPE);
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET_COMMISSION_SUM_AMOUNT, mParams);
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET_ALL_TECH_COMMISSION_LIST, mParams);
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

    private void handleAmountListResult(CommissionAmountListResult result) {
        if (EVENT_TYPE.equals(result.eventType)) {
            mEmptyView.setVisibility(View.GONE);
            if (result.statusCode == 200) {
                mDataLayout.setVisibility(View.VISIBLE);
                if (result.respData != null && !result.respData.isEmpty()) {
                    mSalaryDayList.setVisibility(View.VISIBLE);
                    mEmptyDesc.setVisibility(View.GONE);
                    mAdapter.setData(result.respData);
                } else {
                    mSalaryDayList.setVisibility(View.GONE);
                    mEmptyDesc.setVisibility(View.VISIBLE);
                }
            } else {
                mDataLayout.setVisibility(View.GONE);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RxBus.getInstance().unsubscribe(mGetAllTechCommissionListSubscription);
        RxBus.getInstance().unsubscribe(mGetCommissionSumAmountSubscription);
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
