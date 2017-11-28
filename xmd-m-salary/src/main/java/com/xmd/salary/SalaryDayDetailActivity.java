package com.xmd.salary;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.shidou.commonlibrary.widget.XToast;
import com.xmd.app.BaseActivity;
import com.xmd.app.utils.ResourceUtils;
import com.xmd.m.network.NetworkSubscriber;
import com.xmd.salary.adapter.SalaryListAdapter;
import com.xmd.salary.bean.CommissionBean;
import com.xmd.salary.bean.SalaryBean;
import com.xmd.salary.httprequest.DataManager;
import com.xmd.salary.httprequest.response.CommissionDetailResult;
import com.xmd.salary.httprequest.response.CommissionSumAmountResult;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Lhj on 17-11-22.
 */

public class SalaryDayDetailActivity extends BaseActivity {

    @BindView(R2.id.tv_tech_commission_total)
    TextView tvTechCommissionTotal;
    @BindView(R2.id.tv_tech_service_commission)
    TextView tvTechServiceCommission;
    @BindView(R2.id.ll_tech_service_commission)
    LinearLayout llTechServiceCommission;
    @BindView(R2.id.tv_tech_sell_commission)
    TextView tvTechSellCommission;
    @BindView(R2.id.ll_tech_sell_commission)
    LinearLayout llTechSellCommission;
    @BindView(R2.id.salary_detail_list)
    RecyclerView salaryDetailList;
//    @BindView(R2.id.stationary_scroll_view)
//    StationaryScrollView stationaryScrollView;

    private String mTimeDate;
    private List<CommissionBean> mCommissionBeans;
    private SalaryListAdapter mSalaryAdapter;
    private String mFilterType;
    private boolean mServiceSelected = false;
    private boolean mSellSelected = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_salary_day_detail);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        setTitle(ResourceUtils.getString(R.string.salary_detail_title));
        setBackVisible(true);
        mTimeDate = getIntent().getStringExtra(SalaryListDataFragment.INTENT_TIME);
        getSalarySumAmount(mTimeDate);
        if (mCommissionBeans == null) {
            mCommissionBeans = new ArrayList<>();
        }
        mSalaryAdapter = new SalaryListAdapter(SalaryDayDetailActivity.this, mCommissionBeans);
        mFilterType = ConstantResource.SALARY_TYPE_ALL;
        salaryDetailList.setLayoutManager(new LinearLayoutManager(SalaryDayDetailActivity.this));
     //   salaryDetailList.addItemDecoration(new DividerDecoration(RecyclerView.VERTICAL, ResourceUtils.getDrawable(R.drawable.list_item_divider)));
        salaryDetailList.setHasFixedSize(true);
        salaryDetailList.setAdapter(mSalaryAdapter);
        getCommissionDetailList(mTimeDate, mFilterType);

    }

    @OnClick(R2.id.ll_tech_sell_commission)
    public void onLlTechSellCommissionClicked() {
        mSellSelected = !mSellSelected;
        llTechSellCommission.setSelected(mSellSelected);
        getCommissionDetailList(mTimeDate, getSelectedCommissionType());
    }

    @OnClick(R2.id.ll_tech_service_commission)
    public void onLlTechServiceCommissionClicked() {
        mServiceSelected = !mServiceSelected;
        llTechServiceCommission.setSelected(mServiceSelected);
        getCommissionDetailList(mTimeDate, getSelectedCommissionType());
    }

    public String getSelectedCommissionType() {
        if (mServiceSelected && !mSellSelected) {
            return ConstantResource.SALARY_TYPE_SERVICE;
        } else if (!mServiceSelected && mSellSelected) {
            return ConstantResource.SALARY_TYPE_SELL;
        } else {
            return ConstantResource.SALARY_TYPE_ALL;
        }
    }

    public void getSalarySumAmount(String wordDate) {
        DataManager.getInstance().getTechCommissionSumAmount(wordDate, wordDate, new NetworkSubscriber<CommissionSumAmountResult>() {
            @Override
            public void onCallbackSuccess(CommissionSumAmountResult result) {
                setViewData(result.getRespData());
            }

            @Override
            public void onCallbackError(Throwable e) {
                XToast.show(e.getLocalizedMessage());
            }
        });
    }

    public void setViewData(SalaryBean bean) {
        tvTechCommissionTotal.setText(String.valueOf(String.format("%1.1f", (bean.serviceCommission + bean.salesCommission) / 100f)));
        tvTechServiceCommission.setText(String.format("￥%1.1f", bean.serviceCommission / 100f));
        tvTechSellCommission.setText(String.format("￥%1.1f", bean.salesCommission / 100f));
    }

    private void getCommissionDetailList(String workDate, String type) {

        DataManager.getInstance().getTechCommissionDetail("1", "1000", workDate,workDate, type, new NetworkSubscriber<CommissionDetailResult>() {
            @Override
            public void onCallbackSuccess(CommissionDetailResult result) {
                mSalaryAdapter.setData(result.getRespData());
            }

            @Override
            public void onCallbackError(Throwable e) {
                XToast.show(e.getLocalizedMessage());
            }
        });
    }

}
